package it.hurts.octostudios.rarcompat.mixin;

import artifacts.item.UmbrellaItem;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    public void onHurt(DamageSource source, float p_21017_, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player && player.isUsingItem() && player.getUseItem().getItem() instanceof UmbrellaItem) {
            Entity target = source.getEntity();
            if (target instanceof LivingEntity) {
                Vec3 kb = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ()).normalize()
                        .scale(((IRelicItem) player.getUseItem().getItem()).getStatValue(player.getUseItem(), "shield", "passive_knockback"));
                target.setDeltaMovement(target.getDeltaMovement().add(kb));
            }
        }
    }
}