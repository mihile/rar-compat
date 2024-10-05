package it.hurts.octostudios.rarcompat.items.hat;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class NightVisionGogglesItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("vision")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("amount")
                                        .initialValue(0.02D, 0.05)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 1000, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 10, 25).star(1, 10, 12).star(2, 6, 7).star(3, 16, 7)
                                        .star(4, 2, 13).star(5, 6, 16).star(6, 20, 13).star(7, 16, 16)
                                        .link(0, 6).link(6, 3).link(3, 1).link(1, 2).link(2, 4).link(4, 5).link(4, 0).link(7, 6).link(7, 1).link(5, 1)
                                        .build())
                                .build())

                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack || !(slotContext.entity() instanceof Player player)) return;

        player.removeEffect(MobEffects.NIGHT_VISION);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (isAbilityTicking(stack, "vision"))
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, false, false));
        else
            player.removeEffect(MobEffects.NIGHT_VISION);
    }
}