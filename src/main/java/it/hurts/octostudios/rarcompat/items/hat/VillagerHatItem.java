package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.items.feet.KittySlippersItem;
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
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class VillagerHatItem extends WearableRelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("discount")
                                .stat(StatData.builder("multiplier")
                                        .initialValue(10D, 20D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.3D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 3, 25).star(1, 12, 23).star(2, 12, 19)
                                        .star(3, 9, 15).star(4, 9, 7).star(5, 15, 7)
                                        .star(6, 15, 15)
                                        .link(0, 1).link(0, 2).link(0, 3).link(3, 4).link(4, 5).link(5, 6).link(6, 3)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("passive")
                                .maxLevel(0)
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffd8c348)
                                .borderBottom(0xff8a5b35)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("discount")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
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
        if (!(slotContext.entity() instanceof Player player) || getIronGolems(player, player.getCommandSenderWorld()) == null)
            return;

        for (IronGolem mob : getIronGolems(player, player.getCommandSenderWorld())) {
            if (mob.getTarget() != player)
                break;

            mob.setTarget(null);
        }
    }

    public List<IronGolem> getIronGolems(Player player, Level level) {
        return level.getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(5)).stream().filter(mob -> mob.getTarget() == player && !mob.isDeadOrDying()).toList();
    }

    @EventBusSubscriber
    public static class VillagerHatEvent {
        @SubscribeEvent
        public static void onLivingChangeTargetEvent(LivingChangeTargetEvent event) {
            if (!(event.getEntity() instanceof IronGolem) || !(event.getNewAboutToBeSetTarget() instanceof Player player))
                return;

            ItemStack itemStack = EntityUtils.findEquippedCurio(player, ModItems.VILLAGER_HAT.value());

            if (itemStack.getItem() instanceof VillagerHatItem)
                event.setCanceled(true);
        }

    }
}