package it.hurts.octostudios.rarcompat.mixin;

import artifacts.forge.curio.WearableArtifactCurio;
import it.hurts.octostudios.rarcompat.items.IRelicArtifactsFortune;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

@Mixin(WearableArtifactCurio.class)
public class WearableArtifactCurioMixin {

    @Inject(method = "getFortuneLevel", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void getFortuneLevel(SlotContext slotContext, LootContext lootContext, CallbackInfoReturnable<Integer> cir) {
        List<ItemStack> stackList = EntityUtils.getEquippedRelics(slotContext.entity());

        for (ItemStack stack : stackList) {
            if (!(stack.getItem() instanceof IRelicArtifactsFortune fortune)) return;

            cir.setReturnValue(fortune.getFortuneLevel(slotContext, lootContext));
        }
    }
}
