package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CharmOfSinkingItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("dive")
                                .maxLevel(0)
                                .build())
                        .ability(AbilityData.builder("immersion")
                                .stat(StatData.builder("air")
                                        .initialValue(1D, 3D) // TODO: Use actual percent value or either real time value instead of current implementation
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1)
                                        .formatValue(value -> (int) MathUtils.round(value * 10, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 12, 9).star(1, 9, 14).star(2, 16, 14)
                                        .star(3, 9, 19).star(4, 4, 24).star(5, 12, 26)
                                        .link(0, 1).link(1, 2).link(1, 3).link(1, 3).link(3, 4).link(3, 5)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff3c7090)
                                .borderBottom(0xff1c212d)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("immersion")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (player.isUnderWater())
            EntityUtils.applyAttribute(player, stack, Attributes.GRAVITY, 2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        else
            EntityUtils.removeAttribute(player, stack, Attributes.GRAVITY, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack.getItem() == stack.getItem() || !(slotContext.entity() instanceof Player player))
            return;

        EntityUtils.removeAttribute(player, stack, Attributes.GRAVITY, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @EventBusSubscriber
    public static class CharmOfSinkingEvent {
        @SubscribeEvent
        public static void onBreathe(LivingBreatheEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            var stack = EntityUtils.findEquippedCurio(player, ModItems.CHARM_OF_SINKING.value());

            if (!(stack.getItem() instanceof CharmOfSinkingItem relic) || !player.isUnderWater()
                    || !relic.isAbilityUnlocked(stack, "immersion") || !player.onGround())
                return;

            var tickCount = player.tickCount;

            if (tickCount % 20 == 0 && player.onGround() && player.getAirSupply() > 1) // TODO: Do not duplicate conditional blocks
                relic.spreadRelicExperience(player, stack, 1);

            // TODO: Do not hard-lock 'else' block since it may disable other mods functionality
            if (tickCount % (int) relic.getStatValue(stack, "immersion", "air") == 0) {
                event.setConsumeAirAmount(1);
            } else
                event.setConsumeAirAmount(0);
        }
    }
}
