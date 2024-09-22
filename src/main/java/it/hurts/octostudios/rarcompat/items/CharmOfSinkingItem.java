package it.hurts.octostudios.rarcompat.items;

import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class CharmOfSinkingItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("immersion")
                                .stat(StatData.builder("air")
                                        .initialValue(1D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        if (!(slotContext.entity() instanceof Player player)) return;

        if (player.isEyeInFluid(FluidTags.WATER)) {
            if (player.onGround()) {
                int air = player.getAirSupply();

                if (air < player.getMaxAirSupply())
                    player.setAirSupply((int) Math.min(air + getStatValue(stack, "immersion", "air"), player.getMaxAirSupply()));
            }
        }
    }
}
