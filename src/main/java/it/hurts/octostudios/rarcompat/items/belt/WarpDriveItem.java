package it.hurts.octostudios.rarcompat.items.belt;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.PredicateType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class WarpDriveItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("teleport")
                                .active(CastData.builder().type(CastType.INSTANTANEOUS)
//                                        .predicate("teleport", PredicateType.CAST, (player, stack) -> {
//                                            BlockPos pos = getHitResult(player, stack);
//
//                                            if (pos == null)
//                                                return false;
//
//                                            return player.position().distanceTo(pos.getCenter()) <= getStatValue(stack, "teleport", "distance");
//                                        })
                                        .build())
                                .stat(StatData.builder("distance")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(10D, 20D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("cooldown")
                                        .icon(StatIcons.COOLDOWN)
                                        .initialValue(20D, 150D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.1D)
                                        .formatValue(value -> MathUtils.round(value / 20, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 11, 28).star(1, 6, 24).star(2, 2, 14).star(3, 6, 5)
                                        .star(4, 11, 2).star(5, 16, 5).star(6, 20, 14).star(7, 16, 24)
                                        .star(8, 11, 14).star(9, 7, 20).star(10, 15, 20)
                                        .link(0, 1).link(1, 2).link(2, 3).link(3, 4).link(4, 5).link(5, 6).link(6, 7).link(7, 0)
                                        .link(0, 8).link(9, 8).link(10, 8)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        Level level = player.level();

        if (ability.equals("teleport") && !level.isClientSide) {
            BlockPos blockPos = getHitResult(player, stack);
            RandomSource random = player.getRandom();

            ((ServerLevel) level).sendParticles(
                    ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)),
                            0.7F, 40, 0.9F),
                    player.getX(), player.getY() + 1, player.getZ(),
                    30,
                    0,
                    0, 0, 0.1);
            BlockPos pos = getHitResult(player, stack);

            int distance = (int) player.position().distanceTo(pos.getCenter());
            int roundedDistance = ((distance + 9) / 10) * 10;

            for (int i = 1; i <= roundedDistance; i++) {
                if (i % 10 == 0) {
                    spreadRelicExperience(player, stack, 1);
                }
            }

            player.teleportTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            player.fallDistance = 0;
            player.level().playSound(null, player, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS,
                    1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);

            setAbilityCooldown(stack, "teleport", (int) this.getStatValue(stack, "teleport", "cooldown"));
        }
    }

    public BlockPos getHitResult(Player player, ItemStack stack) {
        Level world = player.getCommandSenderWorld();
        Vec3 view = player.getViewVector(0);
        Vec3 eyeVec = player.getEyePosition(0);

        double distance = getStatValue(stack, "teleport", "distance");

        BlockHitResult ray = world.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                view.z * distance), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        BlockPos pos = ray.getBlockPos();

        if (!world.getBlockState(pos).blocksMotion())
            return null;

        pos = pos.above();

        for (int i = 0; i < 10; i++) {
            if (world.getBlockState(pos).blocksMotion() || world.getBlockState(pos.above()).blocksMotion()) {
                pos = pos.above();

                continue;
            }

            return pos;
        }

        return null;
    }

}
