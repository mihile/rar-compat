package it.hurts.octostudios.rarcompat.network.packets;

import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.UmbrellaItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.Random;

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

            Level level = player.getCommandSenderWorld();

            level.playSound(null, player.blockPosition(), SoundEvents.PHANTOM_FLAP, SoundSource.MASTER, 1F, 1 + (player.getRandom().nextFloat() * 0.25F));

            relic.spreadRelicExperience(player, stack, 1);

            relic.addCharges(stack, 1);

            Vec3 lookDirection = player.getLookAngle();

            Random random = new Random();

            for (int i = 0; i < 40; i++) {
                Vec3 basePosition = player.position().add(0, 0.5, 0).add(lookDirection.scale(i * 0.2));

                Vec3 rightVector = new Vec3(-lookDirection.z, 0, lookDirection.x).normalize();
                Vec3 upVector = rightVector.cross(lookDirection).normalize();

                Vec3 particlePosition = basePosition.add(rightVector.scale((random.nextDouble() - 0.5) * 2.0)).add(upVector.scale((random.nextDouble() - 0.5) * 2));

                ((ServerLevel) level).sendParticles(
                        ParticleTypes.CLOUD,
                        particlePosition.x,
                        particlePosition.y,
                        particlePosition.z,
                        1,
                        0, 0, 0, 0
                );
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
