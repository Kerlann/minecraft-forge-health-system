package com.kerlann.healthsystem;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // Enregistrer les capacités, les événements, etc.
    }

    public void init(FMLInitializationEvent event) {
        // Enregistrer les recettes, etc.
    }

    public void postInit(FMLPostInitializationEvent event) {
        // Post-initialisation
    }
}