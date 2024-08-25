package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.base.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class SuperstitiousHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("superstitious")
                                .stat(StatData.builder("chance")
                                        .initialValue(20D, 90D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 10D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            Entity attacker = event.getSource().getEntity();

            if (!(attacker instanceof Player player)) return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SUPERSTITIOUS_HAT.value());

            if (!(stack.getItem() instanceof SuperstitiousHatItem relic))
                return;

            for (ItemEntity itemEntity : event.getDrops())
                itemEntity.getItem().grow(MathUtils.multicast(player.level().getRandom(), relic.getStatValue(stack, "superstitious", "chance"), 0.4));

        }
    }
}