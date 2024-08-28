package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModAttributes;
import artifacts.registry.ModSoundEvents;
import it.hurts.octostudios.rarcompat.items.base.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Objects;

public class CloudInBottleItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("fly")
                                .stat(StatData.builder("time")
                                        .initialValue(5D, 10D)
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

        if (localPlayer != null && !player.onGround() && localPlayer.input.jumping) {
            player.fallDistance = 0.0F;
            double upwardsMotion = 0.7;

            if (player.hasEffect(MobEffects.JUMP))
                upwardsMotion += 0.1 * (double) (Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).getAmplifier() + 1);

            float direction = (float) ((double) player.getYRot() * Math.PI / 180.0);
            double horizontalFactor = 3.5;

            player.setDeltaMovement(player.getDeltaMovement().add(
                    -Mth.sin(direction) / horizontalFactor,
                    upwardsMotion - player.getDeltaMovement().y,
                    Mth.cos(direction) / horizontalFactor
            ));

            player.hasImpulse = true;
            player.awardStat(Stats.JUMP);

            player.level().playSound(null, player, SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
        }

    }
}
