package it.hurts.octostudios.rarcompat.items.belt;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Objects;

public class CloudInBottleItem extends WearableRelicItem {
    private boolean hasJumped = false;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("jump")
                                .stat(StatData.builder("count")
                                        .initialValue(2, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (player.onGround()) {
            stack.set(DataComponentRegistry.COUNT, 0);
            hasJumped = false;
        }

        if (localPlayer != null && !player.onGround()) {
            if (localPlayer.input.jumping && !hasJumped && stack.getOrDefault(DataComponentRegistry.COUNT, 0) <= getStatValue(stack, "jump", "count")) {
                hasJumped = true;
                stack.set(DataComponentRegistry.COUNT, stack.getOrDefault(DataComponentRegistry.COUNT, 0) + 1);

                if (stack.getOrDefault(DataComponentRegistry.COUNT, 0) > 1) {
                    player.fallDistance = 0.0F;
                    double upwardsMotion = 0.7;

                    if (player.hasEffect(MobEffects.JUMP)) {
                        upwardsMotion += 0.1 * (double) (Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).getAmplifier() + 1);
                    }

                    float direction = (float) ((double) player.getYRot() * Math.PI / 180.0);
                    double horizontalFactor = 3.5;

                    player.setDeltaMovement(player.getDeltaMovement().add(
                            -Mth.sin(direction) / horizontalFactor,
                            upwardsMotion - player.getDeltaMovement().y,
                            Mth.cos(direction) / horizontalFactor
                    ));

                    player.hasImpulse = true;
                    player.awardStat(Stats.JUMP);
                }
            } else if (!localPlayer.input.jumping) {
                hasJumped = false;
            }
        }
    }
}
