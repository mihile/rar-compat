package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.Optional;
import java.util.Random;

public class PickaxeHeaterItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("heater")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .icon(StatIcons.CAPACITY)
                                        .initialValue(7D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 0))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 11, 28).star(1, 11, 18).star(2, 19, 24)
                                        .star(3, 11, 6).star(4, 4, 9).star(5, 18, 9)
                                        .link(0, 1).link(1, 2).link(3, 1).link(3, 4).link(3, 5)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff939ca2)
                                .borderBottom(0xff696b7c)
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
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 100 != 0)
            return;

        if (MathUtils.round(getStatValue(stack, "heater", "capacity"), 0) >= getCharges(stack))
            addCharge(stack, 1);
    }

    public static void addCharge(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.CHARGE, getCharges(stack) + val);
    }

    public static int getCharges(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
    }

    @EventBusSubscriber
    public static class PickaxeHeaterEvent {

        @SubscribeEvent
        public static void onPlayerAttack(BlockDropsEvent event) {
            if (!(event.getBreaker() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.PICKAXE_HEATER.value());

            if (!(stack.getItem() instanceof PickaxeHeaterItem relic) || !relic.isAbilityTicking(stack, "heater") || getCharges(stack) <= 1)
                return;

            Level level = player.level();
            System.out.println(getCharges(stack));
            for (ItemEntity itemStack : event.getDrops()) {
                ItemStack smeltingItem = getSmeltingResult(itemStack.getItem(), (ServerLevel) level);

                if (smeltingItem.is(ItemStack.EMPTY.getItem()))
                    return;

                BlockPos pos = event.getPos();

                spawnBurstParticles(level, pos);

                addCharge(stack, -1);

                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, smeltingItem));

                relic.spreadRelicExperience(player, stack, 1);

                event.setCanceled(true);
            }
        }

        public static void spawnBurstParticles(Level level, BlockPos centerPos) {
            double centerX = centerPos.getX() + 0.5;
            double centerY = centerPos.getY() + 0.5;
            double centerZ = centerPos.getZ() + 0.5;

            Random random = new Random();

            for (int i = 0; i < 25; i++) {
                ((ServerLevel) level).sendParticles(
                        ParticleUtils.constructSimpleSpark(
                                new Color(150 + random.nextInt(106), random.nextInt(50), 50 + random.nextInt(51), 255),
                                0.6F, 20, 0.85F),
                        centerX, centerY, centerZ,
                        1,
                        0.3, 0.3, 0.3,
                        0.01);
            }
        }

        public static ItemStack getSmeltingResult(ItemStack stack, ServerLevel level) {
            for (RecipeHolder<SmeltingRecipe> recipeHolder : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
                SmeltingRecipe recipe = recipeHolder.value();

                if (recipe.matches(new SingleRecipeInput(stack), level))
                    return recipe.getResultItem(level.registryAccess());
            }

            return ItemStack.EMPTY;
        }
    }
}
