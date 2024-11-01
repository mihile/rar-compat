package it.hurts.octostudios.rarcompat.items.feet;

import artifacts.registry.ModItems;
import artifacts.registry.ModSoundEvents;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.awt.*;
import java.util.Random;

public class KittySlippersItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("fall")
                                .stat(StatData.builder("modifier")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(2D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .ability(AbilityData.builder("resurrected")
                                .requiredLevel(5)
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.05D, 0.1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.075)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 15, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .build())
                .build();
    }

    @Override
    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(Attributes.SAFE_FALL_DISTANCE, (float) getStatValue(stack, "fall", "modifier") / 2))
                .build();
    }

    @EventBusSubscriber
    public static class KittySlippersEvent {
        @SubscribeEvent
        public static void onPlayerFall(LivingFallEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            float fallDistance = event.getDistance();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.KITTY_SLIPPERS.value());

            if (!(stack.getItem() instanceof KittySlippersItem relic))
                return;

            if (fallDistance > 4F)
                relic.spreadRelicExperience(player, stack, 1);
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.KITTY_SLIPPERS.value());

            if (!(stack.getItem() instanceof KittySlippersItem relic) || new Random().nextFloat(1) > relic.getStatValue(stack, "resurrected", "chance"))
                return;

            Level level = player.level();
            Random random = new Random();

            level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, player.getSoundSource(), 1F, 0.75F + random.nextFloat(1) * 0.5F);

            for (int i = 0; i < 50; i++) {
                ((ServerLevel) level).sendParticles(
                        ParticleUtils.constructSimpleSpark(new Color(100 + random.nextInt(156), random.nextInt(100 + random.nextInt(156)), random.nextInt(100 + random.nextInt(156))), 0.5F, 60, 0.95F),
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        1,
                        (random.nextDouble() - 0.5) * 3.0,
                        random.nextDouble() * 1.5,
                        (random.nextDouble() - 0.5) * 3.0,
                        0.05
                );
            }

            player.setHealth(1.0F);
            event.setCanceled(true);
        }
    }
}
