package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.packets.DoubleJumpPacket;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
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
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Objects;

public class CloudInBottleItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("jump")
                                .stat(StatData.builder("count")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.5)
                                        .formatValue(value -> (int) MathUtils.round(value, 0))
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
                                .source(LevelingSourceData.abilityBuilder("jump")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !canPlayerUseAbility(player, stack, "jump")
                || !player.onGround() || stack.getOrDefault(DataComponentRegistry.COUNT, 0) <= 0)
            return;

        stack.set(DataComponentRegistry.COUNT, 0);
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class CloudInBottleEvent {
        @SubscribeEvent
        public static void onMouseInput(InputEvent.Key event) {
            Minecraft minecraft = Minecraft.getInstance();

            Player player = minecraft.player;

            if (player == null)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.CLOUD_IN_A_BOTTLE.value());

            if (minecraft.screen != null || event.getAction() != 1 || !(stack.getItem() instanceof CloudInBottleItem relic)
                    || !relic.canPlayerUseAbility(player, stack, "jump") || event.getKey() != minecraft.options.keyJump.getKey().getValue() || player.onGround()
                    || stack.getOrDefault(DataComponentRegistry.COUNT, 0) > Math.round(relic.getStatValue(stack, "jump", "count")))
                return;

            double upwardsMotion = 0.65;

            if (player.hasEffect(MobEffects.JUMP))
                upwardsMotion += 0.1 * (double) Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).getAmplifier();

            float direction = (float) (player.getYRot() * Math.PI / 180.0);
            double horizontalFactor = 3;

            player.setDeltaMovement(-Mth.sin(direction) / horizontalFactor, upwardsMotion, Mth.cos(direction) / horizontalFactor);

            NetworkHandler.sendToServer(new DoubleJumpPacket());
        }
    }
}
