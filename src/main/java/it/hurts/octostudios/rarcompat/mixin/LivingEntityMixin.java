package it.hurts.octostudios.rarcompat.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    @Shadow
    public float xxa;
    @Shadow
    public float yya;
    @Shadow
    public float zza;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void aiStep(CallbackInfo ci) {
        if (!(this.getFirstPassenger() instanceof Player player))
            return;
        System.out.println(this.getControllingPassenger());
        Vec3 vec31 = new Vec3((double)this.xxa, (double)this.yya, (double)this.zza);
        this.travelRidden(player, vec31);
    }

    @Shadow
    private void travelRidden(Player p_278244_, Vec3 p_278231_) {

    }


}
