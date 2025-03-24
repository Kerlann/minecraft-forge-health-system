package com.kerlann.healthsystem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = HealthSystem.MODID,
    name = HealthSystem.NAME,
    version = HealthSystem.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class HealthSystem {
    public static final String MODID = "healthsystem";
    public static final String NAME = "Health System";
    public static final String VERSION = "1.0.0";

    private static Logger logger;

    @SidedProxy(clientSide = "com.kerlann.healthsystem.client.ClientProxy", 
                serverSide = "com.kerlann.healthsystem.common.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Health System: Pre-initialization");
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Health System: Initialization");
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Health System: Post-initialization");
        proxy.postInit(event);
    }

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event) {
        // Système de dommages personnalisé
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            float damageAmount = event.getAmount();
            
            // Exemple: Réduire les dégâts si le joueur a plus de 75% de sa santé
            IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            double maxHealth = maxHealthAttribute.getAttributeValue();
            
            if (player.getHealth() > (float)(maxHealth * 0.75)) {
                event.setAmount(damageAmount * 0.8f); // 20% de réduction des dégâts
                logger.debug("Réduction des dégâts appliquée pour le joueur: " + player.getName());
            }
        }
    }
}