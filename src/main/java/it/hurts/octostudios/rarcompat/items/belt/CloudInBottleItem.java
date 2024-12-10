package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.DoubleJumpPacket;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
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
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

public class CloudInBottleItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("jump")
                                .stat(StatData.builder("count")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5)
                                        .formatValue(value -> MathUtils.round(value, 0))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 15, 3).star(1, 19, 5).star(2, 9, 8).star(3, 14, 9)
                                        .star(4, 17, 11).star(5, 9, 15).star(6, 9, 20).star(7, 3, 24)
                                        .star(8, 15, 24).star(9, 9, 27)
                                        .link(0, 2).link(0, 3).link(1, 3).link(3, 4).link(3, 5)
                                        .link(5, 6).link(6, 7).link(6, 8).link(9, 7).link(9, 8)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffd0eae9)
                                .borderBottom(0xff5c8dc0)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("diving")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.CYAN)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class CloudInBottleEvent {

        @SubscribeEvent
        public static void onMouseInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();

            Player playerClient = minecraft.player;
            ItemStack stack = EntityUtils.findEquippedCurio(playerClient, ModItems.CLOUD_IN_A_BOTTLE.value());

            if (minecraft.screen == null && event.getAction() == 1 && stack.getItem() instanceof CloudInBottleItem && playerClient != null
                    && event.getKey() == minecraft.options.keyJump.getKey().getValue()) {

                NetworkHandler.sendToServer(new DoubleJumpPacket());
            }
        }
    }
}
