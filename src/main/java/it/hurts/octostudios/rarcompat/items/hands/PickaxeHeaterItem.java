package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Objects;
import java.util.Optional;

public class PickaxeHeaterItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("heater")
                                .stat(StatData.builder("heat")
                                        .icon(StatIcons.SIZE)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .stat(StatData.builder("modifier")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.2D, 0.3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 11, 28).star(1, 11, 18).star(2, 19, 24)
                                        .star(3, 11, 6).star(4, 4, 9).star(5, 18, 9)
                                        .link(0, 1).link(1, 2).link(3, 1).link(3, 4).link(3, 5)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0)
            return;
        addTime(stack, 1);


    }

    public static void addTime(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, getTime(stack) + val);
    }

    public static int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    @EventBusSubscriber
    public static class PickaxeHeaterEvent {

        @SubscribeEvent
        public static void onPlayerAttack(BlockDropsEvent event) {
            if (!(event.getBreaker() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.PICKAXE_HEATER.value());

            if (!(stack.getItem() instanceof PickaxeHeaterItem relic))
                return;

            Level level = player.level();
            BlockPos pos = event.getPos();

            for (ItemStack itemStack : getSmeltingResult(new ItemStack(event.getState().getBlock()), (ServerLevel) level).stream().toList()) {
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
                level.addFreshEntity(itemEntity);

                event.setCanceled(true);
            }
        }

        public static Optional<ItemStack> getSmeltingResult(ItemStack stack, ServerLevel level) {
            if (stack.isEmpty()) {
                return Optional.empty();
            }

            Optional<RecipeHolder<SmeltingRecipe>> optionalRecipe =
                    Objects.requireNonNull(level.getServer()).getRecipeManager()
                            .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

            return optionalRecipe.map(recipeHolder -> recipeHolder.value().getResultItem(level.registryAccess()));
        }
    }

}
