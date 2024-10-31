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
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.*;

public class WarpDriveItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("teleport")
                                .active(CastData.builder().type(CastType.INSTANTANEOUS)
                                        .predicate("teleport", PredicateType.CAST, (player, stack) -> getHitResult(player, stack).getType() != HitResult.Type.MISS)
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
            HitResult result = getHitResult(player, stack);

            // TODO: Implement positive vertical safe-place searching
            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) result;
                BlockPos blockPos = blockHitResult.getBlockPos();
                RandomSource random = player.getRandom();

                ((ServerLevel) level).sendParticles(
                        ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)),
                                0.7F, 40, 0.9F),
                        player.getX(), player.getY() + 1, player.getZ(),
                        30,
                        0,
                        0, 0, 0.1);

                player.teleportTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);

                player.level().playSound(null, player, SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS,
                        1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);

                setAbilityCooldown(stack, "teleport", (int) this.getStatValue(stack, "teleport", "cooldown"));
            }
        }
    }

    public HitResult getHitResult(Player player, ItemStack stack) {
        return player.pick(this.getStatValue(stack, "teleport", "distance"), 1.0F, false);
    }

}
