package com.kerlann.healthsystem.client;

import com.kerlann.healthsystem.CommonProxy;
import com.kerlann.healthsystem.HealthSystem;
import com.kerlann.healthsystem.client.gui.HealthHudOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Proxy côté client pour gérer les initialisations spécifiques au client
 */
public class ClientProxy extends CommonProxy {
    
    private static final Logger LOGGER = LogManager.getLogger(HealthSystem.MODID);
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        LOGGER.info("Health System Client: Pre-initialization");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        LOGGER.info("Health System Client: Initialization");
        
        // Enregistrer le renderer de l'HUD
        MinecraftForge.EVENT_BUS.register(HealthHudOverlay.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        LOGGER.info("Health System Client: Post-initialization");
    }
}