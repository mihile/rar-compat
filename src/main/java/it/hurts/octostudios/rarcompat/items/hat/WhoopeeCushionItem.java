package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import artifacts.registry.ModSoundEvents;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class WhoopeeCushionItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("push")
                                .stat(StatData.builder("radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.06)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.075)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 10, 13).star(1, 8, 6).star(2, 16, 9).star(3, 17, 14)
                                        .star(4, 16, 22).star(5, 6, 21).star(6, 3, 13)
                                        .link(0, 1).link(0, 2).link(0, 3).link(0, 4).link(0, 5).link(0, 6).link(0, 6)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffdd5959)
                                .borderBottom(0xff7c2a2a)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("push")
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
        if (!(slotContext.entity() instanceof Player player) || player.level().isClientSide)
            return;

        boolean isSneaking = player.isShiftKeyDown();
        boolean crouchState = stack.getOrDefault(DataComponentRegistry.TOGGLED, false);

        if (isSneaking && !crouchState && new Random().nextDouble() < 0.2)
            createWhoopee(player.level(), player, this, stack);

        stack.set(DataComponentRegistry.TOGGLED, isSneaking);
    }

    public static void createWhoopee(Level level, Player player, WhoopeeCushionItem relic, ItemStack stack) {
        level.playSound(null, player.blockPosition(), ModSoundEvents.FART.value(), player.getSoundSource(), 1F, 0.75F + new Random().nextFloat(1) * 0.5F);

        relic.spreadRelicExperience(player, stack, 1);

        double radius = relic.getStatValue(stack, "push", "radius");

        for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(radius))) {
            mob.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 1));
            mob.setDeltaMovement(mob.position().subtract(player.position()).normalize());
        }
    }

    @EventBusSubscriber
    public static class WhoopeeCushionEvent {

        @SubscribeEvent
        public static void onAttackPlayer(LivingIncomingDamageEvent event) {
            Entity attacker = event.getSource().getEntity();

            if (!(event.getEntity() instanceof Player player) || attacker == player)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.WHOOPEE_CUSHION.value());

            Level level = player.level();

            if (!(stack.getItem() instanceof WhoopeeCushionItem relic) || new Random().nextDouble(1) > relic.getStatValue(stack, "push", "chance"))
                return;

            WhoopeeCushionItem.createWhoopee(level, player, relic, stack);
        }
    }
}
