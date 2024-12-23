package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.CowboyHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobMixin extends LivingEntity {

    @Shadow
    public abstract void setSpeed(float p_21556_);

    protected MobMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        Entity entity = this.getFirstPassenger();
        Mob mob = (Mob) (Object) this;

        if (this.isNoAi() || !(entity instanceof Player player) || mob instanceof Saddleable)
            return;

        ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

        if (!(stack.getItem() instanceof CowboyHatItem))
            return;

        cir.setReturnValue(player);
    }

    @Shadow
    public boolean isNoAi() {
        return true;
    }
}
