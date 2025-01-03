package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.CowboyHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobMixin {
    // TODO: Use correct method arguments instead og obfuscated names

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    public void getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
        Mob mob = (Mob) (Object) this;
        Entity entity = mob.getFirstPassenger();

        if (mob.isNoAi() || !(entity instanceof Player player) || mob instanceof Saddleable)
            return;

        ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

        if (!(stack.getItem() instanceof CowboyHatItem))
            return;

        cir.setReturnValue(player);
    }
}