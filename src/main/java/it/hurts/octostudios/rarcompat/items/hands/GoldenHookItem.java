package it.hurts.octostudios.rarcompat.items.hands;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class GoldenHookItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("hook")
                                .stat(StatData.builder("amount")
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 6, 10).star(1, 10, 5).star(2, 15, 10).star(3, 10, 18)
                                        .star(4, 7, 23).star(5, 13, 23)
                                        .link(0, 1).link(1, 2).link(2, 3).link(4, 5).link(3, 4).link(3, 5)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xfffced59)
                                .borderBottom(0xffe6af15)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("hook")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.BASTION)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class GoldenHookEvent {

        @SubscribeEvent
        public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
            Player player = event.getAttackingPlayer();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.GOLDEN_HOOK.value());

            if (!(stack.getItem() instanceof GoldenHookItem relic) || !relic.isAbilityUnlocked(stack, "hook"))
                return;

            double boostedExperience = event.getOriginalExperience() * relic.getStatValue(stack, "hook", "amount");

            relic.spreadRelicExperience(player, stack, 1);

            event.setDroppedExperience((int) (event.getOriginalExperience() + boostedExperience));
        }

    }

}
