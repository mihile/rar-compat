package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.awt.*;
import java.util.Random;

public class ThornPendantItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("poison")
                                .stat(StatData.builder("multiplier")
                                        .icon(StatIcons.MULTIPLIER)
                                        .initialValue(0.05D, 0.1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value  * 100, 1))
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(2D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.25D, 0.35D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 2))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onReceivingDamage(LivingIncomingDamageEvent event) {
            Entity entityAttacker = event.getSource().getEntity();

            if (!(event.getEntity() instanceof Player player) || !(entityAttacker instanceof LivingEntity attacker))
                return;

            Level level = attacker.level();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.THORN_PENDANT.value());
            Random random = new Random();

            if (!(stack.getItem() instanceof ThornPendantItem relic) || level.isClientSide
                    || random.nextInt(100) > relic.getStatValue(stack, "poison", "chance") * 100) return;

            float multiplier = (float) relic.getStatValue(stack, "poison", "multiplier");
            int time = (int) (relic.getStatValue(stack, "poison", "time") * 20);

            attacker.hurt(event.getSource(), event.getAmount() * multiplier);
            attacker.addEffect(new MobEffectInstance(MobEffects.POISON, time, 1));

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(50 + random.nextInt(50), 200 + random.nextInt(55), 50 + random.nextInt(50)), 0.4F, 20, 0.95F),
                    attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2F, attacker.getZ(), 10, attacker.getBbWidth() / 2F, attacker.getBbHeight() / 2F, attacker.getBbWidth() / 2F, 0.025F);
        }

    }
}
