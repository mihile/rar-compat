package it.hurts.octostudios.rarcompat;

import it.hurts.octostudios.rarcompat.init.ConfigRegistry;
import it.hurts.octostudios.rarcompat.init.SoundRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(RARCompat.MODID)
public class RARCompat {
    public static final String MODID = "rarcompat";

    public RARCompat(IEventBus bus) {
        bus.addListener(this::setupCommon);

        SoundRegistry.register(bus);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        ConfigRegistry.register();
    }

}