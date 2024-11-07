package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.necklace.CharmOfSinkingItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class PlayerEntityMixin {

//    @Shadow public abstract void
//    remove(Entity.RemovalReason p_146834_);
//
//    @Shadow
//    public abstract void playerTouch(Player p_20081_);

    @Inject(method = "Lnet/minecraft/world/entity/Entity;updateFluidHeightAndDoFluidPushing()V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void onAirMax(CallbackInfo ci) {
  //      ci.cancel();
//        Entity entity = (Entity) (Object) this;
//
//        ItemStack stack = EntityUtils.findEquippedCurio(entity, ModItems.CHARM_OF_SINKING.value());
//
//        if (entity == null || !entity.isEyeInFluid(FluidTags.WATER) || !entity.onGround() || !(stack.getItem() instanceof CharmOfSinkingItem relic))
//            return;
//
//        cir.setReturnValue((int) (300 + (500 * relic.getStatValue(stack, "immersion", "air"))));
    }
}
