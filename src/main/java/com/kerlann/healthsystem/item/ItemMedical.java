package com.kerlann.healthsystem.item;

import com.kerlann.healthsystem.HealthSystem;
import com.kerlann.healthsystem.capability.HealthHelper;
import com.kerlann.healthsystem.capability.IHealth;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Classe de base pour les items médicaux
 */
public class ItemMedical extends Item {

    // Types d'items médicaux
    public enum MedicalType {
        BANDAGE("bandage", 0),
        SPLINT("splint", 1),
        ANTIDOTE("antidote", 2),
        FIRST_AID_KIT("first_aid_kit", 3),
        ADRENALINE("adrenaline", 4);
        
        private final String name;
        private final int metadata;
        
        MedicalType(String name, int metadata) {
            this.name = name;
            this.metadata = metadata;
        }
        
        public String getName() {
            return name;
        }
        
        public int getMetadata() {
            return metadata;
        }
        
        public static MedicalType fromMetadata(int metadata) {
            for (MedicalType type : values()) {
                if (type.getMetadata() == metadata) {
                    return type;
                }
            }
            return BANDAGE; // Par défaut
        }
    }
    
    private final MedicalType type;
    private final int useTime;
    private final int healAmount;
    
    public ItemMedical(MedicalType type, int useTime, int healAmount) {
        this.type = type;
        this.useTime = useTime;
        this.healAmount = healAmount;
        
        setUnlocalizedName(HealthSystem.MODID + "." + type.getName());
        setRegistryName(HealthSystem.MODID, type.getName());
        setCreativeTab(CreativeTabs.MISC); // Peut être remplacé par un onglet personnalisé plus tard
        setMaxStackSize(16);
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return useTime;
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.EAT;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Vérifier si l'utilisation est nécessaire
        IHealth health = HealthHelper.getHealth(player);
        if (health == null) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
        
        boolean canUse = false;
        
        switch (type) {
            case BANDAGE:
                canUse = health.getBleedingLevel() > 0;
                break;
            case SPLINT:
                canUse = health.getBoneBrokenLevel() > 0;
                break;
            case ANTIDOTE:
                canUse = health.getPoisonLevel() > 0;
                break;
            case FIRST_AID_KIT:
                canUse = health.getBleedingLevel() > 0 || health.getBoneBrokenLevel() > 0 || 
                         health.getPoisonLevel() > 0 || player.getHealth() < player.getMaxHealth();
                break;
            case ADRENALINE:
                canUse = health.isCriticalCondition() || player.getHealth() <= 6.0f;
                break;
        }
        
        if (canUse) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "Cet item médical n'est pas nécessaire actuellement."));
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
    }
    
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return stack;
        }
        
        EntityPlayer player = (EntityPlayer) entityLiving;
        IHealth health = HealthHelper.getHealth(player);
        
        if (health != null && !world.isRemote) {
            switch (type) {
                case BANDAGE:
                    // Réduit le niveau de saignement de 1-2 niveaux
                    HealthHelper.decreaseBleedingLevel(player, 1 + world.rand.nextInt(2));
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Le bandage a arrêté votre saignement."));
                    break;
                    
                case SPLINT:
                    // Réduit le niveau de fracture de 1
                    HealthHelper.decreaseBoneBrokenLevel(player, 1);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "L'attelle a stabilisé votre fracture."));
                    break;
                    
                case ANTIDOTE:
                    // Réduit le niveau d'empoisonnement complètement
                    HealthHelper.decreasePoisonLevel(player, health.getPoisonLevel());
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "L'antidote a neutralisé le poison."));
                    break;
                    
                case FIRST_AID_KIT:
                    // Réduit tous les niveaux d'affliction et soigne de la santé
                    HealthHelper.decreaseBleedingLevel(player, health.getBleedingLevel());
                    HealthHelper.decreaseBoneBrokenLevel(player, 1);
                    HealthHelper.decreasePoisonLevel(player, health.getPoisonLevel());
                    player.heal(healAmount);
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Le kit de premiers soins a traité vos blessures."));
                    break;
                    
                case ADRENALINE:
                    // Soins d'urgence et boost temporaire
                    player.heal(healAmount);
                    // À compléter avec des effets temporaires de potion
                    player.sendMessage(new TextComponentString(TextFormatting.GREEN + "L'adrénaline vous donne un regain d'énergie!"));
                    break;
            }
            
            // Mise à jour de l'état critique
            HealthHelper.updateCriticalCondition(player);
            
            // Jouer un son
            world.playSound(null, player.posX, player.posY, player.posZ, 
                           SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 
                           0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            
            // Consommer un item
            stack.shrink(1);
        }
        
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        
        // Ajouter des descriptions spécifiques selon le type
        switch (type) {
            case BANDAGE:
                tooltip.add(TextFormatting.BLUE + I18n.format("item.healthsystem.bandage.desc"));
                tooltip.add(TextFormatting.GRAY + "Traite les saignements");
                break;
                
            case SPLINT:
                tooltip.add(TextFormatting.BLUE + I18n.format("item.healthsystem.splint.desc"));
                tooltip.add(TextFormatting.GRAY + "Réduit les fractures");
                break;
                
            case ANTIDOTE:
                tooltip.add(TextFormatting.BLUE + I18n.format("item.healthsystem.antidote.desc"));
                tooltip.add(TextFormatting.GRAY + "Guérit les empoisonnements");
                break;
                
            case FIRST_AID_KIT:
                tooltip.add(TextFormatting.BLUE + I18n.format("item.healthsystem.first_aid_kit.desc"));
                tooltip.add(TextFormatting.GRAY + "Soigne les blessures et restaure " + healAmount + " points de vie");
                break;
                
            case ADRENALINE:
                tooltip.add(TextFormatting.BLUE + I18n.format("item.healthsystem.adrenaline.desc"));
                tooltip.add(TextFormatting.GRAY + "Restaure " + healAmount + " points de vie et donne un boost temporaire");
                break;
        }
        
        // Afficher le temps d'utilisation
        tooltip.add(TextFormatting.DARK_GRAY + "Temps d'utilisation: " + (useTime / 20.0f) + "s");
    }
    
    public MedicalType getType() {
        return type;
    }
}