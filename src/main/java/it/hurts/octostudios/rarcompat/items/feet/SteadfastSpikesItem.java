package it.hurts.octostudios.rarcompat.items.feet;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.api.events.common.LivingSlippingEvent;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

public class SteadfastSpikesItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("resistance")
                                .stat(StatData.builder("modifier")
                                        .icon(StatIcons.MULTIPLIER)
                                        .initialValue(0.2, 0.3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @Nullable
    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier
                .builder().attribute(new RelicAttributeModifier.Modifier(Attributes.KNOCKBACK_RESISTANCE, (float) this.getStatValue(stack, "resistance", "modifier")))
                .build();
    }

    @EventBusSubscriber
    public static class SteadfastSpikesEvent {

        @SubscribeEvent
        public static void onLivingSlipping(LivingSlippingEvent event) {
            if (event.getFriction() <= 0.6F || !(event.getEntity() instanceof Player player)
                    || player.isInWater() || player.isInLava())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.STEADFAST_SPIKES.value());

            if (!(stack.getItem() instanceof SteadfastSpikesItem relic))
                return;

            event.setFriction((float) (event.getFriction() * (1 - (relic.getStatValue(stack, "resistance", "modifier") / 3))));
        }
    }
}
