package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.PacketCreateZone;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class ScarfOfInvisibilityItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("invisible")
                                .stat(StatData.builder("threshold")
                                        .icon(StatIcons.SPEED)
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.035D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(8D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.05D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.level().isClientSide)
            return;

        if (stack.getOrDefault(DataComponentRegistry.TOGGLED, true) && player.getSpeed() < getStatValue(stack, "invisible", "threshold"))
            player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
        else {
            if (stack.get(DataComponentRegistry.TARGET) != null)
                updateInvisibilityZone((ServerLevel) player.level(), player.getUUID(), getStatValue(stack, "invisible", "radius"), stack);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        stack.set(DataComponentRegistry.TOGGLED, true);
        stack.set(DataComponentRegistry.TARGET, null);
    }

    public static void updateInvisibilityZone(ServerLevel level, UUID playerOwnerUUID, double radius, ItemStack itemStack) {
        Player playerOwner = (Player) level.getEntity(playerOwnerUUID);

        if (playerOwner == null)
            return;

        checkDistance(level, playerOwner, radius);

        double particles = radius * 75;

        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;

            double x = getBlockPos(itemStack).getX() + radius * Math.cos(angle);
            double y = getBlockPos(itemStack).getY();
            double z = getBlockPos(itemStack).getZ() + radius * Math.sin(angle);

            y = adjustYPositionToValidBlock(level, x, y, z);

            RandomSource random = playerOwner.getRandom();

            level.sendParticles(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.5F, 1, 1),
                    x, y + 0.2, z, 0, 0.0, 0, 0, 0);
        }
    }

    private static void checkDistance(ServerLevel level, Player playerOwner, double radius) {
        ItemStack stack = EntityUtils.findEquippedCurio(playerOwner, ModItems.SCARF_OF_INVISIBILITY.value());

        if (!(stack.getItem() instanceof ScarfOfInvisibilityItem))
            return;

        if (getBlockPos(stack).getCenter().distanceTo(level.getEntity(playerOwner.getUUID()).getPosition(1)) > radius) {
            stack.set(DataComponentRegistry.TOGGLED, true);
            stack.set(DataComponentRegistry.TARGET, null);
        } else {
            stack.set(DataComponentRegistry.TOGGLED, false);
        }
    }

    private static double adjustYPositionToValidBlock(ServerLevel level, double x, double y, double z) {
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);

        for (int i = 0; i <= 2; i++) {
            BlockPos upPos = pos.above(i);

            if (level.getBlockState(upPos).blocksMotion())
                return upPos.getY() + 1;
        }

        for (int i = 1; i == 1; i++) {
            BlockPos downPos = pos.below(i);

            if (level.getBlockState(downPos).blocksMotion())
                return downPos.getY() + 1;
        }

        return y;
    }

    private static BlockPos getBlockPos(ItemStack stack) {
        if (stack.get(DataComponentRegistry.TARGET) == null) {
            stack.set(DataComponentRegistry.TOGGLED, true);
            return BlockPos.ZERO;
        }

        String[] parts = Objects.requireNonNull(stack.get(DataComponentRegistry.TARGET)).split("\\s+");

        return new BlockPos((int) Double.parseDouble(parts[0]), (int) Double.parseDouble(parts[1]), (int) Double.parseDouble(parts[2]));
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Player playerClient = Minecraft.getInstance().player;
            ItemStack stack = EntityUtils.findEquippedCurio(playerClient, ModItems.SCARF_OF_INVISIBILITY.value());

            if (playerClient != null && playerClient.hasEffect(EffectRegistry.VANISHING) && stack.getItem() instanceof ScarfOfInvisibilityItem)
                NetworkHandler.sendToServer(new PacketCreateZone(Minecraft.getInstance().player.getUUID().toString()));
        }

    }
}