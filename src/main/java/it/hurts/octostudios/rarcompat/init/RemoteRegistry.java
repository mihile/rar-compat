package it.hurts.octostudios.rarcompat.init;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.client.layers.NightVisionGogglesLayer;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = RARCompat.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RemoteRegistry {
    @SubscribeEvent
    public static void onOverlayRegistry(RegisterGuiLayersEvent event) {
        event.registerBelowAll(ResourceLocation.fromNamespaceAndPath(Reference.MODID, "night_vision_goggles"), new NightVisionGogglesLayer());
    }
}