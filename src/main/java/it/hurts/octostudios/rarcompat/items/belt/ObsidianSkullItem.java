package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import top.theillusivec4.curios.api.SlotContext;

public class ObsidianSkullItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("buffer")
                                .stat(StatData.builder("duration")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.ADD, 0.2D)
                                        .formatValue(value -> MathUtils.round(value * 2, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 8, 25).star(1, 3, 16).star(2, 6, 8).star(3, 12, 4)
                                        .star(4, 17, 7).star(5, 19, 13).star(6, 14, 22).star(7, 11, 15)
                                        .link(0, 1).link(0, 7).link(2, 3).link(3, 4).link(4, 5).link(5, 7).link(5, 6).link(7, 6).link(1, 2).link(1, 7)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.NETHER)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.tickCount % 20 != 0)
            return;

        addTime(stack, 1);

        int time = getTime(stack);
        int charges = getCharges(stack);
        double stat = getStatValue(stack, "buffer", "duration") * 2;

        if (time >= 3 && charges > 0 && charges < stat) {
            addCharges(stack, -1);
            addTime(stack, -time);
        } else if (time >= 60 && charges > stat) {
            addCharges(stack, -charges);
            addTime(stack, -time);
        }
    }

    public static void addTime(ItemStack stack, int time) {
        stack.set(DataComponentRegistry.TIME, getTime(stack) + time);
    }

    public static int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public static void addCharges(ItemStack stack, int count) {
        stack.set(DataComponentRegistry.CHARGE, getCharges(stack) + count);
    }

    public static int getCharges(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.CHARGE, 0);
    }

    @EventBusSubscriber
    public static class ObsidianSkull {

        @SubscribeEvent
        public static void onPreGetDamage(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof Player player) || !event.getSource().is(DamageTypeTags.IS_FIRE))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.OBSIDIAN_SKULL.value());

            if (!(stack.getItem() instanceof ObsidianSkullItem relic))
                return;

            double stat = relic.getStatValue(stack, "buffer", "duration") * 2;

            addCharges(stack, 1);
            addTime(stack, -getTime(stack));

            if (getCharges(stack) <= stat)
                event.setNewDamage(0);
        }
    }
}
