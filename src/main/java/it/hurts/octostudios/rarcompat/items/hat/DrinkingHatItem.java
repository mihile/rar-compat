package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModAttributes;
import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class DrinkingHatItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("drinking")
                                .stat(StatData.builder("speed")
                                        .icon(StatIcons.SPEED)
                                        .initialValue(0.3D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 14, 4).star(1, 19, 9).star(2, 5, 11)
                                        .star(3, 13, 17).star(4, 8, 16).star(5, 4, 25)
                                        .star(6, 12, 26)
                                        .link(0, 1).link(0, 2).link(1, 3).link(4, 2).link(4, 3).link(4, 5).link(4, 6)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("nutrition")
                                .requiredLevel(5)
                                .stat(StatData.builder("hunger")
                                        .icon(StatIcons.SATURATION)
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 14, 2).star(1, 13, 9).star(2, 19, 13)
                                        .star(3, 10, 16).star(4, 19, 20).star(5, 13, 25)
                                        .star(6, 4, 23).star(7, 4, 29)
                                        .link(0, 1).link(1, 2).link(1, 3).link(2, 4).link(4, 5).link(6, 3).link(6, 5).link(6, 7)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 15, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(ModAttributes.DRINKING_SPEED, (float) getStatValue(stack, "drinking", "speed")))
                .build();
    }

    @EventBusSubscriber
    public static class DrinkingHatEvents {
        @SubscribeEvent
        public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
            if (!(event.getEntity() instanceof Player player) || player.getCommandSenderWorld().isClientSide())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.PLASTIC_DRINKING_HAT.value());

            if (stack.isEmpty())
                stack = EntityUtils.findEquippedCurio(player, ModItems.NOVELTY_DRINKING_HAT.value());

            if (!(stack.getItem() instanceof DrinkingHatItem relic) || event.getItem().getUseAnimation() != UseAnim.DRINK)
                return;

            relic.spreadRelicExperience(player, stack, (int) Math.ceil(event.getDuration() / 20F));

            if (!relic.canPlayerUseAbility(player, stack, "nutrition"))
                return;

            int hunger = (int) relic.getStatValue(stack, "nutrition", "hunger");

            player.getFoodData().eat(hunger,  hunger / 2F);
        }
    }
}