package it.hurts.octostudios.rarcompat.mixin.init;

import artifacts.client.item.ArtifactRenderers;
import artifacts.client.item.renderer.ArtifactRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ArtifactRenderers.class)
public class ArtifactRenderersMixin {

    @Inject(method = "register(Lnet/minecraft/world/item/Item;Ljava/util/function/Supplier;)V", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void injectRegister(Item item, Supplier<ArtifactRenderer> rendererSupplier, CallbackInfo ci) {
        switch (BuiltInRegistries.ITEM.getKey(item).getPath()) {
            case "warp_drive", "crystal_heart", "universal_attractor", "cloud_in_a_bottle", "obsidian_skull",
                 "chorus_totem", "antidote_vessel" -> ci.cancel();
        }
    }
}