package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class AntidoteVesselItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("antidote")
                                .stat(StatData.builder("amount")
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 4, 7).star(1, 12, 18).star(2, 5, 26).star(3, 15, 26)
                                        .link(0, 1).link(1, 2).link(1, 2).link(1, 3)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("devourer")
                                .stat(StatData.builder("duration")
                                        .initialValue(0.1D, 0.3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffe19d25)
                                .borderBottom(0xff7c4023)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("antidote")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.YELLOW)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("devourer ")
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

    @EventBusSubscriber
    public static class AntidoteVeselEvents {
        @SubscribeEvent
        public static void onIncomingDamage(AttackEntityEvent event) {
            if (!(event.getTarget() instanceof LivingEntity target) || target.getCommandSenderWorld().isClientSide()
                    || target.getActiveEffects().isEmpty())
                return;

            var player = event.getEntity();
            var stack = EntityUtils.findEquippedCurio(player, ModItems.ANTIDOTE_VESSEL.value());

            if (!(stack.getItem() instanceof AntidoteVesselItem relic) || !relic.canPlayerUseAbility(player, stack, "devourer"))
                return;

            for (var activeEffect : target.getActiveEffects().stream().filter(effect -> effect.getEffect().value().isBeneficial()).toList()) {
                var transferDuration = (int) (activeEffect.getDuration() * relic.getStatValue(stack, "devourer", "duration"));

                target.removeEffect(activeEffect.getEffect());
                target.addEffect(new MobEffectInstance(activeEffect.getEffect(), activeEffect.getDuration() - transferDuration, activeEffect.getAmplifier()));

                MobEffectInstance existingEffect = player.getEffect(activeEffect.getEffect());

                if (existingEffect != null) {
                    transferDuration += existingEffect.getDuration();
                    player.removeEffect(activeEffect.getEffect());
                }

                player.addEffect(new MobEffectInstance(activeEffect.getEffect(), transferDuration, activeEffect.getAmplifier()));

                relic.spreadRelicExperience(player, stack, 1);
            }
        }

        @SubscribeEvent
        public static void onAddedEffect(MobEffectEvent.Added event) {
            var effectDuration = event.getEffectInstance();

            if (!(event.getEntity() instanceof Player player) || player.getCommandSenderWorld().isClientSide()
                    || effectDuration.getEffect().value().isBeneficial())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.ANTIDOTE_VESSEL.value());

            if (!(stack.getItem() instanceof AntidoteVesselItem relic) || !relic.canPlayerUseAbility(player, stack, "antidote"))
                return;

            effectDuration.duration = (int) (effectDuration.getDuration() * (1 - relic.getStatValue(stack, "antidote", "amount")));

            relic.spreadRelicExperience(player, stack, 1);
        }
    }
}
