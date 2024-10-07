package it.hurts.octostudios.rarcompat.network.packets;

import artifacts.registry.ModItems;
import artifacts.registry.ModSoundEvents;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.belt.CloudInBottleItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.data.WorldPosition;
import lombok.Data;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DoubleJumpPacket implements CustomPacketPayload {
    public static final Type<DoubleJumpPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "check_double_jump"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf p_320158_, DoubleJumpPacket p_320396_) {

        }

        @Nonnull
        @Override
        public DoubleJumpPacket decode(@Nonnull RegistryFriendlyByteBuf buf) {
            return new DoubleJumpPacket();
        }

    };

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.CLOUD_IN_A_BOTTLE.value());

            if (stack.getItem() instanceof CloudInBottleItem relic) {
                if (!player.onGround()) {
                    stack.set(DataComponentRegistry.COUNT, stack.getOrDefault(DataComponentRegistry.COUNT, 0) + 1);
                    if (stack.getOrDefault(DataComponentRegistry.COUNT, 0) <= relic.getStatValue(stack, "jump", "count")) {
                        double upwardsMotion = 0.9;

                        if (player.hasEffect(MobEffects.JUMP))
                            upwardsMotion += 0.1 * (double) (Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).getAmplifier() + 1);

                        float direction = (float) ((double) player.getYRot() * Math.PI / 180.0);
                        double horizontalFactor = 4;

                        relic.spreadRelicExperience(player, stack, 1);

                        player.hasImpulse = true;
                        player.awardStat(Stats.JUMP);

                        NetworkHandler.sendToClient(new PlayerMotionPacket(-Mth.sin(direction) / horizontalFactor,
                                upwardsMotion, Mth.cos(direction) / horizontalFactor), (ServerPlayer) player);

                        player.level().playSound(null, player, SoundEvents.CHAIN_BREAK, SoundSource.PLAYERS,
                                1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);

                        createJumpParticles(player, (ServerLevel) player.level());
                    }

                } else
                    stack.set(DataComponentRegistry.COUNT, 0);
            }
        });
    }

    private void createJumpParticles(Player player, ServerLevel level) {
        int particleCount = 70;
        double radius = 1;
        double speed = 0.2;

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;

            level.sendParticles(ParticleTypes.CLOUD, player.getX() + xOffset, player.getY(), player.getZ() + zOffset, 1,
                    speed, 0.0, speed, 0.0);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
