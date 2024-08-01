package it.hurts.octostudios.rarcompat.mixin;

import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.NoveltyDrinkingHat;
import it.hurts.octostudios.rarcompat.items.PlasticDrinkingHat;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ModItems.class)
public class ModItemsMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lartifacts/registry/ModItems;wearableItem(Ljava/lang/String;Ljava/util/function/Consumer;)Lnet/minecraft/core/Holder;"))
    private static Holder<Item> redirectWearableItem(String name, Consumer<WearableArtifactItem.Builder> builderConsumer) {
        return switch (name) {
            case "plastic_drinking_hat" -> register(name, PlasticDrinkingHat::new);
            case "novelty_drinking_hat" -> register(name, NoveltyDrinkingHat::new);
            default -> wearableItem(name, builderConsumer);
        };
    }

    @Shadow
    private static Holder<Item> wearableItem(String name, Consumer<WearableArtifactItem.Builder> consumer) {
        return null;
    }

    @Shadow
    private static Holder<Item> register(String name, Supplier<? extends Item> supplier) {
        return null;
    }
}