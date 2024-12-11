package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

public class PowerGloveItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("power")
                                .stat(StatData.builder("amount")
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 2, 24).star(1, 8, 23).star(2, 5, 18).star(3, 5, 11)
                                        .star(4, 8, 7).star(5, 14, 7).star(6, 20, 13).star(7, 20, 20)
                                        .star(8, 15, 23).star(9, 20, 24)
                                        .link(0, 1).link(1, 2).link(2, 3).link(3, 4).link(4, 5).link(5, 6).link(6, 7).link(7, 8).link(8, 9)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffd8b200)
                                .borderBottom(0xffb57e00)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("power")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
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
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        stack.set(DataComponentRegistry.COUNT, stack.getOrDefault(DataComponentRegistry.COUNT, 0) + 1);
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onPlayerAttack(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.POWER_GLOVE.value());

            if (!(stack.getItem() instanceof PowerGloveItem relic) || stack.getOrDefault(DataComponentRegistry.COUNT, 0) < 5)
                return;

            relic.spreadRelicExperience(player, stack, 1);

            event.setAmount((float) (event.getAmount() + (event.getAmount() * relic.getStatValue(stack, "power", "amount"))));

            stack.set(DataComponentRegistry.COUNT, 0);

            RandomSource random = player.getRandom();

            for (int i = 0; i < 20; i++) {
                ((ServerLevel) player.level()).sendParticles(ParticleUtils.constructSimpleSpark(new Color(200 + random.nextInt(55), 100 + random.nextInt(100), random.nextInt(50)),
                                0.7F, 60, 0.9F),
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        1,
                        0.0,
                        0.5,
                        0.0,
                        0.05
                );
            }
        }

    }
}
