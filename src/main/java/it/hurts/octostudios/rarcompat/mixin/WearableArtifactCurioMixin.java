package it.hurts.octostudios.rarcompat.mixin;

import artifacts.forge.curio.WearableArtifactCurio;
import it.hurts.octostudios.rarcompat.items.IRelicArtifact;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(WearableArtifactCurio.class)
public class WearableArtifactCurioMixin {
    @Inject(method = "getFortuneLevel", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void getFortuneLevel(SlotContext slotContext, LootContext lootContext, CallbackInfoReturnable<Integer> cir) {
        var optional = CuriosApi.getCuriosHelper().findCurio(slotContext.entity(), slotContext.identifier(), slotContext.index());

        if (optional.isEmpty())
            return;

        var stack = optional.get().stack();

        if (!(stack.getItem() instanceof IRelicArtifact relic))
            return;

        cir.setReturnValue(relic.getFortuneLevel(stack, slotContext, lootContext));
    }

    @Inject(method = "getLootingLevel", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target, int baseLooting, CallbackInfoReturnable<Integer> cir) {
        var optional = CuriosApi.getCuriosHelper().findCurio(slotContext.entity(), slotContext.identifier(), slotContext.index());

        if (optional.isEmpty())
            return;

        var stack = optional.get().stack();

        if (!(stack.getItem() instanceof IRelicArtifact relic))
            return;

        cir.setReturnValue(relic.getLootingLevel(stack, slotContext, source, target, baseLooting));
    }
}