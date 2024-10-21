package it.hurts.octostudios.rarcompat.client.layers;

import artifacts.registry.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.hat.NightVisionGogglesItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.data.GUIRenderer;
import it.hurts.sskirillss.relics.utils.data.SpriteAnchor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

public class NightVisionGogglesLayer implements LayeredDraw.Layer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "textures/hud/night_vision_goggles_layer_1.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var MC = Minecraft.getInstance();
        var player = MC.player;

        if (player == null)
            return;

        var stack = EntityUtils.findEquippedCurio(player, ModItems.NIGHT_VISION_GOGGLES.value());

        if (!(stack.getItem() instanceof NightVisionGogglesItem relic) || !relic.isAbilityTicking(stack, "vision"))
            return;

        var poseStack = guiGraphics.pose();

        poseStack.pushPose();

        var window = MC.getWindow();

        var width = window.getGuiScaledWidth();
        var height = window.getGuiScaledHeight();

        RenderSystem.enableBlend();

        GUIRenderer.begin(TEXTURE, poseStack)
                .anchor(SpriteAnchor.TOP_LEFT)
                .patternSize(width, height)
                .texSize(width, height)
                .color(0x00ff00)
                .alpha((float) (relic.getStatValue(stack, "vision", "amount") * 0.5F) + 0.075F)
                .end();

        RenderSystem.disableBlend();

        poseStack.popPose();
    }
}