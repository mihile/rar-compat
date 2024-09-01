package it.hurts.octostudios.rarcompat.network;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.network.packets.PacketCreateZone;
import it.hurts.sskirillss.relics.network.packets.PacketItemActivation;
import it.hurts.sskirillss.relics.network.packets.PacketPlayerMotion;
import it.hurts.sskirillss.relics.network.packets.PacketSyncEntityEffects;
import it.hurts.sskirillss.relics.network.packets.abilities.SpellCastPacket;
import it.hurts.sskirillss.relics.network.packets.capability.CapabilitySyncPacket;
import it.hurts.sskirillss.relics.network.packets.leveling.PacketRelicTweak;
import it.hurts.sskirillss.relics.network.packets.sync.SyncTargetPacket;
import it.hurts.sskirillss.relics.utils.Reference;
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

        registrar.playToServer(PacketCreateZone.TYPE, PacketCreateZone.STREAM_CODEC, PacketCreateZone::handle);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToClient(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <MSG extends CustomPacketPayload> void sendToClientsTrackingEntity(MSG message, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, message);
    }

    public static <MSG extends CustomPacketPayload> void sendToClientsTrackingEntityAndSelf(MSG message, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, message);
    }
}