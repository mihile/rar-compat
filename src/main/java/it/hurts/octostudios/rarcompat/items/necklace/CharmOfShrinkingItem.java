package it.hurts.octostudios.rarcompat.items.necklace;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class CharmOfShrinkingItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("devouring")
                                .active(CastData.builder().type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("frequency")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(120D, 140D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.071)
                                        .formatValue(value -> MathUtils.round(value / 20, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.SCULK)
                        .build())
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack)
            return;

        this.removeAttribute(slotContext.entity(), stack);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("devouring") && player.level().isClientSide) {
            if (stage == CastStage.START)
                EntityUtils.applyAttribute(player, stack, Attributes.SCALE, -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            else if (stage == CastStage.END)
                player.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1F, 0.75F + new Random().nextFloat(1) * 0.5F);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (isAbilityTicking(stack, "devouring")) {
            EntityUtils.applyAttribute(player, stack, Attributes.SCALE, -0.5F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        } else {
            this.removeAttribute(player, stack);
        }

    }

    private void removeAttribute(LivingEntity entity, ItemStack stack) {
        EntityUtils.removeAttribute(entity, stack, Attributes.SCALE, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
