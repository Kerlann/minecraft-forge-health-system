package com.kerlann.healthsystem.item;

import com.kerlann.healthsystem.HealthSystem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire pour l'enregistrement des items
 */
@Mod.EventBusSubscriber(modid = HealthSystem.MODID)
public class ItemRegistry {

    // Liste des items du mod
    private static final List<Item> ITEMS = new ArrayList<>();
    
    // Items médicaux
    public static final ItemMedical BANDAGE = new ItemMedical(ItemMedical.MedicalType.BANDAGE, 32, 0);
    public static final ItemMedical SPLINT = new ItemMedical(ItemMedical.MedicalType.SPLINT, 40, 0);
    public static final ItemMedical ANTIDOTE = new ItemMedical(ItemMedical.MedicalType.ANTIDOTE, 20, 0);
    public static final ItemMedical FIRST_AID_KIT = new ItemMedical(ItemMedical.MedicalType.FIRST_AID_KIT, 50, 6);
    public static final ItemMedical ADRENALINE = new ItemMedical(ItemMedical.MedicalType.ADRENALINE, 15, 4);
    
    /**
     * Initialise les items du mod
     */
    public static void init() {
        // Enregistrer les items dans la liste
        registerItem(BANDAGE);
        registerItem(SPLINT);
        registerItem(ANTIDOTE);
        registerItem(FIRST_AID_KIT);
        registerItem(ADRENALINE);
    }
    
    /**
     * Enregistre un item dans la liste
     */
    private static void registerItem(Item item) {
        ITEMS.add(item);
    }
    
    /**
     * Gère l'enregistrement des items
     */
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        
        // Enregistrer tous les items
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }
    
    /**
     * Gère l'enregistrement des modèles des items
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        // Enregistrer les modèles pour tous les items
        for (Item item : ITEMS) {
            registerItemModel(item);
        }
    }
    
    /**
     * Enregistre le modèle d'un item
     */
    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, 
            new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
    
    /**
     * Gère l'enregistrement des recettes
     */
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        // Recette pour le bandage
        addShapedRecipe("bandage", new ItemStack(BANDAGE, 3), 
            " S ", "PWP", " S ", 
            'S', Items.STRING, 
            'P', Items.PAPER, 
            'W', Items.WOOL);
        
        // Recette pour l'attelle
        addShapedRecipe("splint", new ItemStack(SPLINT, 2), 
            "S S", "SSS", "S S", 
            'S', Items.STICK);
        
        // Recette pour l'antidote
        addShapedRecipe("antidote", new ItemStack(ANTIDOTE, 1), 
            " B ", "SFS", " G ", 
            'B', Items.GLASS_BOTTLE, 
            'S', Items.SPIDER_EYE, 
            'F', Items.FERMENTED_SPIDER_EYE, 
            'G', Items.GLOWSTONE_DUST);
        
        // Recette pour le kit de premiers soins
        addShapedRecipe("first_aid_kit", new ItemStack(FIRST_AID_KIT, 1), 
            "BBB", "BAB", "BBB", 
            'B', BANDAGE, 
            'A', Items.GOLDEN_APPLE);
        
        // Recette pour l'adrénaline
        addShapedRecipe("adrenaline", new ItemStack(ADRENALINE, 1), 
            " N ", "GBG", " R ", 
            'N', Items.NETHER_WART, 
            'G', Items.GLOWSTONE_DUST, 
            'B', Items.GLASS_BOTTLE, 
            'R', Items.REDSTONE);
    }
    
    /**
     * Ajoute une recette façonnée au registre
     */
    private static void addShapedRecipe(String name, ItemStack output, Object... params) {
        ResourceLocation location = new ResourceLocation(HealthSystem.MODID, name);
        ShapedRecipe recipe = new ShapedRecipe(location, output);
        
        String[] pattern = new String[3];
        int i = 0;
        
        // Extraire le modèle
        for (Object param : params) {
            if (param instanceof String) {
                pattern[i++] = (String) param;
                if (i >= 3) break;
            }
        }
        
        recipe.setKey('a', Ingredient.fromItem(Items.DIAMOND)); // Placeholder, sera remplacé
        
        // Ajouter la recette au registre
        GameRegistry.addShapedRecipe(location, null, output, pattern, params);
    }
}