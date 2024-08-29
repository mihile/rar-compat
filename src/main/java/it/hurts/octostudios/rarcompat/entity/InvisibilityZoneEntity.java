package it.hurts.octostudios.rarcompat.entity;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.body.ScarfOfInvisibilityItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

            y = adjustYPositionToValidBlock(x, y, z, playerOwner);

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

    private double adjustYPositionToValidBlock(double x, double y, double z, Player player) {
        HitResult result = this.level().clip(new ClipContext(new Vec3(x, player.getY() + 2, z),
                new Vec3(x, player.getY() - 2, z),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        Vec3 position = result.getLocation();

        if (result.getType() == HitResult.Type.MISS || level().getBlockState(new BlockPos((int) position.x, (int) position.y, (int) position.z)).blocksMotion())
            return y;

        return position.y();
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
