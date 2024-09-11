package it.hurts.octostudios.rarcompat.items.hat;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
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
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class AnglersHatEvents {
        @SubscribeEvent
        public static void onItemFished(ItemFishedEvent event) {
            Player player = event.getEntity();
            Level level = player.getCommandSenderWorld();

            FishingHook fishingHook = event.getHookEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ANGLERS_HAT.value());

            if (!(stack.getItem() instanceof AnglersHatItem relic) || level.isClientSide())
                return;

            ServerLevel serverLevel = (ServerLevel) level;
            RandomSource random = serverLevel.getRandom();

            int rolls = MathUtils.multicast(random, relic.getStatValue(stack, "catch", "chance"), 1F);

            LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);

            LootParams lootparams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.TOOL, stack)
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .create(LootContextParamSets.FISHING);

            for (int i = 0; i < rolls; i++) {
                List<ItemStack> drop = loottable.getRandomItems(lootparams);

                relic.spreadRelicExperience(player, stack, drop.size());

                for (ItemStack itemstack : drop) {
                    ItemEntity itementity = new ItemEntity(serverLevel, fishingHook.getX(), fishingHook.getY(), fishingHook.getZ(), itemstack);

                    double x = player.getX() - fishingHook.getX();
                    double y = player.getY() - fishingHook.getY();
                    double z = player.getZ() - fishingHook.getZ();

                    itementity.setDeltaMovement(x * 0.1, y * 0.1 + Math.sqrt(Math.sqrt(x * x + y * y + z * z)) * 0.08, z * 0.1);

                    serverLevel.addFreshEntity(itementity);

                    serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, player.getX(), player.getY() + 0.5, player.getZ() + 0.5, random.nextInt(6) + 1));
                }
            }
        }
    }
}