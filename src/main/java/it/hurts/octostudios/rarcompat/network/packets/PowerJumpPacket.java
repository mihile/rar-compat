package it.hurts.octostudios.rarcompat.network.packets;

import artifacts.registry.ModItems;
import io.netty.buffer.ByteBuf;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.feet.BunnyHoppersItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

@Data
@AllArgsConstructor
public class PowerJumpPacket implements CustomPacketPayload {
    private final int action;

    public static final CustomPacketPayload.Type<PowerJumpPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "power_jump"));

    public static final StreamCodec<ByteBuf, PowerJumpPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PowerJumpPacket::getAction,
            PowerJumpPacket::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.BUNNY_HOPPERS.value());

            if (!(stack.getItem() instanceof BunnyHoppersItem relic))
                return;

            int action = this.getAction();
            double limit = relic.getStatValue(stack, "hold", "distance");

            stack.set(DataComponentRegistry.TIME, stack.getOrDefault(DataComponentRegistry.TIME, 0) + 1);

            if (action == 0) {
                stack.set(DataComponentRegistry.TOGGLED, false);
                stack.set(DataComponentRegistry.TIME, 0);
            }

            if (Boolean.TRUE.equals(stack.get(DataComponentRegistry.TOGGLED)) && stack.getOrDefault(DataComponentRegistry.TIME, 0) <= limit) {
                NetworkHandler.sendToClient(new PlayerMotionPacket(0, 0.2, 0), player);

                Random random = new Random();

                ((ServerLevel) player.level()).sendParticles(
                        ParticleUtils.constructSimpleSpark(new Color(200 + random.nextInt(56), 200 + random.nextInt(56), 200 + random.nextInt(56)),
                                0.7F, 40, 0.9F),
                        player.getX(),
                        player.getY() + 0.1,
                        player.getZ(),
                        10,
                        0.3, 0.3, 0.3,
                        0.02
                );
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
