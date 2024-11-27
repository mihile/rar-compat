package it.hurts.octostudios.rarcompat.items.hat;

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
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TOGGLED;

public class SnorkelItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("diving")
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 20, 9).star(1, 16, 14).star(2, 6, 10)
                                        .star(3, 12, 20).star(4, 4, 16).star(5, 16, 24)
                                        .star(6, 9, 24)
                                        .link(0, 1).link(1, 2).link(1, 3).link(4, 2).link(4, 3).link(1, 5).link(5, 6).link(6, 4)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff22b818)
                                .borderBottom(0xff00869c)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 10 != 0)
            return;

        var toggled = stack.getOrDefault(TOGGLED, false);

        if (player.isUnderWater()) {
            if (!toggled) {
                stack.set(TOGGLED, true);

                var effect = player.getEffect(MobEffects.WATER_BREATHING);

                var currentDuration = effect != null ? effect.getDuration() : 0;
                var resultDuration = (int) getStatValue(stack, "diving", "duration");

                if (resultDuration > currentDuration) {
                    spreadRelicExperience(player, stack, (int) Math.ceil((resultDuration - currentDuration) / 20F));

                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, resultDuration * 20, 0, true, true));
                }
            }
        } else if (toggled)
            stack.set(TOGGLED, false);
    }
}