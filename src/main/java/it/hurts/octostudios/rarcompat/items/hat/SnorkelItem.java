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
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SnorkelItem extends WearableRelicItem  {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("diving")
                                .stat(StatData.builder("duration")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.AQUATIC)
                        .build())
                .build();
    }

//    @Override
//    public void curioTick(SlotContext slotContext, ItemStack stack) {
//        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 10 != 0)
//            return;
//
//        var toggled = NBTUtils.getBoolean(stack, "toggled", false);
//
//        if (player.isUnderWater()) {
//            if (!toggled) {
//                NBTUtils.setBoolean(stack, "toggled", true);
//
//                var effect = player.getEffect(MobEffects.WATER_BREATHING);
//
//                var currentDuration = effect != null ? effect.getDuration() : 0;
//                var resultDuration = (int) getAbilityValue(stack, "diving", "duration");
//
//                if (resultDuration > currentDuration) {
//                    addExperience(player, stack, (int) Math.ceil((resultDuration - currentDuration) / 20F));
//
//                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, resultDuration * 20, 0, true, true));
//                }
//            }
//        } else if (toggled)
//            NBTUtils.setBoolean(stack, "toggled", false);
//    }
}