package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.CreateZonePacket;
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
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
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
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
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
                                .research(ResearchData.builder()
                                        .star(0, 11, 27).star(1, 11, 12).star(2, 11, 8)
                                        .star(3, 17, 12).star(4, 6, 12).star(5, 6, 17)
                                        .link(0, 1).link(1, 2).link(1, 3).link(1, 4).link(4, 5)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff51a4df)
                                .borderBottom(0xff2c3b70)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (stack.get(DataComponentRegistry.WORLD_POSITION) == null) {
            double thresholdValue = getStatValue(stack, "invisible", "threshold");

            if (Math.abs(player.getKnownMovement().y) > thresholdValue)
                return;

            if (player.getSpeed() <= thresholdValue) {
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
                if (player.tickCount % 20 == 0)
                    spreadRelicExperience(player, stack, +1);
            } else if (player.isShiftKeyDown() && thresholdValue < 0.9F) {
                player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
                if (player.tickCount % 20 == 0)
                    spreadRelicExperience(player, stack, +1);
            } else if (Math.abs(player.getKnownMovement().x) <= 0.01D && Math.abs(player.getKnownMovement().z) <= 0.01D && player.getSpeed() == 0.1F)
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

        RandomSource random = player.getRandom();

        if (level.isClientSide) return;
        checkDistance(player, radius);
        createCyl(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.5F, 1, 1),
                getBlockPos(itemStack), level, radius, 0.1F);
    }

    public static void createCyl(ParticleOptions particle, Vec3 center, Level level, double radius, float step) {
        int offset = 16;
        double len = 6.283185307179586 * radius;
        int num = (int) (len / (double) step);

        for (int i = 0; i < num; ++i) {
            double angle = Math.toRadians((double) (360.0F / (float) num * (float) i) + 360.0 * ((len / (double) step - (double) num) / (double) num / len));
            double extraX = radius * Math.sin(angle) + center.x();
            double extraZ = radius * Math.cos(angle) + center.z();
            double extraY = center.y();
            boolean foundPos = false;

            int tries;
            for (tries = 0; tries < offset * 2; ++tries) {
                Vec3 vec = new Vec3(extraX, extraY, extraZ);
                BlockPos pos = new BlockPos(Mth.floor(extraX), Mth.floor(extraY), Mth.floor(extraZ));
                BlockState state = level.getBlockState(pos);
                VoxelShape shape = state.getCollisionShape(level, pos);
                if (state.getBlock() instanceof LiquidBlock) {
                    shape = Shapes.block();
                }

                if (shape.isEmpty()) {
                    if (!foundPos) {
                        --extraY;
                        continue;
                    }
                } else {
                    foundPos = true;
                }

                if (shape.isEmpty()) {
                    break;
                }

                AABB aabb = shape.bounds();
                if (!aabb.move(pos).contains(vec)) {
                    if (!(aabb.maxY >= 1.0)) {
                        break;
                    }

                    ++extraY;
                } else {
                    extraY += step;
                }
            }

            if (tries < offset * 2) {
                ((ServerLevel) level).sendParticles(particle, extraX, extraY + 0.10000000149011612, extraZ, 0, 0.0, 0.0, 0, 0);
            }
        }

    }

    private static void checkDistance(Player playerOwner, double radius) {
        Level level = playerOwner.level();

        if (level.isClientSide) return;

        ItemStack stack = EntityUtils.findEquippedCurio(playerOwner, ModItems.SCARF_OF_INVISIBILITY.value());
        int offset = 16;

        if (getBlockPos(stack).distanceTo(playerOwner.position()) <= radius) return;

        RandomSource random = playerOwner.getRandom();

        int particleCount = (int) (radius * 75);

        for (int i = 0; i < particleCount; i++) {
            double angle = i * (2 * Math.PI / particleCount);
            double x = (radius * Math.cos(angle)) + getBlockPos(stack).x;
            double y = getBlockPos(stack).y;
            double z = (radius * Math.sin(angle)) + getBlockPos(stack).z;

            boolean foundPos = false;
            int tries;

            for (tries = 0; tries < offset * 2; tries++) {
                Vec3 vec = new Vec3(x, y, z);
                BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));

                BlockState state = level.getBlockState(pos);
                VoxelShape shape = state.getCollisionShape(level, pos);

                if (state.getBlock() instanceof LiquidBlock)
                    shape = Shapes.block();

                if (shape.isEmpty()) {
                    if (!foundPos) {
                        y -= 1;

                        continue;
                    }
                } else
                    foundPos = true;

                if (shape.isEmpty())
                    break;

                AABB aabb = shape.bounds();

                if (!aabb.move(pos).contains(vec)) {
                    if (aabb.maxY >= 1F) {
                        y += 1;

                        continue;
                    }

                    break;
                }

                y += 0.1F;
            }

            if (tries < offset * 2)
                ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                                new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)),
                                0.5F, (int) (radius * (40 / radius)), 0.9F),
                        x, y, z,
                        2,
                        0,
                        0.02 + (random.nextDouble() * 0.02), 0, 0.1
                );
        }

        stack.set(DataComponentRegistry.WORLD_POSITION, null);
    }

    private static Vec3 getBlockPos(ItemStack stack) {
        if (stack.get(DataComponentRegistry.WORLD_POSITION) == null)
            return new Vec3(0, 0, 0);

        return stack.get(DataComponentRegistry.WORLD_POSITION).getPos();
    }

    @EventBusSubscriber(value = Dist.CLIENT)
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

                NetworkHandler.sendToServer(new CreateZonePacket());
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
                                0.5F, (int) (radius * (17 / radius)), 0.9F),
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