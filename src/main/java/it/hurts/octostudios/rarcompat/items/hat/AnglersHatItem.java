package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.neoforge.data.LootTables;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

import java.util.List;

public class AnglersHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("catch")
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.01D, 0.09D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.6D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onItemFished(ItemFishedEvent event) {
            Player player = event.getEntity();
            Level level = player.level();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ANGLERS_HAT.value());

            if (!(stack.getItem() instanceof AnglersHatItem relic))
                return;

            if (level instanceof ServerLevel serverLevel) {
                LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);

                LootParams lootparams = new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .withParameter(LootContextParams.TOOL, stack)
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .create(LootContextParamSets.FISHING);

                int rolls = MathUtils.multicast(level.getRandom(), relic.getStatValue(stack, "catch", "chance"), 1F);
                System.out.println(rolls + " GEGQ");
                for (int i = 0; i < rolls; i++) {
                    if (level.getRandom().nextFloat() < 0.5f) {
                        for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                            ItemEntity entity = new ItemEntity(serverLevel, player.getX(), player.getY(), player.getZ(), loot);
                            serverLevel.addFreshEntity(entity);
                        }
                    }
                }
            }
        }
    }
}