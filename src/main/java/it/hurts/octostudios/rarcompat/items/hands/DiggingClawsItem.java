package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.ability.UpgradeToolTierAbility;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModItems;
import artifacts.util.AbilityHelper;
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
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Getter;
import net.minecraft.references.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class DiggingClawsItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("claws")
                                .stat(StatData.builder("amount")
                                        .icon(StatIcons.COUNT)
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 4, 15).star(1, 12, 3).star(2, 6, 19).star(3, 16, 6)
                                        .star(4, 9, 23).star(5, 17, 12).star(6, 13, 24).star(7, 17, 18)
                                        .link(0, 1).link(2, 3).link(4, 5).link(6, 7)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("passive")
                                .maxLevel(0)
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff0c71e0)
                                .borderBottom(0xff151989)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class DiggingClawsEvent {
        @SubscribeEvent
        private static void onDiggingClawsHarvestCheck(PlayerEvent.HarvestCheck event) {
            BlockState blockState = event.getTargetBlock();
            Player player = event.getEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.DIGGING_CLAWS.value());

            if (!(stack.getItem() instanceof DiggingClawsItem relic))
                return;

            if (player.getMainHandItem().getItem() instanceof TieredItem tieredItem) {
                if (relic.canPlayerUseAbility(player, stack, "passive"))
                    event.setCanHarvest(isCorrectTierForDrops(getTierFromString((Tiers) tieredItem.getTier()), blockState));
            } else if (!event.getTargetBlock().is(BlockTags.INCORRECT_FOR_WOODEN_TOOL)) {
                event.setCanHarvest(true);
            }
        }

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.DIGGING_CLAWS.value());

            if (!(stack.getItem() instanceof DiggingClawsItem relic))
                return;

            event.setNewSpeed((float) (event.getOriginalSpeed() + relic.getStatValue(stack, "claws", "amount")));
        }

        @SubscribeEvent
        public static void onBlockDestroy(BlockEvent.BreakEvent event) {
            Player player = event.getPlayer();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.DIGGING_CLAWS.value());

            if (!(stack.getItem() instanceof DiggingClawsItem relic))
                return;

            float hardness = event.getState().getDestroySpeed(player.level(), player.blockPosition());
            Random random = new Random();

            if (random.nextFloat(1) <= (hardness / 20))
                relic.spreadRelicExperience(player, stack, 1);
        }

        public static int getTierFromString(Tiers tier) {
            return switch (tier) {
                case Tiers.WOOD -> 1;
                case Tiers.STONE -> 2;
                case Tiers.IRON -> 3;
                case Tiers.GOLD -> 4;
                case Tiers.DIAMOND -> 5;
                case Tiers.NETHERITE -> 6;
            };
        }

        public static boolean isCorrectTierForDrops(int i, BlockState state) {
            if (!state.requiresCorrectToolForDrops()) {
                return true;
            }

            if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
                return i >= 4;
            } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
                return i >= 2;
            } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
                return i >= 1;
            }

            return false;
        }
    }
}