package it.hurts.octostudios.rarcompat.items;

import it.hurts.octostudios.rarcompat.network.packets.RepulsionUmbrellaPacket;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
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
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;

public class UmbrellaItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glider")
                                .requiredPoints(2)
                                .stat(StatData.builder("count")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.ADD, 1D)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
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
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
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
                                .source(LevelingSourceData.abilityBuilder("glider_1", "glider")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("glider_2", "glider")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("shield")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
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

        if (player.onGround() && getCharges(stack) != 0) {
            setCharges(stack, 0);
            player.getCooldowns().addCooldown(this, 0);
        }

        if (player.isInWater() || !isHoldingUmbrellaUpright(player, player.getUsedItemHand()) || player.getDeltaMovement().y > 0 || player.getAbilities().flying
                || player.isFallFlying() || player.hasEffect(MobEffects.SLOW_FALLING) || !canPlayerUseAbility(player, stack, "glider"))
            return;

        Vec3 motion = player.getDeltaMovement();

        player.setDeltaMovement(motion.x, -0.15, motion.z);
        player.fallDistance = 0;

        if (player.tickCount % 20 == 0 && !player.onGround())
            spreadRelicExperience(player, stack, 1);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (canPlayerUseAbility(player, player.getMainHandItem(), "shield")) {
            player.startUsingItem(hand);

            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        return InteractionResultHolder.fail(player.getItemInHand(hand));
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

    @Override
    public int getBarWidth(ItemStack stack) {
        int charges = stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
        int statCount = (int) getStatValue(stack, "glider", "count");

        return Math.round(13.0F * (statCount - charges) / statCount);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return this.getCharges(stack) != 0;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        int charges = getCharges(stack);
        int statCount = (int) getStatValue(stack, "glider", "count");

        float normalizedValue = Math.max(0.0F, 1.0F - (charges / (float) statCount));
        return Mth.hsvToRgb(normalizedValue / 3.0F, 1.0F, 1.0F);
    }

    public void addCharges(ItemStack stack, int val) {
        setCharges(stack, getCharges(stack) + val);
    }

    public int getCharges(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
    }

    public void setCharges(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.CHARGE, Math.max(val, 0));
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class UmbrellaClientEvent {
        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Player playerClient = Minecraft.getInstance().player;

            if (playerClient == null || event.getAction() != 1)
                return;

            ItemStack stack = playerClient.getMainHandItem();

            if (event.getButton() == 0
                    && !HotkeyRegistry.ABILITY_LIST.isDown()
                    && event.getButton() != HotkeyRegistry.ABILITY_LIST.getKey().getValue()
                    && !playerClient.hasContainerOpen()
                    && !playerClient.getCooldowns().isOnCooldown(stack.getItem())
                    && Minecraft.getInstance().screen == null
                    && stack.getItem() instanceof UmbrellaItem relic
                    && relic.canPlayerUseAbility(playerClient, stack, "glider")
                    && relic.getCharges(stack) < relic.getStatValue(stack, "glider", "count")
                    && !playerClient.onGround()
                    && !playerClient.isFallFlying()) {

                playerClient.setDeltaMovement(playerClient.getLookAngle().scale(-1).scale(1.1));

                NetworkHandler.sendToServer(new RepulsionUmbrellaPacket());
            }
        }
    }

    @EventBusSubscriber
    public static class UmbrellaEvent {
        @SubscribeEvent
        public static void onPlayerHurt(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof UmbrellaItem relic
                    && player.isUsingItem()
                    && event.getSource().getEntity() instanceof LivingEntity attacker
                    && attacker != player) {

                if (attacker.position().subtract(player.position()).normalize().dot(player.getLookAngle().normalize()) < 0.8)
                    return;

                event.setCanceled(true);

                AABB boundingBox = new AABB(player.blockPosition()).inflate(2, 1, 2);

                relic.spreadRelicExperience(player, stack, 1);

                for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity != player && entity.isAlive())) {
                    Vec3 toEntity = entity.position().subtract(player.position()).normalize().scale(0.4 + (relic.getStatValue(stack, "glider", "count") * 0.2));

                    entity.setDeltaMovement(toEntity);

                    Vec3 startPosition = attacker.position().add(new Vec3(0, attacker.getBbHeight() / 2.0, 0));
                    Vec3 particleVelocity = toEntity.normalize().scale(0.5);

                    ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD, startPosition.x, startPosition.y, startPosition.z,
                            10, particleVelocity.x, particleVelocity.y, particleVelocity.z, 0.1);
                }

                player.level().playSound(null, player.blockPosition(), SoundEvents.ALLAY_HURT, SoundSource.MASTER, 0.3f,
                        1 + (player.getRandom().nextFloat() * 0.25F));
            }
        }

        @SubscribeEvent
        public static void onLivingRender(RenderLivingEvent.Pre<?, ?> event) {
            if (!(event.getRenderer().getModel() instanceof HumanoidModel<?> humanoidModel) || !(event.getEntity() instanceof Player player)
                    || !isHoldingUmbrellaUpright(player, player.getUsedItemHand()))
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
