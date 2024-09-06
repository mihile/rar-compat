package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.PacketCreateZone;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.HotkeyRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

public class ScarfOfInvisibilityItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("invisible")
                                .active(CastData.builder().type(CastType.TOGGLEABLE).build())
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
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (stack.get(DataComponentRegistry.WORLD_POSITION) == null) {
            double thresholdValue = getStatValue(stack, "invisible", "threshold");

            if (Math.abs(player.getKnownMovement().y) > thresholdValue) return;

            if (player.getSpeed() <= thresholdValue)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
            else if (player.isShiftKeyDown() && thresholdValue < 0.9F)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
            else if (Math.abs(player.getKnownMovement().x) <= 0.01D && Math.abs(player.getKnownMovement().z) <= 0.01D && player.getSpeed() == 0.1F)
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
        } else
            updateInvisibilityZone(player.level(), player, getStatValue(stack, "invisible", "radius"), stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack) return;

        stack.set(DataComponentRegistry.WORLD_POSITION, null);
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
            Level level = playerOwner.level();
            RandomSource random = playerOwner.getRandom();

            int particleCount = (int) (radius * 75);
            double angleStep = 2 * Math.PI / particleCount;

            for (int i = 0; i < particleCount; i++) {
                double angle = i * angleStep;
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);

                Vec3 particlePosition = getBlockPos(stack).add(x, 0, z);

                level.addParticle(ParticleUtils.constructSimpleSpark(
                                new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)),
                                0.5F, (int) (radius * (40 / radius)), 1),
                        particlePosition.x,
                        particlePosition.y,
                        particlePosition.z,
                        0.0,
                        0.02 + (random.nextDouble() * 0.02),
                        0.0
                );
            }
            stack.set(DataComponentRegistry.WORLD_POSITION, null);
        }
    }

    private static Vec3 getBlockPos(ItemStack stack) {
        if (stack.get(DataComponentRegistry.WORLD_POSITION) == null)
            return new Vec3(0, 0, 0);

        return stack.get(DataComponentRegistry.WORLD_POSITION).getPos();
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onMouseInput(InputEvent.MouseButton.Pre event) {
            Player playerClient = Minecraft.getInstance().player;
            ItemStack stack = EntityUtils.findEquippedCurio(playerClient, ModItems.SCARF_OF_INVISIBILITY.value());

            if (stack.getItem() instanceof ScarfOfInvisibilityItem relic
                    && playerClient != null
                    && !HotkeyRegistry.ABILITY_LIST.isDown()
                    && event.getButton() != HotkeyRegistry.ABILITY_LIST.getKey().getValue()
                    && playerClient.hasEffect(EffectRegistry.VANISHING)
                    && !playerClient.hasContainerOpen()
                    && Minecraft.getInstance().screen == null) {

                NetworkHandler.sendToServer(new PacketCreateZone(Minecraft.getInstance().player.getUUID().toString()));
                createBallParticles(playerClient, stack, relic.getStatValue(stack, "invisible", "radius"));
            }

        }

        public static void createBallParticles(Player player, ItemStack stack, double radius) {
            Level level = player.level();
            RandomSource random = player.getRandom();

            for (int i = 0; i < radius * 50; i++) {
                double theta = 2 * Math.PI * random.nextDouble();
                double phi = Math.acos(2 * random.nextDouble() - 1);

                double velocityX = radius * Math.sin(phi) * Math.cos(theta);
                double velocityY = radius * Math.sin(phi) * Math.sin(theta);
                double velocityZ = radius * Math.cos(phi);

                level.addParticle(ParticleUtils.constructSimpleSpark(
                                new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)),
                                0.5F, (int) (radius * (17 / radius)), 1
                        ),
                        getBlockPos(stack).x,
                        getBlockPos(stack).y + 1,
                        getBlockPos(stack).z,
                        velocityX * 0.055,
                        velocityY * 0.055,
                        velocityZ * 0.055
                );
            }
        }
    }
}