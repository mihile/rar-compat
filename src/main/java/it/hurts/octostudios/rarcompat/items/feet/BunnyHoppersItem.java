package it.hurts.octostudios.rarcompat.items.feet;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.PowerJumpPacket;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

public class BunnyHoppersItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("hold")
                                .active(CastData.builder().type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("distance")
                                        .icon(StatIcons.DISTANCE)
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
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class BunnyHoppersServerEvent {

        @SubscribeEvent
        public static void onJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player) || player.level().isClientSide)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.BUNNY_HOPPERS.value());

            if (!(stack.getItem() instanceof BunnyHoppersItem relic) || !relic.isAbilityTicking(stack, "hold"))
                return;

            relic.spreadRelicExperience(player, stack, 1);

            PowerJumpPacket.createJump(1, (ServerPlayer) player);
            stack.set(DataComponentRegistry.TOGGLED, true);
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class BunnyHoppersClintEvent {

        @SubscribeEvent
        public static void onHoldJump(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();

            Player playerClient = minecraft.player;
            ItemStack stack = EntityUtils.findEquippedCurio(playerClient, ModItems.BUNNY_HOPPERS.value());

            if (minecraft.screen == null && stack.getItem() instanceof BunnyHoppersItem relic && playerClient != null
                    && event.getKey() == minecraft.options.keyJump.getKey().getValue() && Boolean.TRUE.equals(stack.get(DataComponentRegistry.TOGGLED))
                    && relic.isAbilityTicking(stack, "hold")) {

                NetworkHandler.sendToServer(new PowerJumpPacket(event.getAction()));
            }
        }
    }
}
