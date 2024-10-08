package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.items.necklace.FlamePendantItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
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
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.awt.*;
import java.util.Random;

public class WitheredBraceletItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("withered")
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(2D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
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

    @EventBusSubscriber
    public static class WitheredBraceletEvent {

        @SubscribeEvent
        public static void onReceivingDamage(AttackEntityEvent event) {
            Entity prey = event.getTarget();
            Player player = event.getEntity();

            if (!(prey instanceof LivingEntity attacker) || attacker == player)
                return;

            Level level = attacker.level();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.WITHERED_BRACELET.value());

            Random random = new Random();

            if (!(stack.getItem() instanceof WitheredBraceletItem relic) || level.isClientSide
                    || random.nextFloat(1) > relic.getStatValue(stack, "withered", "chance"))
                return;

            int duration = (int) relic.getStatValue(stack, "withered", "time") * 20;

            attacker.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 1));

            relic.spreadRelicExperience(player, stack, 1);

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(75, 0, 130), 0.9F, 60, 0.95F),
                    attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2F, attacker.getZ(), 10, attacker.getBbWidth() / 2F, attacker.getBbHeight() / 2F, attacker.getBbWidth() / 2F, 0.025F);
        }
    }
}
