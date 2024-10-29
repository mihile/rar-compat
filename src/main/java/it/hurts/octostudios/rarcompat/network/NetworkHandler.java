package it.hurts.octostudios.rarcompat.network;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.network.packets.CreateZonePacket;
import it.hurts.octostudios.rarcompat.network.packets.DoubleJumpPacket;
import it.hurts.octostudios.rarcompat.network.packets.PlayerMotionPacket;
import it.hurts.octostudios.rarcompat.network.packets.PowerJumpPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RARCompat.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToClient(PlayerMotionPacket.TYPE, PlayerMotionPacket.STREAM_CODEC, PlayerMotionPacket::handle);
        registrar.playToServer(CreateZonePacket.TYPE, CreateZonePacket.STREAM_CODEC, CreateZonePacket::handle);
        registrar.playToServer(DoubleJumpPacket.TYPE, DoubleJumpPacket.STREAM_CODEC, DoubleJumpPacket::handle);
        registrar.playToServer(PowerJumpPacket.TYPE, PowerJumpPacket.STREAM_CODEC, PowerJumpPacket::handle);
    }
}