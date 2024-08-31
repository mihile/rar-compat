package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

public class AnglersHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("catch")
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(1D, 9D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {
        // TODO: Cause additional loot table rolls instead of just increasing the stack size
        @SubscribeEvent
        public static void onItemFished(ItemFishedEvent event) {
            Player player = event.getEntity();
            Level level = player.level();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ANGLERS_HAT.value());

            if (!(stack.getItem() instanceof AnglersHatItem relic))
                return;

            for (ItemStack itemStack : event.getDrops())
                itemStack.grow(MathUtils.multicast( level.getRandom(), relic.getStatValue(stack, "catch", "chance"), 0.4));

        }
    }
}