package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

public class PowerGloveItem extends WearableRelicItem {
    @Getter
    @Setter
    private long powerTimer;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("power")
                                .stat(StatData.builder("amount")
                                        .initialValue(1D, 1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        this.powerTimer++;
    }

    // @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onPlayerAttack(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.POWER_GLOVE.value());

            if (!(stack.getItem() instanceof PowerGloveItem relic) || relic.getPowerTimer() < 5) return;

            event.setAmount((float) (event.getAmount() * relic.getStatValue(stack, "power", "amount")));
            relic.setPowerTimer(0);
        }

    }
}
