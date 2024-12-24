package it.hurts.octostudios.rarcompat.handlers;

import artifacts.entity.MimicEntity;
import it.hurts.octostudios.rarcompat.config.MimicConfigData;
import it.hurts.octostudios.rarcompat.init.ConfigRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class MimicHandler {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static void readFromConfig(MimicConfigData data) {
        var entries = data.getItems();

        MimicHandler.ITEMS.clear();

        if (entries.isEmpty())
            return;

        for (var entry : entries) {
            var id = ResourceLocation.parse(entry);

            if (!BuiltInRegistries.ITEM.containsKey(id))
                continue;

            MimicHandler.ITEMS.add(BuiltInRegistries.ITEM.get(id));
        }
    }

    public static void init() {
        readFromConfig(ConfigRegistry.MIMIC_CONFIG);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (ITEMS.isEmpty() || !(event.getEntity() instanceof MimicEntity entity))
            return;

        var level = entity.getCommandSenderWorld();
        var random = level.getRandom();

        event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), ITEMS.get(random.nextInt(ITEMS.size())).getDefaultInstance()));
    }
}