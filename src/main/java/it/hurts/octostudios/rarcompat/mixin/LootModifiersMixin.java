package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModLootConditions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModLootConditions.class)
public class LootModifiersMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void addLoot(String name, MapCodec<? extends LootItemCondition> codec, CallbackInfoReturnable<Holder<LootItemConditionType>> cir) {
        cir.cancel();
    }

}
