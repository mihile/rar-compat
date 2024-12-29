package it.hurts.octostudios.rarcompat.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "travelRidden", at = @At("HEAD"), cancellable = true)
    private void travelRidden(Player player, Vec3 vec, CallbackInfo ci) {
        Mob mounted = (Mob) (Object) this;

        if (mounted instanceof Saddleable)
            return;

        mounted.setTarget(null);
        mounted.setSpeed(0.3F);

        this.tickRidden(mounted, player);

        if ((mounted instanceof FlyingAnimal || mounted instanceof FlyingMob || mounted instanceof WaterAnimal) && !mounted.onGround())
            this.travel(this.getRiddenInput(player, mounted), mounted);
        else {
            if (player.level().isClientSide() && player instanceof LocalPlayer localPlayer && localPlayer.input.jumping && mounted.onGround())
                mounted.addDeltaMovement(new Vec3(0, 0.6, 0));

            mounted.travel(this.getRiddenInput(player, mounted));
        }

        ci.cancel();
    }

    @Unique
    protected Vec3 getRiddenInput(Player player, Mob mounted) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;

        if (f1 <= 0.0F)
            f1 *= 0.25F;

        if ((mounted instanceof FlyingAnimal || mounted instanceof FlyingMob) || mounted instanceof WaterAnimal) {
            double verticalSpeed = mounted.getDeltaMovement().y;

            double speedMultiplier = 0.5;

            if (verticalSpeed != 0.0)
                speedMultiplier += Math.abs(verticalSpeed) * 0.1;

            return new Vec3(f, -Math.sin(Math.toRadians(player.getXRot()) * speedMultiplier), f1);
        }

        return new Vec3(f, 0.0, f1);
    }

    @Unique
    protected void tickRidden(Mob mob, Player player) {
        Vec2 rotation;

        if (mob instanceof FlyingMob)
            rotation = new Vec2(-player.getXRot() * 0.5F, player.getYRot());
        else
            rotation = new Vec2(player.getXRot() * 0.5F, player.getYRot());

        mob.setXRot(rotation.x % 360.0F);
        mob.setYRot(rotation.y % 360.0F);

        mob.yRotO = mob.yBodyRot = mob.yHeadRot = mob.getYRot();

        if (!mob.isControlledByLocalInstance() || !mob.onGround())
            return;
    }

    public void travel(Vec3 movementInput, Mob mob) {
        if (!this.isControlledByLocalInstance())
            return;

        Vec3 adjustedMovement = mob.handleRelativeFrictionAndCalculateMovement(movementInput, 0.5F);

        double verticalMovement = adjustedMovement.y;

        if (mob.hasEffect(MobEffects.LEVITATION))
            verticalMovement += (0.05 * (double) (mob.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - adjustedMovement.y) * 0.2;
        else if (!this.level().isClientSide)
            verticalMovement -= -0.1;

        if (!mob.isInWater() && mob instanceof WaterAnimal)
            this.setDeltaMovement(adjustedMovement.x, -0.08, adjustedMovement.z);
        else
            this.setDeltaMovement(adjustedMovement.x, verticalMovement, adjustedMovement.z);

        if (this.horizontalCollision && !this.level().isClientSide) {
            double impactForce = this.getDeltaMovement().horizontalDistance();

            if (impactForce > 0.1)
                this.hurt(this.damageSources().flyIntoWall(), (float) (impactForce * 10.0));
        }

        mob.calculateEntityAnimation(this instanceof FlyingAnimal);
    }
}