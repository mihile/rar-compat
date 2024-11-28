package it.hurts.octostudios.rarcompat.items.feet;

import artifacts.registry.ModItems;
import artifacts.registry.ModSoundEvents;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
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
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
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
                                .research(ResearchData.builder()
                                        .star(0, 6, 24).star(1, 2, 19).star(2, 2, 9).star(3, 6, 4)
                                        .star(4, 16, 4).star(5, 20, 9).star(6, 20, 19).star(7, 16, 24)
                                        .star(8, 11, 16).star(9, 8, 19).star(10, 5, 13).star(11, 8, 8)
                                        .star(12, 14, 19).star(13, 17, 13).star(14, 14, 8)
                                        .link(0, 1).link(1, 2).link(2, 3).link(3, 4).link(4, 5).link(5, 6).link(6, 7).link(7, 0)
                                        .link(0, 1).link(1, 2).link(2, 3).link(3, 4).link(4, 5).link(5, 6).link(6, 7).link(7, 0)
                                        .link(8, 9).link(9, 10).link(8, 11).link(8, 12).link(12, 13).link(8, 14)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("resurrected")
                                .requiredLevel(5)
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.05D, 0.1D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 3, 16).star(1, 8, 15).star(2, 7, 11)
                                        .star(3, 9, 6).star(4, 11, 8)
                                        .star(5, 14, 17).star(6, 16, 14).star(7, 17, 22)
                                        .link(0, 1).link(1, 2).link(2, 3).link(2, 4).link(1, 5).link(5, 6).link(5, 7)
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffededed)
                                .borderBottom(0xff696969)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 15, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.JUNGLE)
                        .entry(LootCollections.VILLAGE)
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
        public static void onLivingHurt(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            int damage = (int) event.getOriginalDamage();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.KITTY_SLIPPERS.value());

            if (!(stack.getItem() instanceof KittySlippersItem relic))
                return;

            stack.set(DataComponentRegistry.COUNT, damage);
        }

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

            if (!(stack.getItem() instanceof KittySlippersItem relic) || new Random().nextFloat(1) > relic.getStatValue(stack, "resurrected", "chance")
                    || !relic.canPlayerUseAbility(player, stack, "resurrected"))
                return;

            Level level = player.level();
            Random random = new Random();

            level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, player.getSoundSource(), 1F, 0.75F + random.nextFloat(1) * 0.5F);

            for (int i = 0; i < 50; i++)
                ((ServerLevel) level).sendParticles(
                        ParticleUtils.constructSimpleSpark(new Color(100 + random.nextInt(156), random.nextInt(100 + random.nextInt(156)), random.nextInt(100 + random.nextInt(156))), 0.5F, 60, 0.95F),
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        1,
                        (random.nextDouble() - 0.5) * 3.0,
                        random.nextDouble() * 1.5,
                        (random.nextDouble() - 0.5) * 3.0,
                        0.05
                );

            relic.spreadRelicExperience(player, stack, stack.getOrDefault(DataComponentRegistry.COUNT, 1));

            player.setHealth(1.0F);

            event.setCanceled(true);

            stack.set(DataComponentRegistry.TOGGLED, false);
        }
    }
}
