package it.hurts.octostudios.rarcompat.items.feet;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.PowerJumpPacket;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.Random;

public class BunnyHoppersItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("hold")
                                .active(CastData.builder().type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("distance")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.06)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 3, 16).star(1, 8, 15).star(2, 7, 11).star(3, 9, 6).star(4, 11, 8)
                                        .star(5, 14, 17).star(6, 16, 14).star(7, 17, 22)
                                        .link(0, 1).link(1, 2).link(2, 3).link(2, 4).link(1, 5).link(5, 6).link(5, 7)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffa89075)
                                .borderBottom(0xff473a2f)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("hold")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !canPlayerUseAbility(player, stack, "hold")
                || !isAbilityTicking(stack, "hold"))
            return;

        if (player.onGround() || getTime(stack) <= 0) {
            addTime(stack, -getTime(stack));
            setToggled(stack, true);
        }

        if (!player.getCommandSenderWorld().isClientSide() || !(player instanceof LocalPlayer localPlayer)
                || getTime(stack) >= getStatValue(stack, "hold", "distance") || player.isFallFlying()
                || !getToggled(stack))
            return;

        if (!localPlayer.input.jumping) {
            setToggled(stack, false);
        } else {
            NetworkHandler.sendToServer(new PowerJumpPacket());

            player.setDeltaMovement(new Vec3(player.getDeltaMovement().x, 0.6 + ((double) getTime(stack) / 80), player.getDeltaMovement().z));

            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.5;
                double offsetY = (random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (random.nextDouble() - 0.5) * 0.5;

                player.level().addParticle(ParticleUtils.constructSimpleSpark(new Color(200 + random.nextInt(56), 200 + random.nextInt(56), 200 + random.nextInt(56)),
                                0.7F, 40, 0.9F),
                        player.getX() + offsetX,
                        player.getY() + 0.1 + offsetY,
                        player.getZ() + offsetZ,
                        0, 0, 0);
            }
        }
    }

    public void addTime(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, getTime(stack) + val);
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public void setToggled(ItemStack stack, boolean val) {
        stack.set(DataComponentRegistry.TOGGLED, val);
    }

    public boolean getToggled(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TOGGLED, false);
    }

    @EventBusSubscriber
    public static class BunnyHoppersEvent {
        @SubscribeEvent
        public static void onJumping(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.BUNNY_HOPPERS.value());

            if (!(stack.getItem() instanceof BunnyHoppersItem relic))
                return;

            relic.addTime(stack, -relic.getTime(stack));
        }

        @SubscribeEvent
        public static void onFall(LivingFallEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.BUNNY_HOPPERS.value());

            if (!(stack.getItem() instanceof BunnyHoppersItem relic) || player.getCommandSenderWorld().isClientSide())
                return;

            event.setDistance(event.getDistance() - (float) relic.getTime(stack));
        }
    }
}
