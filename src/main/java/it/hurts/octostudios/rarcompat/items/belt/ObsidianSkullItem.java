package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
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
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;

public class ObsidianSkullItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("buffer")
                                .stat(StatData.builder("duration")
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
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff3e265a)
                                .borderBottom(0xff150b2c)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("buffer")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.PURPLE)
                                        .build())
                                .build())
                        .build())
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
        double stat = MathUtils.round(getStatValue(stack, "buffer", "duration") * 2, 0);

        if (time >= 3 && charges > 0 && charges < stat) {
            addCharges(stack, -1);
            addTime(stack, -time);
        } else if (time >= 60 && charges > 0) {
            addCharges(stack, -charges);
            addTime(stack, -time);

            player.playSound(SoundEvents.BAT_TAKEOFF, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
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
        public static void onAttack(LivingIncomingDamageEvent event) {
            Level level = event.getEntity().level();
            if (!(event.getEntity() instanceof Player player) || !event.getSource().is(DamageTypeTags.IS_FIRE) || level.isClientSide || player.tickCount % 10 != 0)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.OBSIDIAN_SKULL.value());

            if (!(stack.getItem() instanceof ObsidianSkullItem relic))
                return;

            double stat = MathUtils.round(relic.getStatValue(stack, "buffer", "duration") * 2, 0);

            addCharges(stack, 1);
            addTime(stack, -getTime(stack));
            event.setCanceled(true);

            if (getCharges(stack) <= stat) {
                relic.spreadRelicExperience(player, stack, 1);

                RandomSource random = level.getRandom();

                ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(
                                new Color(64 + random.nextInt(64), random.nextInt(50), 200 + random.nextInt(55)),
                                0.6F, 20, 0.9F),
                        player.getX(), player.getY() + player.getBbHeight() / 2F, player.getZ(),
                        10, player.getBbWidth() / 2F, player.getBbHeight() / 2F, player.getBbWidth() / 2F,
                        0.025F);

                if (stat == getCharges(stack))
                    player.level().playSound(null, player, SoundEvents.BAT_LOOP, SoundSource.PLAYERS,
                            1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);
            }
        }
    }
}
