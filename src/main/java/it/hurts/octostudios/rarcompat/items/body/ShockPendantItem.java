package it.hurts.octostudios.rarcompat.items.body;

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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.awt.*;
import java.util.Random;

public class ShockPendantItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("lightning")
                                .stat(StatData.builder("damage")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("chance")
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
        public static void onReceivingDamage(LivingIncomingDamageEvent event) {
            Entity attacker = event.getSource().getEntity();

            if (!(event.getEntity() instanceof Player player) || attacker == null)
                return;

            Level level = attacker.level();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SHOCK_PENDANT.value());

            if (!(stack.getItem() instanceof ShockPendantItem relic) || level.isClientSide) return;
            Random random = new Random();

            if (random.nextInt(100) < relic.getStatValue(stack, "lightning", "chance")) {
                LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);

                lightningBolt.setPos(attacker.position());
                lightningBolt.setDamage((float) relic.getStatValue(stack, "lightning", "damage"));
                level.addFreshEntity(lightningBolt);

                ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.4F, 20, 0.95F),
                        attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2F, attacker.getZ(), 10, attacker.getBbWidth() / 2F, attacker.getBbHeight() / 2F, attacker.getBbWidth() / 2F, 0.025F);
            }
        }
    }
}
