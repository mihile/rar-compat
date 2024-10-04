package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.data.worldgen.AncientCityStructurePieces;
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
                                        .icon(StatIcons.DEALT_DAMAGE)
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.15D, 0.25D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 2))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry("minecraft:chests/ancient_city", 0.5F)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onReceivingDamage(LivingIncomingDamageEvent event) {
            Entity attacker = event.getSource().getEntity();

            if (!(event.getEntity() instanceof Player player) || attacker == null || attacker == player)
                return;

            Random random = new Random();
            Level level = attacker.level();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SHOCK_PENDANT.value());

            if (!(stack.getItem() instanceof ShockPendantItem relic) || level.isClientSide
                    || random.nextInt(100) > relic.getStatValue(stack, "lightning", "chance") * 100) return;

            relic.spreadRelicExperience(player, stack, 1);

            LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);

            lightningBolt.setVisualOnly(true);
            lightningBolt.setPos(attacker.position());

            level.addFreshEntity(lightningBolt);

            attacker.hurt(lightningBolt.damageSources().lightningBolt(), (float) relic.getStatValue(stack, "lightning", "damage"));

            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.4F, 30, 0.95F),
                    attacker.getX(), attacker.getY() + attacker.getBbHeight() / 2F, attacker.getZ(), 10, attacker.getBbWidth() / 2F, attacker.getBbHeight() / 2F, attacker.getBbWidth() / 2F, 0.025F);
        }
    }
}
