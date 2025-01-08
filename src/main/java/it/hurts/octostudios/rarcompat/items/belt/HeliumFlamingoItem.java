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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import top.theillusivec4.curios.api.SlotContext;

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
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("speed")
                                        .initialValue(0.2D, 0.3D)
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
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("flying")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())

                .loot(LootData.builder()
                        .entry(LootCollections.DESERT)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (stage == CastStage.END)
            setToggled(stack, false);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !isAbilityTicking(stack, "flying") || player.isInWater())
            return;

        if (player.tickCount % 20 == 0 && getToggled(stack)) {
            addTime(stack, 1);
            spreadRelicExperience(player, stack, 1);
        }

        int statValue = (int) MathUtils.round(getStatValue(stack, "flying", "time"), 0);

        if (getTime(stack) >= statValue) {
            player.setDeltaMovement(player.getDeltaMovement().x, -0.25, player.getKnownMovement().z);
            player.fallDistance = 0;

            setToggled(stack, false);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || newStack == stack)
            return;

        EntityUtils.removeAttribute(player, stack, NeoForgeMod.SWIM_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        setToggled(stack, false);
        setTime(stack, 0);
    }

    public boolean getToggled(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TOGGLED, false);
    }

    public void setToggled(ItemStack stack, boolean val) {
        stack.set(DataComponentRegistry.TOGGLED, val);
    }

    public void addTime(ItemStack stack, int time) {
        setTime(stack, getTime(stack) + time);
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public void setTime(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, Math.max(val, 0));
    }

    @EventBusSubscriber
    public static class HeliumFlamingoEvent {
        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.HELIUM_FLAMINGO.value());

            if (!(stack.getItem() instanceof HeliumFlamingoItem relic) || player.isInWater()
                    || !relic.isAbilityTicking(stack, "flying"))
                return;

            var statValue = (int) MathUtils.round(relic.getStatValue(stack, "flying", "time"), 0);
            var time = relic.getTime(stack);

            if (time >= statValue || Math.abs(player.getKnownMovement().x) <= 0.01D || Math.abs(player.getKnownMovement().z) <= 0.01D)
                return;

            player.setSprinting(true);
            relic.setToggled(stack, true);
        }

        @SubscribeEvent
        public static void onSwimAir(PlayerSwimEvent event) {
            Player player = event.getEntity();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.HELIUM_FLAMINGO.value());

            if (!(stack.getItem() instanceof HeliumFlamingoItem relic) || player.isInWater() || !relic.isAbilityTicking(stack, "flying"))
                return;

            if (player.onGround()) {
                relic.setTime(stack, 0);
                relic.setToggled(stack, false);
            }

            if (relic.getToggled(stack)) {
                event.setResult(EventResult.SUCCESS);
                player.setSprinting(true);

                EntityUtils.applyAttribute(player, stack, NeoForgeMod.SWIM_SPEED, (float) relic.getStatValue(stack, "flying", "speed"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            } else {
                event.setResult(EventResult.PASS);

                EntityUtils.removeAttribute(player, stack, NeoForgeMod.SWIM_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            }
        }
    }
}