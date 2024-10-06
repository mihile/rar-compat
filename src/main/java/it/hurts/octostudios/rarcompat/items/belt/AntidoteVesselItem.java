package it.hurts.octostudios.rarcompat.items.belt;

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
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.lang.reflect.Field;

public class AntidoteVesselItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("antidote")
                                .stat(StatData.builder("amount")
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class AntidoteVeselEvent {

        @SubscribeEvent
        public static void onMobEffect(MobEffectEvent.Added event) {
            MobEffectInstance effectInstance = event.getEffectInstance();

            if (effectInstance == MobEffects.BAD_OMEN || effectInstance == MobEffects.TRIAL_OMEN
                    || effectInstance.getEffect().value().isBeneficial() || !(event.getEntity() instanceof Player player) || player.level().isClientSide)
                return;

            ItemStack itemStack = EntityUtils.findEquippedCurio(player, ModItems.ANTIDOTE_VESSEL.value());

            if (!(itemStack.getItem() instanceof AntidoteVesselItem relic))
                return;

            int newDuration = (int) (effectInstance.getDuration() * (1 - relic.getStatValue(itemStack, "antidote", "amount")));

            try {
                Field durationField = MobEffectInstance.class.getDeclaredField("duration");

                durationField.setAccessible(true);

                durationField.setInt(effectInstance, newDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
