package it.hurts.octostudios.rarcompat.mixin;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
        Mob livingEntity = (Mob) (Object) this;

        if (livingEntity instanceof Saddleable || livingEntity instanceof FlyingMob || livingEntity instanceof WaterAnimal)
            return;

        Vec3 riddenInput = this.getRiddenInput(player);
        livingEntity.setTarget(null);
        this.tickRidden(livingEntity, player, vec);

        if (this.isControlledByLocalInstance()) {
            this.setSpeed((float) 0.7);
            this.travel(riddenInput);
        } else {
            this.setDeltaMovement(Vec3.ZERO);
            this.setSpeed(0.0F);
            this.calculateEntityAnimation(false);
        }

        ci.cancel();
    }

    @Shadow
    public abstract void setSpeed(float p_21320_);

    @Shadow
    public void calculateEntityAnimation(boolean p_268129_) {
    }

    @Shadow
    public abstract void travel(Vec3 p_21280_);

    @Unique
    protected Vec3 getRiddenInput(Player player) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;

        if (f1 <= 0.0F)
            f1 *= 0.25F;

        return new Vec3(f, 0.0, f1);
    }

    @Unique
    protected void tickRidden(Mob mob, Player player, Vec3 vec) {
        Vec2 rotation;

        if (mob instanceof FlyingMob)
            rotation = new Vec2(-player.getXRot() * 0.5F, player.getYRot());
        else
            rotation = new Vec2(player.getXRot() * 0.5F, player.getYRot());

        mob.setXRot(rotation.x % 360.0F);
        mob.setYRot(rotation.y % 360.0F);

        mob.yRotO = mob.yBodyRot = mob.yHeadRot = mob.getYRot();
    }
}