package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
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
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.fixes.NeoForgeEntityLegacyAttributesFix;
import top.theillusivec4.curios.api.SlotContext;

import static it.hurts.octostudios.rarcompat.items.belt.HeliumFlamingoItem.HeliumFlamingoEvent.addTime;
import static it.hurts.octostudios.rarcompat.items.belt.HeliumFlamingoItem.HeliumFlamingoEvent.getTime;

public class HeliumFlamingoItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("flying")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 2, 6).star(1, 8, 10).star(2, 6, 19)
                                        .star(3, 15, 21).star(4, 20, 11)
                                        .link(0, 2).link(1, 2).link(2, 3).link(3, 4)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xfff47d92)
                                .borderBottom(0xffb43263)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.DESERT)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (stage == CastStage.END)
            setAbilityCooldown(stack, "flying", 60);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !isAbilityTicking(stack, "flying") || player.isInWater())
            return;

        if (slotContext.entity().tickCount % 20 == 0)
            addTime(stack, 1);

        if (getTime(stack) > (int) getStatValue(stack, "flying", "time")) {
            player.setDeltaMovement(player.getDeltaMovement().x, -0.08, player.getKnownMovement().z);
            player.fallDistance = 0;
        }
    }

    @EventBusSubscriber
    public static class HeliumFlamingoEvent {

        @SubscribeEvent
        public static void onSwimAir(PlayerSwimEvent event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.HELIUM_FLAMINGO.value());

            if (!(stack.getItem() instanceof HeliumFlamingoItem relic) || player.isInWater() || player.getSpeed() >= 0.15)
                return;

            int timeWorked = (int) relic.getStatValue(stack, "flying", "time");

            if (getTime(stack) > timeWorked && player.onGround()) {
                addTime(stack, -getTime(stack));

                relic.setAbilityCooldown(stack, "flying", 40);

                return;
            }

            player.setSpeed(Math.min(1F, player.getSpeed()));

            if (relic.isAbilityTicking(stack, "flying") && getTime(stack) <= timeWorked)
                event.setResult(EventResult.SUCCESS);
        }

        public static void addTime(ItemStack stack, int val) {
            stack.set(DataComponentRegistry.TIME, getTime(stack) + val);
        }

        public static int getTime(ItemStack stack) {
            return stack.getOrDefault(DataComponentRegistry.TIME, 0);
        }

    }
}