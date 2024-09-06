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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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
                                        .initialValue(0.07D, 0.08D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.03D)
                                        .formatValue(value -> {
                                            if (value >= 0.1)
                                                return MathUtils.round(value, 1);
                                            return MathUtils.round(value, 3);
                                        })
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
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (stack.getOrDefault(DataComponentRegistry.TOGGLED, true)) {
            double thresholdValue = getStatValue(stack, "invisible", "threshold");

            if (Math.abs(player.getKnownMovement().y) > thresholdValue) return;

            if (player.getSpeed() <= thresholdValue)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
            else if (player.isShiftKeyDown() && thresholdValue < 0.9F)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
            else if (Math.abs(player.getKnownMovement().x) <= 0.01D && Math.abs(player.getKnownMovement().z) <= 0.01D && player.getSpeed() == 0.1F)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
        } else {
            if (stack.get(DataComponentRegistry.TARGET) != null)
                updateInvisibilityZone(player.level(), player, getStatValue(stack, "invisible", "radius"), stack);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        stack.set(DataComponentRegistry.TOGGLED, true);
        stack.set(DataComponentRegistry.TARGET, null);
    }

    public static void updateInvisibilityZone(Level level, Player player, double radius, ItemStack itemStack) {
        if (player == null)
            return;

        checkDistance(player, radius);
        RandomSource random = player.getRandom();

        ParticleUtils.createCyl(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.5F, 1, 1),
                getBlockPos(itemStack), level, radius, 0.1F);
    }

    private static void checkDistance(Player playerOwner, double radius) {
        ItemStack stack = EntityUtils.findEquippedCurio(playerOwner, ModItems.SCARF_OF_INVISIBILITY.value());

        if (getBlockPos(stack).distanceTo(playerOwner.position()) > radius) {
            stack.set(DataComponentRegistry.TOGGLED, true);
            stack.set(DataComponentRegistry.TARGET, null);
        } else {
            stack.set(DataComponentRegistry.TOGGLED, false);
        }
    }

    private static Vec3 getBlockPos(ItemStack stack) {
        if (stack.get(DataComponentRegistry.TARGET) == null) {
            stack.set(DataComponentRegistry.TOGGLED, true);
            return Vec3.ZERO;
        }

        String[] parts = Objects.requireNonNull(stack.get(DataComponentRegistry.TARGET)).split("\\s+");

        return new Vec3(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Player playerClient = Minecraft.getInstance().player;
            ItemStack stack = EntityUtils.findEquippedCurio(playerClient, ModItems.SCARF_OF_INVISIBILITY.value());

            if (stack.getItem() instanceof ScarfOfInvisibilityItem
                    && playerClient != null
                    && !playerClient.hasContainerOpen()
                    && playerClient.hasEffect(EffectRegistry.VANISHING)
                    && Minecraft.getInstance().screen == null)
                NetworkHandler.sendToServer(new PacketCreateZone(Minecraft.getInstance().player.getUUID().toString()));
        }

    }
}