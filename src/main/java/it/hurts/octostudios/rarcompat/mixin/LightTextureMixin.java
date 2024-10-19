package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @ModifyVariable(method = "updateLightTexture", at = @At(value = "STORE", ordinal = 0), ordinal = 5)
    private float getLightModifier(float original) {
        var player = Minecraft.getInstance().player;

        if (player == null)
            return original;

        var stack = EntityUtils.findEquippedCurio(player, ModItems.NIGHT_VISION_GOGGLES.value());

        // TODO: Replace 1F with formula of brightness interpolated between 0F and 1F based on the ability stat.
        return stack.isEmpty() ? original : 1F;
    }
}