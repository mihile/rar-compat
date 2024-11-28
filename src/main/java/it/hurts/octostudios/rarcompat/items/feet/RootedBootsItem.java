package it.hurts.octostudios.rarcompat.items.feet;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

public class RootedBootsItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("devouring")
                                .active(CastData.builder().type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("frequency")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(120D, 140D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.071)
                                        .formatValue(value -> MathUtils.round(value / 20, 1))
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff533021)
                                .borderBottom(0xff8ac100)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % Math.round(this.getStatValue(stack, "devouring", "frequency")) != 0
                || player.level().isClientSide || !isAbilityTicking(stack, "devouring"))
            return;

        Level level = player.level();
        BlockPos blockPos = player.blockPosition().below();
        BlockState blockState = player.level().getBlockState(blockPos);
        RandomSource random = player.getRandom();

        if (blockState.is(Blocks.GRASS_BLOCK)) {
            ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                            new Color(random.nextInt(50), 100 + random.nextInt(155), random.nextInt(50)),
                            0.3F, 40, 0.9F),
                    player.getX(), player.getY() + 0.2, player.getZ(),
                    20,
                    0.2,
                    0, 0.2, 0.0);
            spreadRelicExperience(player, stack, 1);

            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 2);

            level.setBlock(blockPos, Blocks.DIRT.defaultBlockState(), 3);
            level.playSound(null, player, SoundEvents.GRASS_BREAK, SoundSource.PLAYERS,
                    1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
        }
    }
}
