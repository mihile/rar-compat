package it.hurts.octostudios.rarcompat.network.packets;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.charm.HeliumFlamingoItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public class FlamingoSwimPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FlamingoSwimPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RARCompat.MODID, "swim"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlamingoSwimPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, FlamingoSwimPacket packet) {

        }

        @Nonnull
        @Override
        public FlamingoSwimPacket decode(@Nonnull RegistryFriendlyByteBuf buf) {
            return new FlamingoSwimPacket();
        }
    };

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.HELIUM_FLAMINGO.value());

            if (!(stack.getItem() instanceof HeliumFlamingoItem relic) || player.isInWater()
                    || !relic.isAbilityTicking(stack, "flying"))
                return;

            var statValue = (int) MathUtils.round(relic.getStatValue(stack, "flying", "time"), 0);
            var time = relic.getTime(stack);

            if (time >= statValue || Math.abs(player.getKnownMovement().x) <= 0.01D || Math.abs(player.getKnownMovement().z) <= 0.01D)
                return;

            player.setSprinting(true);
            relic.setToggled(stack, true);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
