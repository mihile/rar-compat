package it.hurts.octostudios.rarcompat.mixin;

import artifacts.Artifacts;
import artifacts.item.ArtifactItem;
import artifacts.item.UmbrellaItem;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UmbrellaItem.class)
public class UmbrellaItemMixin extends ArtifactItem implements IRelicItem {
    public UmbrellaItemMixin() {
        super(new Properties());
    }

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("glider")
                                .stat(StatData.builder("speed")
                                        .initialValue(0.4D, 0.6D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("shield")
                                .stat(StatData.builder("knockback")
                                        .initialValue(2.0D, 1.5D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUse(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (hand == InteractionHand.MAIN_HAND) {
            player.startUsingItem(hand);
            cir.setReturnValue(InteractionResultHolder.consume(player.getItemInHand(hand)));
        }
    }

    @Override
    public boolean isCosmetic() {
        return !Artifacts.CONFIG.items.umbrellaIsGlider.get() && !Artifacts.CONFIG.items.umbrellaIsShield.get();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int p_41415_) {
        if (entity instanceof Player player) {
            Vec3 lookVec = player.getLookAngle();
            AABB area = player.getBoundingBox().inflate(1, 0.5, 1);
            level.getEntities(player, area, bob -> bob instanceof LivingEntity && bob != player).forEach(e -> {
                Vec3 kb = new Vec3(lookVec.x, 0, lookVec.z).normalize()
                        .scale(getStatValue(stack, "shield", "knockback"));
                e.setDeltaMovement(e.getDeltaMovement().add(kb));
            });
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private static void onLivingUpdate(LivingEntity entity, CallbackInfo ci) {
        if (UmbrellaItem.shouldGlide(entity)) {
            ItemStack stack = entity.getMainHandItem();
            if (stack.getItem() instanceof IRelicItem && stack.getItem() instanceof UmbrellaItem) {
                double fallSpeed = ((IRelicItem) stack.getItem()).getStatValue(stack, "glider", "speed");
                Vec3 motion = entity.getDeltaMovement();
                if (motion.y < 0) {
                    entity.setDeltaMovement(motion.x, motion.y * fallSpeed, motion.z);
                }
            }
        }
    }
}