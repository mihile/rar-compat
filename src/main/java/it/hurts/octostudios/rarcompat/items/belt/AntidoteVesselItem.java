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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.lang.reflect.Field;

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
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @EventBusSubscriber
    public static class AntidoteVeselEvent {

        @SubscribeEvent
        public static void onMobEffect(MobEffectEvent.Added event) {
            MobEffectInstance effectInstance = event.getEffectInstance();

            if (effectInstance == MobEffects.BAD_OMEN || effectInstance == MobEffects.TRIAL_OMEN || effectInstance == MobEffects.RAID_OMEN
                    || effectInstance.getEffect().value().isBeneficial() || !(event.getEntity() instanceof Player player) || player.level().isClientSide)
                return;

            ItemStack itemStack = EntityUtils.findEquippedCurio(player, ModItems.ANTIDOTE_VESSEL.value());

            if (!(itemStack.getItem() instanceof AntidoteVesselItem relic))
                return;

            int newDuration = (int) (effectInstance.getDuration() * (1 - relic.getStatValue(itemStack, "antidote", "amount")));

            relic.spreadRelicExperience(player, itemStack, 1);

            try {
                Field durationField = MobEffectInstance.class.getDeclaredField("duration");

                durationField.setAccessible(true);

                durationField.setInt(effectInstance, newDuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
