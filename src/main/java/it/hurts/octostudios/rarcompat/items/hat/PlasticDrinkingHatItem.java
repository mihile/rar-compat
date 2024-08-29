package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModAttributes;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import top.theillusivec4.curios.api.SlotContext;

public class PlasticDrinkingHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("quick_sip")
                                .stat(StatData.builder("drinking_speed")
                                        .initialValue(1.1D, 1.5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("nutritional_sip")
                                .stat(StatData.builder("hunger")
                                        .initialValue(1.0D, 4.0D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.05D)
                                        .formatValue(value -> MathUtils.round(value, 0))
                                        .build())
                                .stat(StatData.builder("saturation")
                                        .initialValue(0.6D, 4.8D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, ModAttributes.DRINKING_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        EntityUtils.applyAttribute(player, stack, ModAttributes.DRINKING_SPEED, (float) getStatValue(stack, "quick_sip", "drinking_speed") - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        if (player.isUsingItem() && player.getUseItem().getUseAnimation() == UseAnim.DRINK)
            player.getFoodData().eat((int) getStatValue(stack, "nutritional_sip", "hunger"),
                    (float) getStatValue(stack, "nutritional_sip", "saturation"));
    }
}
