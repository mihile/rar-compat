package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.NightVisionGogglesItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @ModifyVariable(method = "updateLightTexture", at = @At(value = "STORE"), name = "f10")
    private float modifyF10(float original) {
        var player = Minecraft.getInstance().player;

        if (player == null)
            return original;

        var stack = EntityUtils.findEquippedCurio(player, ModItems.NIGHT_VISION_GOGGLES.value());

        if (!(stack.getItem() instanceof NightVisionGogglesItem relic) || !relic.isAbilityTicking(stack, "vision"))
            return original;

        double gamma = relic.getStatValue(stack, "vision", "amount");

        return (float) Mth.lerp(gamma, 0.2D, 5D);
    }
}