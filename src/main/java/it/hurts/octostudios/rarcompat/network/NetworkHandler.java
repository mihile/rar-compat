package it.hurts.octostudios.rarcompat.network;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.network.packets.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    @SubscribeEvent
    public static void onRegisterPayloadHandler(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(RARCompat.MODID)
                .versioned("1.0")
                .optional();
        registrar.playToServer(DoubleJumpPacket.TYPE, DoubleJumpPacket.STREAM_CODEC, DoubleJumpPacket::handle);
        registrar.playToServer(PowerJumpPacket.TYPE, PowerJumpPacket.STREAM_CODEC, PowerJumpPacket::handle);
        registrar.playToServer(RepulsionUmbrellaPacket.TYPE, RepulsionUmbrellaPacket.STREAM_CODEC, RepulsionUmbrellaPacket::handle);
    }
}