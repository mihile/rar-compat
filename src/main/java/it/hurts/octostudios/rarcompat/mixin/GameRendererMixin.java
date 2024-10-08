package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.hurts.octostudios.rarcompat.items.hat.NightVisionGogglesItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    //@ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float getNightVisionScale(float scale, LivingEntity entity, float p_109110_) {
        if (!(entity instanceof Player player) || !player.hasEffect(MobEffects.NIGHT_VISION))
            return scale;

        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ModItems.NIGHT_VISION_GOGGLES.value());

        if (relicStack != null && relicStack.getItem() instanceof NightVisionGogglesItem goggles) {
            double gamma = goggles.getStatValue(relicStack, "vision", "amount");

            return (float) Mth.lerp(gamma, 0.005D, 5D);
        }

        return scale;
    }
}

