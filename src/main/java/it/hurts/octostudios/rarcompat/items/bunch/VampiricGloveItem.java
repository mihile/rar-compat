package it.hurts.octostudios.rarcompat.items.bunch;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class VampiricGloveItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("vampire")
                                .stat(StatData.builder("amount")
                                        .initialValue(10D, 90D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 9D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    // @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onAttack(AttackEntityEvent event) {
            if (event.getEntity() instanceof Player player && event.getTarget() instanceof LivingEntity) {
                ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.VAMPIRIC_GLOVE.value());

                if (!(stack.getItem() instanceof VampiricGloveItem relic)) return;

                double damageToHeal = event.getEntity().getAttackStrengthScale(0.5F) * relic.getStatValue(stack, "vampire", "amount");

                player.heal((float) damageToHeal);

            }
        }
    }
}
