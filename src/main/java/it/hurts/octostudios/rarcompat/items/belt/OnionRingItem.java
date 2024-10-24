package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Random;

public class OnionRingItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("onion")
                                .stat(StatData.builder("amount")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100, 0))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ONION_RING.value());

            if (!(stack.getItem() instanceof OnionRingItem relic))
                return;

            int currentHunger = player.getFoodData().getFoodLevel();
            double modifier = relic.getStatValue(stack, "onion", "amount");

            event.setNewSpeed((float) (event.getNewSpeed() + (event.getNewSpeed() * (currentHunger * modifier))));
        }

        @SubscribeEvent
        public static void onBlockDestroy(BlockEvent.BreakEvent event) {
            Player player = event.getPlayer();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ONION_RING.value());

            if (!(stack.getItem() instanceof OnionRingItem relic))
                return;

            float hardness = event.getState().getDestroySpeed(player.level(), player.blockPosition());
            float currentHunger = player.getFoodData().getFoodLevel();

            if (hardness >= 0.5 && currentHunger / 20 >= new Random().nextFloat(1))
                relic.spreadRelicExperience(player, stack, 1);
        }
    }
}
