package it.hurts.octostudios.rarcompat.network.packets;

import io.netty.buffer.ByteBuf;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.UmbrellaItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

@Data
@AllArgsConstructor
public class RepulsionUmbrellaPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RepulsionUmbrellaPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "repulsion"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RepulsionUmbrellaPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf p_320158_, RepulsionUmbrellaPacket p_320396_) {

        }

        @Nonnull
        @Override
        public RepulsionUmbrellaPacket decode(@Nonnull RegistryFriendlyByteBuf buf) {
            return new RepulsionUmbrellaPacket();
        }

    };

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof UmbrellaItem relic))
                return;

            player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.MASTER, 0.5F, 1 + (player.getRandom().nextFloat() * 0.25F));

            relic.spreadRelicExperience(player, stack, 1);

            int charges = stack.getOrDefault(DataComponentRegistry.CHARGE, 0);

            stack.set(DataComponentRegistry.CHARGE, charges + 1);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
