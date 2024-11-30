package it.hurts.octostudios.rarcompat.items.hands;

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
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class PocketPistonItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("discarding")
                                .stat(StatData.builder("range")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 11, 27).star(1, 11, 23).star(2, 4, 19).star(3, 4, 23)
                                        .star(4, 18, 19).star(5, 18, 23).star(6, 11, 15).star(7, 11, 6)
                                        .star(8, 4, 10).star(9, 18, 10)
                                        .link(0, 1).link(1, 2).link(1, 4).link(3, 0).link(0, 5).link(5, 4).link(2, 3).link(1, 6).link(2, 6)
                                        .link(4, 6).link(3, 0).link(0, 5).link(5, 4).link(2, 3).link(6, 8).link(6, 9).link(7, 8).link(7, 9)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("attacking")
                                .stat(StatData.builder("interaction")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 11, 27).star(1, 11, 23).star(2, 4, 19).star(3, 4, 23)
                                        .star(4, 18, 19).star(5, 18, 23).star(6, 11, 15).star(7, 11, 6)
                                        .star(8, 4, 10).star(9, 18, 10)
                                        .link(0, 1).link(1, 2).link(1, 4).link(3, 0).link(0, 5).link(5, 4).link(2, 3).link(1, 6).link(2, 6)
                                        .link(4, 6).link(3, 0).link(0, 5).link(5, 4).link(2, 3).link(6, 8).link(6, 9).link(7, 8).link(7, 9)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffcb9848)
                                .borderBottom(0xff6a6a6a)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.PILLAGE)
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        float modifier = (float) getStatValue(stack, "discarding", "range");

        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.ENTITY_INTERACTION_RANGE, modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .attribute(new RelicAttributeModifier.Modifier(Attributes.BLOCK_INTERACTION_RANGE, modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL))
                .build();
    }

    @EventBusSubscriber
    public static class PocketPistonEvent {

        @SubscribeEvent
        public static void onAttacking(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.POCKET_PISTON.value());

            if (!(event.getTarget() instanceof LivingEntity target) || !(stack.getItem() instanceof PocketPistonItem relic))
                return;

            float modifier = (float) relic.getStatValue(stack, "attacking", "interaction");

            Vec3 toEntity = target.position().subtract(player.position()).normalize().scale(modifier);

            target.setDeltaMovement(toEntity.x, toEntity.y / 2, toEntity.z);
        }
    }
}
