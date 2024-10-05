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
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import top.theillusivec4.curios.api.SlotContext;

public class FeralClawsItem extends WearableRelicItem {
    @Getter
    @Setter
    private long resetTimer;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("claws")
                                .stat(StatData.builder("amount")
                                        .initialValue(2D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0 || !stack.getOrDefault(DataComponentRegistry.TOGGLED, false))
            return;

        this.resetTimer++;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack || !(slotContext.entity() instanceof Player player)) return;

        EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    //  @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onPlayerAttack(AttackEntityEvent event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.FERAL_CLAWS.value());

            if (!(stack.getItem() instanceof FeralClawsItem relic)) return;

            if (relic.getResetTimer() <= 3)
                EntityUtils.applyAttribute(player, stack, Attributes.ATTACK_SPEED, (float) relic.getStatValue(stack, "claws", "amount"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            else {
                stack.set(DataComponentRegistry.TOGGLED, false);
                EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            }

            stack.set(DataComponentRegistry.TOGGLED, true);
            relic.setResetTimer(0);

            player.displayClientMessage(Component.literal(String.valueOf(player.getAttribute(Attributes.ATTACK_SPEED).getValue())), true);
        }

    }
}

