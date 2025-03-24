package com.kerlann.healthsystem.client.gui;

import com.kerlann.healthsystem.HealthSystem;
import com.kerlann.healthsystem.capability.HealthCapability;
import com.kerlann.healthsystem.capability.IHealth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Classe gérant l'affichage de l'HUD de santé
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = HealthSystem.MODID, value = Side.CLIENT)
public class HealthHudOverlay extends Gui {
    
    private static final ResourceLocation ICONS = new ResourceLocation(HealthSystem.MODID, "textures/gui/health_icons.png");
    
    private static final int ICON_SIZE = 16;
    private static final int SPACING = 2;
    
    /**
     * Gère le rendu de l'overlay pour l'HUD de santé
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        // Ne rendre que si l'HUD principal est affiché
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        
        // Vérifier si le joueur a la capacité de santé
        if (player == null || !player.hasCapability(HealthCapability.HEALTH_CAPABILITY, null)) {
            return;
        }
        
        // Obtenir la capacité de santé
        IHealth health = player.getCapability(HealthCapability.HEALTH_CAPABILITY, null);
        if (health == null) {
            return;
        }
        
        // Obtenir les dimensions de l'écran
        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        
        // Configuration du rendu
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ICONS);
        
        // Paramètres de position
        int baseX = width / 2 + 92; // Position de base en X (à droite des cœurs de vie)
        int baseY = height - 49;    // Position de base en Y (même niveau que les cœurs de vie)
        
        // Affichage des icônes d'état
        int xOffset = 0;
        
        // Saignement
        int bleedingLevel = health.getBleedingLevel();
        if (bleedingLevel > 0) {
            // Déterminer quelle texture utiliser selon le niveau
            int textureX = 0;
            int textureY = 0;
            
            if (bleedingLevel >= 4) {
                textureY = 32; // Icône de saignement sévère
            } else if (bleedingLevel >= 2) {
                textureY = 16; // Icône de saignement modéré
            }
            
            // Dessiner l'icône
            drawTexturedModalRect(baseX + xOffset, baseY, textureX, textureY, ICON_SIZE, ICON_SIZE);
            xOffset += ICON_SIZE + SPACING;
        }
        
        // Fracture osseuse
        int boneBrokenLevel = health.getBoneBrokenLevel();
        if (boneBrokenLevel > 0) {
            // Déterminer quelle texture utiliser selon le niveau
            int textureX = 16;
            int textureY = 0;
            
            if (boneBrokenLevel >= 3) {
                textureY = 32; // Icône de fracture grave
            } else if (boneBrokenLevel >= 2) {
                textureY = 16; // Icône de fracture modérée
            }
            
            // Dessiner l'icône
            drawTexturedModalRect(baseX + xOffset, baseY, textureX, textureY, ICON_SIZE, ICON_SIZE);
            xOffset += ICON_SIZE + SPACING;
        }
        
        // Empoisonnement
        int poisonLevel = health.getPoisonLevel();
        if (poisonLevel > 0) {
            // Déterminer quelle texture utiliser selon le niveau
            int textureX = 32;
            int textureY = 0;
            
            if (poisonLevel >= 3) {
                textureY = 32; // Icône d'empoisonnement grave
            } else if (poisonLevel >= 2) {
                textureY = 16; // Icône d'empoisonnement modéré
            }
            
            // Dessiner l'icône
            drawTexturedModalRect(baseX + xOffset, baseY, textureX, textureY, ICON_SIZE, ICON_SIZE);
            xOffset += ICON_SIZE + SPACING;
        }
        
        // État critique
        if (health.isCriticalCondition()) {
            int textureX = 48;
            int textureY = 0;
            
            // Effet clignotant pour attirer l'attention
            if (Minecraft.getSystemTime() % 1000 > 500) {
                textureY = 16;
            }
            
            // Dessiner l'icône
            drawTexturedModalRect(baseX + xOffset, baseY, textureX, textureY, ICON_SIZE, ICON_SIZE);
        }
        
        // Finaliser le rendu
        GlStateManager.disableBlend();
    }
}