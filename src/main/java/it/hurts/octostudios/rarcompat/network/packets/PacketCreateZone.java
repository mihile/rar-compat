package it.hurts.octostudios.rarcompat.network.packets;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.data.WorldPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

@Data
@AllArgsConstructor
public class PacketCreateZone implements CustomPacketPayload {

    public static final Type<PacketCreateZone> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "create_visibility_zone"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCreateZone> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf p_320158_, PacketCreateZone p_320396_) {

        }

        @Nonnull
        @Override
        public PacketCreateZone decode(@Nonnull RegistryFriendlyByteBuf buf) {
            return new PacketCreateZone();
        }

    };

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SCARF_OF_INVISIBILITY.value());

            if (stack.get(DataComponentRegistry.WORLD_POSITION) == null) {
                stack.set(DataComponentRegistry.WORLD_POSITION, new WorldPosition(player));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
