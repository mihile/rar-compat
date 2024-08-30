package it.hurts.octostudios.rarcompat.items.arm;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class UmbrellaItemItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glider")
                                .stat(StatData.builder("fall_speed")
                                        .initialValue(0.4D, 0.6D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("shield")
                                .stat(StatData.builder("knockback")
                                        .initialValue(2.0D, 1.5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(StatData.builder("passive_knockback")
                                        .initialValue(1.0D, 0.5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onEntityHurt(LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();

            if ((entity instanceof Player player) && player.isUsingItem() && player.getUseItem().getItem() instanceof artifacts.item.UmbrellaItem) {
                DamageSource source = event.getEntity().getLastDamageSource();
                Entity target = source.getEntity();

                if (target instanceof LivingEntity) {
                    Vec3 kb = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ()).normalize()
                            .scale(((IRelicItem) player.getUseItem().getItem()).getStatValue(player.getUseItem(), "shield", "passive_knockback"));
                    target.setDeltaMovement(target.getDeltaMovement().add(kb));
                }
            }
        }
    }
}
