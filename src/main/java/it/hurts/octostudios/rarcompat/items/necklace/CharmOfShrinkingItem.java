package it.hurts.octostudios.rarcompat.items.necklace;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class CharmOfShrinkingItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("shrinking")
                                .active(CastData.builder().type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(8D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2)
                                        .formatValue(value -> (int) MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.SCULK)
                        .build())
                .build();
    }

    @Nullable
    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        if (isAbilityTicking(stack, "shrinking"))
            return RelicAttributeModifier.builder()
                    .attribute(new RelicAttributeModifier.Modifier(Attributes.SCALE, -0.5F))
                    .build();

        return super.getRelicAttributeModifiers(stack);
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (stage == CastStage.START)
            playSound(player, SoundEvents.PUFFER_FISH_BLOW_OUT);
        else if (stage == CastStage.END) {
            playSound(player, SoundEvents.PUFFER_FISH_BLOW_UP);

            setAbilityCooldown(stack, "shrinking", 200);

            this.removeAttribute(player, stack);
        }

        if (player.tickCount % 20 == 0)
            setCurrentTick(stack, 1);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        double time = getStatValue(stack, "shrinking", "time");

        if (getCurrentTick(stack) >= time) {
            playSound(player, SoundEvents.PUFFER_FISH_BLOW_UP);

            setAbilityCooldown(stack, "shrinking", 200);

            setCurrentTick(stack, -getCurrentTick(stack));

            removeAttribute(player, stack);
        }

        if (player.tickCount % 20 == 0 && isAbilityTicking(stack, "shrinking"))
            spreadRelicExperience(player, stack, 1);
    }

    public void playSound(Player player, SoundEvent events) {
        player.playSound(events, 1F, 0.75F + new Random().nextFloat(1) * 0.5F);
    }

    public void setCurrentTick(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, getCurrentTick(stack) + val);
    }

    public int getCurrentTick(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    private void removeAttribute(LivingEntity entity, ItemStack stack) {
        EntityUtils.removeAttribute(entity, stack, Attributes.SCALE, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
