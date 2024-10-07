package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
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
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import top.theillusivec4.curios.api.SlotContext;

public class FeralClawsItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("claws")
                                .stat(StatData.builder("amount")
                                        .icon(StatIcons.MULTIPLIER)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.07D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.NETHER)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        stack.set(DataComponentRegistry.COUNT, stack.getOrDefault(DataComponentRegistry.COUNT, 0) + 1);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack || !(slotContext.entity() instanceof Player player)) return;

        EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @EventBusSubscriber
    public static class FeralClawsEvent {

        @SubscribeEvent
        public static void onPlayerAttack(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.FERAL_CLAWS.value());

            if (!(stack.getItem() instanceof FeralClawsItem relic)) return;
            int currentStacks = stack.getOrDefault(DataComponentRegistry.COUNT, 0);

            if (player.getAttackStrengthScale(0.5F) >= 1.0F || currentStacks <= 3) {
                stack.set(DataComponentRegistry.COUNT, currentStacks + 1);

                relic.spreadRelicExperience(player, stack, 1);

                EntityUtils.applyAttribute(player, stack, Attributes.ATTACK_SPEED, (float) relic.getStatValue(stack, "claws", "amount"), AttributeModifier.Operation.ADD_VALUE);
            } else {
                stack.set(DataComponentRegistry.COUNT, 0);
                EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE);
            }

        }

    }
}

