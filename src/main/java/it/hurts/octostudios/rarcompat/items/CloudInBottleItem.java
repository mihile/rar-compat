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
            double upwardsMotion = 0.5;

            if (player.hasEffect(MobEffects.JUMP)) {
                upwardsMotion += 0.1 * (double) (player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
            }

            if (player.isSprinting()) {
                upwardsMotion *= 2;
            }

            Vec3 motion = player.getDeltaMovement();
            double motionMultiplier = 0.0;

            if (player.isSprinting()) {
                motionMultiplier = 2;
            }

            float direction = (float) ((double) player.getYRot() * Math.PI / 180.0);
            player.setDeltaMovement(player.getDeltaMovement().add((double) (-Mth.sin(direction)) * motionMultiplier, upwardsMotion - motion.y, (double) Mth.cos(direction) * motionMultiplier));
            player.hasImpulse = true;
            player.awardStat(Stats.JUMP);

            if (player.isSprinting()) {
                player.causeFoodExhaustion(0.2F);
            } else {
                player.causeFoodExhaustion(0.05F);
            }

            if (!player.level().isClientSide()) {
                double chance = player.getAttributeValue(ModAttributes.FLATULENCE);
                if ((double) player.getRandom().nextFloat() < chance) {
                    player.level().playSound((Player) null, player, (SoundEvent) ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
                } else {
                    player.level().playSound((Player) null, player, SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
                }
            }
        }
    }


//
//    @EventBusSubscriber
//    public static class Event {
//
//        @SubscribeEvent
//        public static void onJumpPlayer(LivingEvent.LivingJumpEvent event) {
//            if (!(event.getEntity() instanceof Player player)) return;
//
//            player.fallDistance = 0;
//            System.out.println("hahq");
//            double upwardsMotion = 0.5;
//            if (player.hasEffect(MobEffects.JUMP)) {
//                upwardsMotion += 0.1 * (player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
//            }
//            if (player.isSprinting()) {
//                upwardsMotion *= 2;
//            }
//
//            Vec3 motion = player.getDeltaMovement();
//            double motionMultiplier = 0;
//            if (player.isSprinting()) {
//                motionMultiplier = 2;
//            }
//            float direction = (float) (player.getYRot() * Math.PI / 180);
//            player.setDeltaMovement(player.getDeltaMovement().add(
//                    -Mth.sin(direction) * motionMultiplier,
//                    upwardsMotion - motion.y,
//                    Mth.cos(direction) * motionMultiplier)
//            );
//
//            player.hasImpulse = true;
//
//            player.awardStat(Stats.JUMP);
//            if (player.isSprinting()) {
//                player.causeFoodExhaustion(0.2F);
//            } else {
//                player.causeFoodExhaustion(0.05F);
//            }
//
//            if (!player.level().isClientSide()) {
//                double chance = player.getAttributeValue(ModAttributes.FLATULENCE);
//                if (player.getRandom().nextFloat() < chance) {
//                    player.level().playSound(null, player, ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
//                } else {
//                    player.level().playSound(null, player, SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
//                }
//            }
//
//        }
//    }
}
