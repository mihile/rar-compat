package it.hurts.octostudios.rarcompat;

import it.hurts.octostudios.rarcompat.init.SoundRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RARCompat.MODID)
public class RARCompat {
    public static final String MODID = "rarcompat";

    public RARCompat(IEventBus bus) {
        SoundRegistry.register(bus);
    }
}