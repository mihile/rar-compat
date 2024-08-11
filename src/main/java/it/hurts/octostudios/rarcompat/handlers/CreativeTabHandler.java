package it.hurts.octostudios.rarcompat.handlers;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.base.WearableRelicItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CreativeTabHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModItems.CREATIVE_TAB.get()) {
            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                if (item instanceof WearableRelicItem)
                    event.accept(item);
            }
        }
    }
}