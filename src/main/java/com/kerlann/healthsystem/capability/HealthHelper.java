package com.kerlann.healthsystem.capability;

import com.kerlann.healthsystem.HealthSystem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe utilitaire pour manipuler les capacités de santé
 */
public class HealthHelper {
    
    private static final Logger LOGGER = LogManager.getLogger(HealthSystem.MODID);
    
    /**
     * Obtient la capacité de santé d'un joueur
     * @param player Le joueur
     * @return La capacité de santé du joueur ou null si indisponible
     */
    public static IHealth getHealth(EntityPlayer player) {
        if (player != null && player.hasCapability(HealthCapability.HEALTH_CAPABILITY, null)) {
            return player.getCapability(HealthCapability.HEALTH_CAPABILITY, null);
        }
        return null;
    }
    
    /**
     * Augmente le niveau de saignement d'un joueur
     * @param player Le joueur
     * @param amount Le montant d'augmentation
     */
    public static void increaseBleedingLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getBleedingLevel();
            int newLevel = Math.min(5, currentLevel + amount);
            
            if (newLevel > currentLevel) {
                health.setBleedingLevel(newLevel);
                
                // Appliquer les effets selon le niveau de saignement
                applyBleedingEffects(player, newLevel);
                
                // Notification au joueur
                String message = TextFormatting.RED + "Vous saignez";
                if (newLevel >= 4) {
                    message += " abondamment";
                }
                message += "!";
                player.sendMessage(new TextComponentString(message));
                
                LOGGER.debug("Niveau de saignement du joueur {} augmenté à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Réduit le niveau de saignement d'un joueur
     * @param player Le joueur
     * @param amount Le montant de réduction
     */
    public static void decreaseBleedingLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getBleedingLevel();
            int newLevel = Math.max(0, currentLevel - amount);
            
            if (newLevel < currentLevel) {
                health.setBleedingLevel(newLevel);
                
                // Notification au joueur si le saignement a cessé
                if (newLevel == 0 && currentLevel > 0) {
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Votre saignement s'est arrêté."));
                }
                
                LOGGER.debug("Niveau de saignement du joueur {} réduit à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Augmente le niveau de fracture osseuse d'un joueur
     * @param player Le joueur
     * @param amount Le montant d'augmentation
     */
    public static void increaseBoneBrokenLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getBoneBrokenLevel();
            int newLevel = Math.min(3, currentLevel + amount);
            
            if (newLevel > currentLevel) {
                health.setBoneBrokenLevel(newLevel);
                
                // Appliquer les effets selon le niveau de fracture
                applyBoneBrokenEffects(player, newLevel);
                
                // Notification au joueur
                String message = TextFormatting.DARK_RED + "Vous avez ";
                if (newLevel == 1) {
                    message += "une légère fracture";
                } else if (newLevel == 2) {
                    message += "une fracture modérée";
                } else {
                    message += "une fracture grave";
                }
                message += "!";
                player.sendMessage(new TextComponentString(message));
                
                LOGGER.debug("Niveau de fracture du joueur {} augmenté à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Réduit le niveau de fracture osseuse d'un joueur
     * @param player Le joueur
     * @param amount Le montant de réduction
     */
    public static void decreaseBoneBrokenLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getBoneBrokenLevel();
            int newLevel = Math.max(0, currentLevel - amount);
            
            if (newLevel < currentLevel) {
                health.setBoneBrokenLevel(newLevel);
                
                // Notification au joueur si la fracture a guéri
                if (newLevel == 0 && currentLevel > 0) {
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Votre fracture a guéri."));
                }
                
                LOGGER.debug("Niveau de fracture du joueur {} réduit à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Augmente le niveau d'empoisonnement d'un joueur
     * @param player Le joueur
     * @param amount Le montant d'augmentation
     */
    public static void increasePoisonLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getPoisonLevel();
            int newLevel = Math.min(3, currentLevel + amount);
            
            if (newLevel > currentLevel) {
                health.setPoisonLevel(newLevel);
                
                // Appliquer les effets selon le niveau d'empoisonnement
                applyPoisonEffects(player, newLevel);
                
                // Notification au joueur
                String message = TextFormatting.DARK_GREEN + "Vous êtes ";
                if (newLevel == 1) {
                    message += "légèrement empoisonné";
                } else if (newLevel == 2) {
                    message += "modérément empoisonné";
                } else {
                    message += "gravement empoisonné";
                }
                message += "!";
                player.sendMessage(new TextComponentString(message));
                
                LOGGER.debug("Niveau d'empoisonnement du joueur {} augmenté à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Réduit le niveau d'empoisonnement d'un joueur
     * @param player Le joueur
     * @param amount Le montant de réduction
     */
    public static void decreasePoisonLevel(EntityPlayer player, int amount) {
        IHealth health = getHealth(player);
        if (health != null) {
            int currentLevel = health.getPoisonLevel();
            int newLevel = Math.max(0, currentLevel - amount);
            
            if (newLevel < currentLevel) {
                health.setPoisonLevel(newLevel);
                
                // Notification au joueur si l'empoisonnement a disparu
                if (newLevel == 0 && currentLevel > 0) {
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Vous n'êtes plus empoisonné."));
                }
                
                LOGGER.debug("Niveau d'empoisonnement du joueur {} réduit à {}", player.getName(), newLevel);
            }
        }
    }
    
    /**
     * Met à jour l'état critique d'un joueur en fonction de ses conditions actuelles
     * @param player Le joueur
     */
    public static void updateCriticalCondition(EntityPlayer player) {
        IHealth health = getHealth(player);
        if (health != null) {
            boolean wasCritical = health.isCriticalCondition();
            
            // Critères pour l'état critique
            boolean isCritical = 
                health.getBleedingLevel() >= 4 || 
                health.getBoneBrokenLevel() >= 3 || 
                health.getPoisonLevel() >= 3 || 
                player.getHealth() <= 4.0f;
            
            health.setCriticalCondition(isCritical);
            
            // Notification au joueur si son état a changé
            if (isCritical && !wasCritical) {
                player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "" + TextFormatting.BOLD + "ÉTAT CRITIQUE! Trouvez des soins immédiatement!"));
                LOGGER.debug("Joueur {} entré en état critique", player.getName());
            } else if (!isCritical && wasCritical) {
                player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Votre état s'est stabilisé."));
                LOGGER.debug("Joueur {} sorti de l'état critique", player.getName());
            }
        }
    }
    
    /**
     * Applique les effets de saignement selon le niveau
     * @param player Le joueur
     * @param level Le niveau de saignement
     */
    private static void applyBleedingEffects(EntityPlayer player, int level) {
        switch (level) {
            case 1: // Saignement léger
                // Effet visuel léger
                break;
            case 2: // Saignement modéré
                // Lenteur légère
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 100, 0)); // 2 = Lenteur
                break;
            case 3: // Saignement important
                // Lenteur modérée
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 200, 0));
                break;
            case 4: // Saignement sévère
                // Lenteur et faiblesse
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 300, 1));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(18), 300, 0)); // 18 = Faiblesse
                break;
            case 5: // Saignement critique
                // Lenteur, faiblesse et nausée
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 400, 2));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(18), 400, 1));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(9), 200, 0)); // 9 = Nausée
                break;
        }
    }
    
    /**
     * Applique les effets de fracture osseuse selon le niveau
     * @param player Le joueur
     * @param level Le niveau de fracture
     */
    private static void applyBoneBrokenEffects(EntityPlayer player, int level) {
        switch (level) {
            case 1: // Fracture légère
                // Lenteur légère
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 400, 0));
                break;
            case 2: // Fracture modérée
                // Lenteur modérée et mining fatigue
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 600, 1));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 600, 0)); // 4 = Mining fatigue
                break;
            case 3: // Fracture grave
                // Lenteur sévère, mining fatigue et sauts réduits
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 1200, 2));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 1200, 1));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(8), 1200, 1)); // 8 = Jump Boost (négatif)
                break;
        }
    }
    
    /**
     * Applique les effets d'empoisonnement selon le niveau
     * @param player Le joueur
     * @param level Le niveau d'empoisonnement
     */
    private static void applyPoisonEffects(EntityPlayer player, int level) {
        switch (level) {
            case 1: // Empoisonnement léger
                // Effet de poison léger
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(19), 200, 0)); // 19 = Poison
                break;
            case 2: // Empoisonnement modéré
                // Poison modéré et nausée
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(19), 300, 0));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(9), 200, 0));
                break;
            case 3: // Empoisonnement grave
                // Poison sévère, nausée et faim
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(19), 400, 1));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(9), 300, 0));
                player.addPotionEffect(new PotionEffect(Potion.getPotionById(17), 400, 0)); // 17 = Faim
                break;
        }
    }
}