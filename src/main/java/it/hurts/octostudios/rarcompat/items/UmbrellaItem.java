package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.necklace.ScarfOfInvisibilityItem;
import it.hurts.octostudios.rarcompat.network.packets.CreateZonePacket;
import it.hurts.octostudios.rarcompat.network.packets.PowerJumpPacket;
import it.hurts.octostudios.rarcompat.network.packets.RepulsionUmbrellaPacket;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class UmbrellaItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glider")
                                .stat(StatData.builder("count")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> (int) MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 3, 10).star(1, 11, 5).star(2, 19, 10)
                                        .star(3, 11, 22).star(4, 15, 22).star(5, 13, 25)
                                        .link(0, 1).link(1, 2).link(1, 3).link(3, 5).link(5, 4)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("shield")
                                .requiredLevel(5)
                                .stat(StatData.builder("knockback")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.07D)
                                        .formatValue(value -> MathUtils.round(value, 0))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 2, 5).star(1, 11, 3).star(2, 20, 6)
                                        .star(3, 11, 13).star(4, 3, 19).star(5, 19, 19).star(6, 11, 25)
                                        .link(0, 3).link(1, 3).link(2, 3).link(3, 4).link(4, 6).link(6, 5).link(3, 5)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffb63a2b)
                                .borderBottom(0xff600f15)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(15)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("shield")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("glider_1", "glider")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("glider_2", "glider")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (!(entity instanceof Player player))
            return;

        int charges = stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
        int statCount = (int) getStatValue(stack, "glider", "count");

        if (charges >= statCount)
            player.getCooldowns().addCooldown(this, 120);

        if (player.onGround() && charges != 0)
            stack.set(DataComponentRegistry.CHARGE, 0);

        if (player.isInWater() || !isHoldingUmbrellaUpright(player) || player.hasEffect(MobEffects.SLOW_FALLING) || player.getDeltaMovement().y > 0)
            return;

        Vec3 motion = player.getDeltaMovement();

        player.setDeltaMovement(motion.x, -0.15, motion.z);

        if (player.tickCount % 20 == 0 && !player.onGround())
            spreadRelicExperience(player, stack, 1);

        createParticle(level, player);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity, InteractionHand hand) {
        return entity.getItemInHand(hand).getItem() instanceof UmbrellaItem && (!entity.isUsingItem() || entity.getUsedItemHand() != hand);
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity) {
        return isHoldingUmbrellaUpright(entity, InteractionHand.MAIN_HAND) || isHoldingUmbrellaUpright(entity, InteractionHand.OFF_HAND);
    }

    public void createParticle(Level level, Player player) {
        if (level.isClientSide || player.fallDistance < 2)
            return;

        Vec3 basePosition = player.getEyePosition(1.0F)
                .add(player.getLookAngle().scale(0.5))
                .add(player.getUpVector(1.0F).scale(-0.25))
                .add(player.getLookAngle().cross(new Vec3(0, 1, 0))
                        .scale(player.getMainHandItem().getItem() instanceof UmbrellaItem ? 0.3 : -0.3));

        Vec3[] offsets = new Vec3[]{
                new Vec3(0.5, 0, 0.5),
                new Vec3(0.5, 0, -0.5),
                new Vec3(-0.5, 0, 0.5),
                new Vec3(-0.5, 0, -0.5)
        };

        for (Vec3 offset : offsets) {
            Vec3 particlePosition = basePosition.add(offset);

            ((ServerLevel) level).sendParticles(
                    ParticleTypes.CLOUD,
                    particlePosition.x,
                    player.getY() + 3,
                    particlePosition.z,
                    1,
                    0, 0, 0, 0
            );
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class UmbrellaEventEvent {
        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Player playerClient = Minecraft.getInstance().player;

            if (playerClient == null || event.getAction() != 1)
                return;

            ItemStack stack = playerClient.getMainHandItem();

            if (stack.getItem() instanceof UmbrellaItem relic
                    && event.getButton() == 0
                    && !playerClient.getCooldowns().isOnCooldown(stack.getItem())
                    && !HotkeyRegistry.ABILITY_LIST.isDown()
                    && event.getButton() != HotkeyRegistry.ABILITY_LIST.getKey().getValue()
                    && !playerClient.hasContainerOpen()
                    && Minecraft.getInstance().screen == null
                    && !playerClient.onGround()) {
                Vec3 lookDirection = playerClient.getLookAngle().scale(-1);
                double modifierVal = 1.2;

                playerClient.setDeltaMovement(new Vec3((lookDirection.x * modifierVal), (lookDirection.y * modifierVal), (lookDirection.z * modifierVal)));

                NetworkHandler.sendToServer(new RepulsionUmbrellaPacket());
            }
        }
    }

    @EventBusSubscriber
    public static class UmbrellaEvent {
        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            if (!isHoldingUmbrellaUpright(event.getEntity()))
                return;

            event.setDamageMultiplier(0);
        }

        @SubscribeEvent
        public static void onPlayerHurt(LivingIncomingDamageEvent event) {
            if (event.getEntity() instanceof Player player
                    && player.getUseItem().getItem() instanceof UmbrellaItem relic
                    && player.isUsingItem()
                    && event.getSource().getEntity() instanceof LivingEntity attacker
                    && !player.getCommandSenderWorld().isClientSide()
                    && attacker != player) {

                Vec3 playerToAttacker = attacker.position().subtract(player.position()).normalize();
                Vec3 playerLookDirection = player.getLookAngle().normalize();

                double dotProduct = playerToAttacker.dot(playerLookDirection);

                if (dotProduct < 0.8)
                    return;

                event.setCanceled(true);

                double radius = 2.0;
                double height = 2.0;

                Vec3 zoneCenter = player.position().add(player.getLookAngle().normalize().scale(1.0));

                AABB boundingBox = new AABB
                        (zoneCenter.x - radius, zoneCenter.y - height / 2.0, zoneCenter.z - radius,
                                zoneCenter.x + radius, zoneCenter.y + height / 2.0, zoneCenter.z + radius);

                List<LivingEntity> entitiesInRange = player.level().getEntitiesOfClass(
                        LivingEntity.class, boundingBox,
                        entity -> entity != player && entity.isAlive()
                );

                relic.spreadRelicExperience(player, player.getUseItem(), 1);

                for (LivingEntity entity : entitiesInRange) {
                    double stat = relic.getStatValue(player.getUseItem(), "shield", "knockback");
                    Vec3 toEntity = entity.position().subtract(player.position()).normalize().scale(stat * 0.5);

                    entity.setDeltaMovement(toEntity.x, toEntity.y / 2, toEntity.z);

                    Vec3 startPosition = attacker.position().add(new Vec3(0, attacker.getBbHeight() / 2.0, 0));
                    Vec3 particleVelocity = toEntity.normalize().scale(0.5);

                    ((ServerLevel) player.level()).sendParticles
                            (ParticleTypes.CLOUD,
                                    startPosition.x,
                                    startPosition.y,
                                    startPosition.z,
                                    10,
                                    particleVelocity.x,
                                    particleVelocity.y,
                                    particleVelocity.z,
                                    0.1);
                }

                player.level().playSound(null, player.blockPosition(), SoundEvents.ALLAY_HURT, SoundSource.MASTER, 0.3f, 1 + (player.getRandom().nextFloat() * 0.25F));
            }
        }


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
    }
}
