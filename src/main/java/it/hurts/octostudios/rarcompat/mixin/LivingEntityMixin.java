package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.CowboyHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
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
        ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

        if (!((LivingEntity) (Object) this instanceof Mob mounted) || mounted instanceof Saddleable || !(stack.getItem() instanceof CowboyHatItem relic))
            return;

        if (!this.isControlledByLocalInstance()) {
            mounted.setDeltaMovement(Vec3.ZERO);
            mounted.setSpeed(0.0F);
            mounted.calculateEntityAnimation(false);
        }

        mounted.setTarget(null);
        mounted.setSpeed(0.3F);

        this.tickRidden(mounted, player);

        if (relic.isWaterOrFlyingMob(mounted))
            this.travel(this.getRiddenInput(player, relic, mounted), mounted);
        else
            mounted.travel(this.getRiddenInput(player, relic, mounted));

        ci.cancel();
    }

    @Unique
    protected Vec3 getRiddenInput(Player player, CowboyHatItem relic, Mob mounted) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;

        if (f1 <= 0.0F)
            f1 *= 0.25F;

        if (relic.isWaterOrFlyingMob(mounted)) {
            var verticalSpeed = mounted.getDeltaMovement().y;
            var speedMultiplier = 0.5;

            if (mounted.getDeltaMovement().y != 0.0)
                speedMultiplier += Math.abs(verticalSpeed) * 0.1;

            return new Vec3(f, player.getLookAngle().y * speedMultiplier, f1);
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
    }

    @Unique
    public void travel(Vec3 movementInput, Mob mob) {
        if (!this.isControlledByLocalInstance())
            return;

        Vec3 adjustedMovement = mob.handleRelativeFrictionAndCalculateMovement(movementInput, 1);

        if (!mob.isInWater() && mob instanceof WaterAnimal)
            this.setDeltaMovement(adjustedMovement.x, -0.08, adjustedMovement.z);
        else
            this.setDeltaMovement(adjustedMovement.x, movementInput.y * 0.8, adjustedMovement.z);
    }
}