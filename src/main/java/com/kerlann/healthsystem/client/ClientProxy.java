package com.kerlann.healthsystem.client;

import com.kerlann.healthsystem.CommonProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Enregistrer les rendus, etc.
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // Initialisation côté client
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        // Post-initialisation côté client
    }
}