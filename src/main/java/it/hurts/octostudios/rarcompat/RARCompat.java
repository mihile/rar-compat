package it.hurts.octostudios.rarcompat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(RARCompat.MODID)
public class RARCompat {
    public static final String MODID = "rarcompat";

    public RARCompat(IEventBus bus) {
        bus.addListener(this::fillCreativeTabs);
    }

    public void fillCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModItems.CREATIVE_TAB.get()) {
            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                if (item instanceof WearableRelicItem)
                    event.accept(item);
            }
        }
    }
}