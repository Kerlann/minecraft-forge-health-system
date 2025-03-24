package com.kerlann.healthsystem.capability;

import com.kerlann.healthsystem.HealthSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = HealthSystem.MODID)
public class HealthCapability {

    @CapabilityInject(IHealth.class)
    public static final Capability<IHealth> HEALTH_CAPABILITY = null;

    public static final ResourceLocation HEALTH_CAP = new ResourceLocation(HealthSystem.MODID, "health");

    public static void register() {
        CapabilityManager.INSTANCE.register(IHealth.class, new Storage(), HealthImpl::new);
    }

    public static class HealthImpl implements IHealth {
        private float maxHealth = 20.0F;
        private float healthRegenRate = 1.0F;
        private int bleedingLevel = 0;
        private int boneBrokenLevel = 0;
        private int poisonLevel = 0;
        private boolean isCriticalCondition = false;

        @Override
        public float getMaxHealth() {
            return maxHealth;
        }

        @Override
        public void setMaxHealth(float maxHealth) {
            this.maxHealth = maxHealth;
        }

        @Override
        public float getHealthRegenRate() {
            return healthRegenRate;
        }

        @Override
        public void setHealthRegenRate(float rate) {
            this.healthRegenRate = rate;
        }

        @Override
        public int getBleedingLevel() {
            return bleedingLevel;
        }

        @Override
        public void setBleedingLevel(int level) {
            this.bleedingLevel = Math.max(0, Math.min(5, level));
        }

        @Override
        public int getBoneBrokenLevel() {
            return boneBrokenLevel;
        }

        @Override
        public void setBoneBrokenLevel(int level) {
            this.boneBrokenLevel = Math.max(0, Math.min(3, level));
        }

        @Override
        public int getPoisonLevel() {
            return poisonLevel;
        }

        @Override
        public void setPoisonLevel(int level) {
            this.poisonLevel = Math.max(0, Math.min(3, level));
        }

        @Override
        public boolean isCriticalCondition() {
            return isCriticalCondition;
        }

        @Override
        public void setCriticalCondition(boolean critical) {
            this.isCriticalCondition = critical;
        }
    }

    public static class Storage implements Capability.IStorage<IHealth> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("maxHealth", instance.getMaxHealth());
            nbt.setFloat("healthRegenRate", instance.getHealthRegenRate());
            nbt.setInteger("bleedingLevel", instance.getBleedingLevel());
            nbt.setInteger("boneBrokenLevel", instance.getBoneBrokenLevel());
            nbt.setInteger("poisonLevel", instance.getPoisonLevel());
            nbt.setBoolean("isCriticalCondition", instance.isCriticalCondition());
            return nbt;
        }

        @Override
        public void readNBT(Capability<IHealth> capability, IHealth instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound tags = (NBTTagCompound) nbt;
            instance.setMaxHealth(tags.getFloat("maxHealth"));
            instance.setHealthRegenRate(tags.getFloat("healthRegenRate"));
            instance.setBleedingLevel(tags.getInteger("bleedingLevel"));
            instance.setBoneBrokenLevel(tags.getInteger("boneBrokenLevel"));
            instance.setPoisonLevel(tags.getInteger("poisonLevel"));
            instance.setCriticalCondition(tags.getBoolean("isCriticalCondition"));
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {
        private final IHealth instance = HEALTH_CAPABILITY.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == HEALTH_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == HEALTH_CAPABILITY ? HEALTH_CAPABILITY.cast(instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) HEALTH_CAPABILITY.getStorage().writeNBT(HEALTH_CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            HEALTH_CAPABILITY.getStorage().readNBT(HEALTH_CAPABILITY, instance, null, nbt);
        }
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(HEALTH_CAP, new Provider());
        }
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        EntityPlayer player = event.getEntityPlayer();
        IHealth oldHealth = event.getOriginal().getCapability(HEALTH_CAPABILITY, null);
        IHealth newHealth = player.getCapability(HEALTH_CAPABILITY, null);

        if (oldHealth != null && newHealth != null) {
            // Copier les données de l'ancienne capacité vers la nouvelle
            newHealth.setMaxHealth(oldHealth.getMaxHealth());
            newHealth.setHealthRegenRate(oldHealth.getHealthRegenRate());
            newHealth.setBleedingLevel(oldHealth.getBleedingLevel());
            newHealth.setBoneBrokenLevel(oldHealth.getBoneBrokenLevel());
            newHealth.setPoisonLevel(oldHealth.getPoisonLevel());
            newHealth.setCriticalCondition(oldHealth.isCriticalCondition());
        }
    }
}