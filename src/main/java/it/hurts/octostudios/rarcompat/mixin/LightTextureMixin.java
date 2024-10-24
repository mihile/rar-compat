package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.NightVisionGogglesItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightTexture.class)
public class LightTextureMixin {
    @ModifyArgs(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;set(FFF)Lorg/joml/Vector3f;"))
    private void modifySetArguments(Args args) {
        var player = Minecraft.getInstance().player;

        if (player == null)
            return;

        var stack = EntityUtils.findEquippedCurio(player, ModItems.NIGHT_VISION_GOGGLES.value());

        if (!(stack.getItem() instanceof NightVisionGogglesItem relic) || !relic.isAbilityTicking(stack, "vision"))
            return;

        var gamma = (float) relic.getStatValue(stack, "vision", "amount");
        var modifier = relic.getRelicLevel(stack) / relic.getLevelingData().getMaxLevel();

        float r = args.get(0);
        float b = args.get(2);

        args.set(0, Math.clamp((r + gamma) * modifier, 0F, 1F));
        args.set(1, 1F);
        args.set(2, Math.clamp((b + gamma) * modifier, 0F, 1F));
    }
}