package it.hurts.octostudios.rarcompat.entity;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.ScarfOfInvisibilityItem;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.NoteBlockEvent;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

public class InvisibilityZoneEntity extends Entity {
    @Getter
    @Setter
    private double radius;

    @Getter
    @Setter
    private UUID invZoneUUID;

    @Getter
    @Setter
    private UUID playerOwnerUUID;

    @Getter
    @Setter
    private Player playerOwner;

    private static InvisibilityZoneEntity existingZone;

    public InvisibilityZoneEntity(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;

        if (invZoneUUID != null && playerOwnerUUID != null) {
            existingZone = (InvisibilityZoneEntity) ((ServerLevel) this.level()).getEntity(invZoneUUID);
            playerOwner = (Player) ((ServerLevel) this.level()).getEntity(playerOwnerUUID);
        }

        checkDistance();

        double particles = getRadius() * 75;

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;

            double x = this.getX() + getRadius() * Math.cos(angle);
            double y = this.getY();
            double z = this.getZ() + getRadius() * Math.sin(angle);

            y = adjustYPositionToValidBlock(x, y, z);

            Random random = new Random();

            ((ServerLevel) level()).sendParticles(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.5F, 1, 1),
                    x, y + 0.2, z, 0, 0.0, 0, 0, 0);
        }
    }

    private void checkDistance() {
        ItemStack stack = EntityUtils.findEquippedCurio(playerOwner, ModItems.SCARF_OF_INVISIBILITY.value());

        if (!(stack.getItem() instanceof ScarfOfInvisibilityItem relic)) {
            discard();
            existingZone = null;
            return;
        }

        if (playerOwner.distanceTo(this) > relic.getStatValue(stack, "invisible", "radius")) {
            relic.setFlagEffect(true);
            this.discard();
        } else if (playerOwner.distanceTo(this) < relic.getStatValue(stack, "invisible", "radius"))
            relic.setFlagEffect(false);

    }

    private double adjustYPositionToValidBlock(double x, double y, double z) {
        for (int i = 0; i <= 2; i++) {
            BlockPos posAbove = new BlockPos((int) x, (int) (y + i), (int) z);
            if (!level().getBlockState(posAbove).isAir() && !level().getBlockState(posAbove).canBeReplaced()) {
                return y + i + 1;
            }
        }

        for (int i = 0; i <= 2; i++) {
            BlockPos posBelow = new BlockPos((int) x, (int) y - i - 1, (int) z);
            if (!level().getBlockState(posBelow).isAir() && !level().getBlockState(posBelow).canBeReplaced()) {
                return y - i;
            }
        }

        return y;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_326003_) {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.radius = compound.getInt("Radius");
        this.playerOwnerUUID = compound.getUUID("Player");
        invZoneUUID = compound.getUUID("ExistingZoneUUID");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putDouble("Radius", getRadius());
        compound.putUUID("Player", getPlayerOwner().getUUID());
        compound.putUUID("ExistingZoneUUID", invZoneUUID);
    }

    public static void replaceZone(Level level, InvisibilityZoneEntity newZone) {
        if (existingZone != null) {
            existingZone.remove(RemovalReason.DISCARDED);
        }

        existingZone = newZone;
        level.addFreshEntity(newZone);
    }
}
