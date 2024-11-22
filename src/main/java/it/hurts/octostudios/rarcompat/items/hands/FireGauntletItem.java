package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.items.belt.ObsidianSkullItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class FireGauntletItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("arson")
                                .stat(StatData.builder("sector")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(0.8D, 0.9D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(2D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 4, 15).star(1, 12, 3).star(2, 6, 19).star(3, 16, 6)
                                        .star(4, 9, 23).star(5, 17, 12).star(6, 13, 24).star(7, 17, 18)
                                        .link(0, 1).link(2, 3).link(4, 5).link(6, 7)
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
    public static class FireGauntletEvent {

        @SubscribeEvent
        public static void onAttack(AttackEntityEvent event) {
            Player player = event.getEntity();

            if (!(event.getTarget() instanceof LivingEntity target) || player.level().isClientSide)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.FIRE_GAUNTLET.value());

            if (!(stack.getItem() instanceof FireGauntletItem relic))
                return;

            double attackRange = Objects.requireNonNull(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE)).getValue();
            double sector = relic.getStatValue(stack, "arson", "sector") * 10;
            int flameTime = (int) relic.getStatValue(stack, "arson", "time");

            for (LivingEntity entity : findMobsInCone(player, attackRange, sector * 10))
                entity.setRemainingFireTicks(flameTime * 20);

            spawnDirectionalArc(player, sector, attackRange);
        }

        private static void spawnDirectionalArc(LivingEntity player, double arcAngle, double rangeAttack) {
            Level level = player.level();

            double centerX = player.getX();
            double centerY = player.getY() + 1;
            double centerZ = player.getZ();

            double centerAngle = Math.toRadians(player.getYRot()) + 20.5;

            double startAngle = centerAngle - Math.toRadians(arcAngle);
            double endAngle = centerAngle + Math.toRadians(arcAngle);

            Random random = new Random();

            for (double d = 0; d <= rangeAttack; d += 0.4) {
                int particleCount = (int) (arcAngle / 2 + d);

                for (int i = 0; i <= particleCount; i++) {
                    double angleStep = (endAngle - startAngle) / particleCount;

                    double angle = startAngle + angleStep * i;
                    double x = centerX + d * Math.cos(angle);
                    double z = centerZ + d * Math.sin(angle);

                    ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                                    new Color(200 + random.nextInt(56), random.nextInt(100), random.nextInt(20)),
                                    0.8F, 5, 0.9F),
                            x, centerY, z,
                            0, 0, 0, 0, 0);
                }
            }
        }

        public static List<LivingEntity> findMobsInCone(Player player, double attackRange, double angle) {
            double playerY = player.getY();

            return player.level().getEntities(player, new AABB(player.blockPosition()).inflate(attackRange), entity -> entity instanceof LivingEntity).stream()
                    .filter(entity -> {
                        double entityY = entity.getY();
                        if (entityY < playerY - 2 || entityY > playerY + 2) return false;
                        return player.getLookAngle().normalize().dot(entity.position().subtract(player.position()).normalize()) > Math.cos(Math.toRadians(angle) * 0.5);
                    })
                    .map(entity -> (LivingEntity) entity)
                    .collect(Collectors.toList());
        }
    }
}
