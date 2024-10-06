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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@Data
@AllArgsConstructor
public class CreateZonePacket implements CustomPacketPayload {

    public static final Type<CreateZonePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "create_visibility_zone"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateZonePacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf p_320158_, CreateZonePacket p_320396_) {

        }

        @Nonnull
        @Override
        public CreateZonePacket decode(@Nonnull RegistryFriendlyByteBuf buf) {
            return new CreateZonePacket();
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
