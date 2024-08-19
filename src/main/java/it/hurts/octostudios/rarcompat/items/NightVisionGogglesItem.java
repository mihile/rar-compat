package it.hurts.octostudios.rarcompat.items;

import it.hurts.octostudios.rarcompat.items.base.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
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
                        .ability(AbilityData.builder("night_vision")
                        .active(CastData.builder()
                                .type(CastType.TOGGLEABLE)
                                .build())
                        .stat(StatData.builder("brightness_amount")
                                .initialValue(0.005D, 5D)
                                .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                .formatValue(value -> MathUtils.round(value, 1))
                                .build())
                        .build())

                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player)
            player.removeEffect(MobEffects.NIGHT_VISION);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (isAbilityTicking(stack, "night_vision"))
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 200, 0, true, false));
        else
            player.removeEffect(MobEffects.NIGHT_VISION);
    }
}