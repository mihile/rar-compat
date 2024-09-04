package it.hurts.octostudios.rarcompat.network.packets;

import artifacts.registry.ModItems;
import io.netty.buffer.ByteBuf;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PacketCreateZone implements CustomPacketPayload {
    private final String playerUUID;

    public static final Type<PacketCreateZone> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "create_visibility_zone"));

    public static final StreamCodec<ByteBuf, PacketCreateZone> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketCreateZone::getPlayerUUID,
            PacketCreateZone::new
    );

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(UUID.fromString(playerUUID));

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SCARF_OF_INVISIBILITY.value());

            stack.set(DataComponentRegistry.TOGGLED, false);

            if (stack.get(DataComponentRegistry.TARGET) == null)
                stack.set(DataComponentRegistry.TARGET, player.getX() + " " + player.getY() + " " + player.getZ());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
