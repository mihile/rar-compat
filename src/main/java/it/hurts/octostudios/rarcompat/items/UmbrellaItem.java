package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Setter;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class UmbrellaItem extends WearableRelicItem {
    public static double fallDistanceTick = 0;
    public static double shiftHoldTime = 0;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glider")
                                .stat(StatData.builder("speed")
                                        .icon(StatIcons.JUMP_HEIGHT)
                                        .initialValue(20D, 40D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.0625D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("shield")
                                .stat(StatData.builder("knockback")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(0.25D, 1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.4D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        if (player.onGround() || !isHoldingUmbrellaUpright(player))
            fallDistanceTick = 0;

        if (!player.isShiftKeyDown())
            shiftHoldTime = 0.0;

        if (!player.onGround() && !player.isInWater()
                && player.getDeltaMovement().y < 0
                && !player.hasEffect(MobEffects.SLOW_FALLING)
                && isHoldingUmbrellaUpright(player)) {
            fallDistanceTick++;
            Vec3 motion = player.getDeltaMovement();

            double modifyVal = ((UmbrellaItem) stack.getItem()).getStatValue(stack, "glider", "speed") / 100.0;
            double fallDistanceRatio = fallDistanceTick / 130.0;
            double logFactor = Math.min(Math.log1p(modifyVal * fallDistanceRatio), 0.33);

            double newFallSpeed = motion.y * (1.0 - logFactor);

            if (player.isShiftKeyDown()) {
                shiftHoldTime += 0.002;
                newFallSpeed -= Math.max(shiftHoldTime * 0.02, 0.1);
            }

            player.setDeltaMovement(motion.x, newFallSpeed, motion.z);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 center = player.position().add(player.getLookAngle().scale(1));

        level.getEntitiesOfClass(LivingEntity.class, new AABB(center.x - 0.5, center.y - 0.5, center.z - 0.5, center.x + 0.5, center.y + 0.5, center.z + 0.5),
                entity -> entity != null && entity != player).forEach(entity -> pushAwayEntity(player, entity));

        player.startUsingItem(hand);

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity entity, int p_41415_) {
        if (entity instanceof Player player)
            player.getCooldowns().addCooldown(this, 60);

        super.releaseUsing(p_41412_, p_41413_, entity, p_41415_);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }

    public boolean pushAwayEntity(Player player, LivingEntity attacker) {
        if (player.level().isClientSide) return false;

        Vec3 toTarget = new Vec3(attacker.getX() - player.getX(), 0, attacker.getZ() - player.getZ()).normalize();

        if (player.getLookAngle().normalize().dot(toTarget) > 0) {
            attacker.setDeltaMovement(attacker.getDeltaMovement().add(toTarget.scale(getStatValue(player.getUseItem(), "shield", "knockback"))));

            return true;
        }

        return false;
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity, InteractionHand hand) {
        return entity.getItemInHand(hand).getItem() instanceof UmbrellaItem && (!entity.isUsingItem() || entity.getUsedItemHand() != hand);
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity) {
        return isHoldingUmbrellaUpright(entity, InteractionHand.MAIN_HAND) || isHoldingUmbrellaUpright(entity, InteractionHand.OFF_HAND);
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onLivingRender(RenderLivingEvent.Pre<?, ?> event) {
            if (!(event.getRenderer().getModel() instanceof HumanoidModel<?> humanoidModel) || !(event.getEntity() instanceof Player player)
                    || !isHoldingUmbrellaUpright(player))
                return;

            boolean isHoldingOffHand = isHoldingUmbrellaUpright(player, InteractionHand.OFF_HAND);
            boolean isHoldingMainHand = isHoldingUmbrellaUpright(player, InteractionHand.MAIN_HAND);
            boolean isRightHanded = player.getMainArm() == HumanoidArm.RIGHT;

            if ((isHoldingMainHand && isRightHanded) || (isHoldingOffHand && !isRightHanded))
                humanoidModel.rightArmPose = HumanoidModel.ArmPose.THROW_SPEAR;

            if ((isHoldingMainHand && !isRightHanded) || (isHoldingOffHand && isRightHanded))
                humanoidModel.leftArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
        }

        @SubscribeEvent
        public static void onEntityHurt(LivingIncomingDamageEvent event) {
            if (event.getEntity() instanceof Player player
                    && player.getUseItem().getItem() instanceof UmbrellaItem relic
                    && player.isUsingItem()
                    && event.getSource().getEntity() instanceof LivingEntity attacker) {

                if (relic.pushAwayEntity(player, attacker))
                    event.setCanceled(true);
            }
        }

    }
}
