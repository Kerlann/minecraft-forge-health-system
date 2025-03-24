package com.kerlann.healthsystem.event;

import com.kerlann.healthsystem.HealthSystem;
import com.kerlann.healthsystem.capability.HealthHelper;
import com.kerlann.healthsystem.capability.IHealth;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Gestionnaire d'événements liés au système de santé
 */
@Mod.EventBusSubscriber(modid = HealthSystem.MODID)
public class HealthEventHandler {

    private static final Logger LOGGER = LogManager.getLogger(HealthSystem.MODID);
    private static final Random RANDOM = new Random();
    
    // Map pour suivre le délai entre les dommages de saignement (1 entrée par joueur)
    private static final Map<UUID, Integer> bleedingTimers = new HashMap<>();
    
    // Map pour suivre le délai de régénération de santé (1 entrée par joueur)
    private static final Map<UUID, Integer> regenTimers = new HashMap<>();
    
    /**
     * Gère les événements de dommages aux entités vivantes
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        
        // Ne s'applique qu'aux joueurs
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            DamageSource source = event.getSource();
            float damage = event.getAmount();
            
            // Obtenir la capacité de santé du joueur
            IHealth health = HealthHelper.getHealth(player);
            if (health == null) return;
            
            // Gestion du saignement selon le type de dommage
            if (source.damageType.equals("player") || source.damageType.equals("mob") || 
                source.damageType.equals("arrow") || source.damageType.equals("thorns")) {
                
                // Probabilité basée sur les dégâts
                float bleedChance = damage * 0.15f; // 15% par point de dégâts
                if (RANDOM.nextFloat() < bleedChance) {
                    int bleedIncrease = damage >= 6 ? 2 : 1; // Augmentation plus importante pour les gros dégâts
                    HealthHelper.increaseBleedingLevel(player, bleedIncrease);
                }
            }
            
            // Gestion des fractures en cas de chute
            if (source.damageType.equals("fall") && damage >= 3.0f) {
                float fractureChance = (damage - 2.0f) * 0.2f; // 20% par point de dégâts au-dessus de 2
                if (RANDOM.nextFloat() < fractureChance) {
                    int fractureIncrease = damage >= 7 ? 2 : 1;
                    HealthHelper.increaseBoneBrokenLevel(player, fractureIncrease);
                }
            }
            
            // Gestion de l'empoisonnement
            if (source.damageType.equals("poison") || source.damageType.equals("magic")) {
                float poisonChance = damage * 0.1f; // 10% par point de dégâts
                if (RANDOM.nextFloat() < poisonChance) {
                    HealthHelper.increasePoisonLevel(player, 1);
                }
            }
            
            // Mettre à jour l'état critique
            HealthHelper.updateCriticalCondition(player);
        }
    }
    
    /**
     * Gère les événements de chute pour la détection des fractures
     */
    @SubscribeEvent
    public static void onEntityFall(LivingFallEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        
        // Ne s'applique qu'aux joueurs
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            float distance = event.getDistance();
            
            // Chutes importantes peuvent causer des fractures même sans dégâts
            if (distance >= 5.0f && !player.isCreative()) {
                float boneDamageChance = (distance - 3.0f) * 0.1f; // 10% par bloc au-dessus de 3
                if (RANDOM.nextFloat() < boneDamageChance) {
                    int level = distance >= 10.0f ? 2 : 1;
                    HealthHelper.increaseBoneBrokenLevel(player, level);
                }
            }
        }
    }
    
    /**
     * Modifie la guérison en fonction de l'état de santé du joueur
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        
        // Ne s'applique qu'aux joueurs
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            float amount = event.getAmount();
            
            // Obtenir la capacité de santé du joueur
            IHealth health = HealthHelper.getHealth(player);
            if (health == null) return;
            
            // Réduire la guérison si le joueur saigne
            int bleedingLevel = health.getBleedingLevel();
            if (bleedingLevel > 0) {
                float reductionFactor = 1.0f - (0.2f * bleedingLevel); // 20% de réduction par niveau de saignement
                float newAmount = amount * Math.max(0.1f, reductionFactor);
                event.setAmount(newAmount);
                LOGGER.debug("Guérison réduite pour le joueur {} due au saignement. Avant: {}, Après: {}", 
                          player.getName(), amount, newAmount);
            }
            
            // Réduire la guérison si le joueur est empoisonné
            int poisonLevel = health.getPoisonLevel();
            if (poisonLevel > 0) {
                float reductionFactor = 1.0f - (0.3f * poisonLevel); // 30% de réduction par niveau d'empoisonnement
                float newAmount = event.getAmount() * Math.max(0.1f, reductionFactor);
                event.setAmount(newAmount);
                LOGGER.debug("Guérison réduite pour le joueur {} due à l'empoisonnement. Avant: {}, Après: {}", 
                          player.getName(), event.getAmount(), newAmount);
            }
        }
    }
    
    /**
     * Gère le tick des joueurs pour les effets périodiques
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Ne procéder qu'à la fin de la phase du tick et côté serveur
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
            return;
        }
        
        EntityPlayer player = event.player;
        UUID playerID = player.getUniqueID();
        
        // Obtenir la capacité de santé du joueur
        IHealth health = HealthHelper.getHealth(player);
        if (health == null) return;
        
        // Gestion des dommages de saignement
        int bleedLevel = health.getBleedingLevel();
        if (bleedLevel > 0) {
            // Initialiser le timer s'il n'existe pas
            if (!bleedingTimers.containsKey(playerID)) {
                bleedingTimers.put(playerID, 0);
            }
            
            // Incrémenter le timer
            int timer = bleedingTimers.get(playerID) + 1;
            bleedingTimers.put(playerID, timer);
            
            // Appliquer les dégâts selon le niveau et le timer
            int ticksRequired = Math.max(5, 100 - (bleedLevel * 15)); // Intervalle entre les dommages selon le niveau
            if (timer >= ticksRequired) {
                // Réinitialiser le timer
                bleedingTimers.put(playerID, 0);
                
                // Appliquer les dégâts
                float damageAmount = 0.5f * bleedLevel;
                player.attackEntityFrom(DamageSource.GENERIC, damageAmount);
                LOGGER.debug("Dommages de saignement appliqués au joueur {}: {}", player.getName(), damageAmount);
            }
        } else {
            // Supprimer le timer si le joueur ne saigne plus
            bleedingTimers.remove(playerID);
        }
        
        // Gestion de la régénération de santé personnalisée
        if (!player.isDead && player.getFoodStats().getFoodLevel() > 17) { // Régénération active si la faim est suffisante
            // Initialiser le timer s'il n'existe pas
            if (!regenTimers.containsKey(playerID)) {
                regenTimers.put(playerID, 0);
            }
            
            // Incrémenter le timer
            int timer = regenTimers.get(playerID) + 1;
            regenTimers.put(playerID, timer);
            
            // Calculer la vitesse de régénération en fonction de l'état de santé
            float baseRate = health.getHealthRegenRate();
            float actualRate = baseRate;
            
            // Réduire la régénération selon les afflictions
            if (health.getBleedingLevel() > 0) {
                actualRate *= Math.max(0.2f, 1.0f - (health.getBleedingLevel() * 0.15f));
            }
            
            if (health.getBoneBrokenLevel() > 0) {
                actualRate *= Math.max(0.5f, 1.0f - (health.getBoneBrokenLevel() * 0.1f));
            }
            
            if (health.getPoisonLevel() > 0) {
                actualRate *= Math.max(0.3f, 1.0f - (health.getPoisonLevel() * 0.2f));
            }
            
            // Calculer le temps requis entre chaque régénération
            int ticksRequired = Math.max(10, (int)(80 / actualRate));
            
            // Appliquer la régénération si le temps est écoulé
            if (timer >= ticksRequired && player.getHealth() < player.getMaxHealth()) {
                // Réinitialiser le timer
                regenTimers.put(playerID, 0);
                
                // Appliquer la guérison
                float healAmount = 1.0f * actualRate;
                player.heal(healAmount);
                LOGGER.debug("Régénération naturelle pour le joueur {}: {}", player.getName(), healAmount);
            }
        }
    }
    
    /**
     * Gère le tick du monde pour les effets de récupération sur le long terme
     */
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        // Ne procéder qu'à la fin de la phase du tick et côté serveur
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) {
            return;
        }
        
        // Toutes les 5 minutes (6000 ticks), diminuer légèrement les afflictions des joueurs
        if (event.world.getTotalWorldTime() % 6000 == 0) {
            for (EntityPlayer player : event.world.playerEntities) {
                IHealth health = HealthHelper.getHealth(player);
                if (health != null) {
                    // Chance de guérison naturelle (sauf pour les niveaux élevés qui nécessitent des soins)
                    if (health.getBleedingLevel() <= 2 && RANDOM.nextFloat() < 0.3f) {
                        HealthHelper.decreaseBleedingLevel(player, 1);
                    }
                    
                    if (health.getPoisonLevel() <= 1 && RANDOM.nextFloat() < 0.2f) {
                        HealthHelper.decreasePoisonLevel(player, 1);
                    }
                    
                    // Les fractures guérissent très lentement naturellement, seulement niveau 1
                    if (health.getBoneBrokenLevel() == 1 && RANDOM.nextFloat() < 0.1f) {
                        HealthHelper.decreaseBoneBrokenLevel(player, 1);
                    }
                    
                    // Mettre à jour l'état critique
                    HealthHelper.updateCriticalCondition(player);
                }
            }
        }
    }
}