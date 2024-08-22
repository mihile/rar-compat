package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.base.WearableRelicItem;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ScarfOfInvisibilityItem extends WearableRelicItem {
    private static Vec3 prePos;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("invisible")
                                .stat(StatData.builder("threshold")
                                        .initialValue(1D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(1D, 10D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;

        boolean inRadius = true;
        if (prePos != null) {
            double distance = Math.sqrt(Math.pow(player.getX() - prePos.x, 2) + Math.pow(player.getZ() - prePos.z, 2));
            inRadius = distance >= this.getStatValue(stack, "invisible", "radius");
        }

        if (player.getSpeed() < this.getStatValue(stack, "invisible", "threshold") && inRadius) {
            player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
        }
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onRight(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getEntity();
            interaction(event.getEntity(), player.position());

            prePos = new Vec3(player.getX(), player.getY(), player.getZ());
        }

        @SubscribeEvent
        public static void onLeft(PlayerInteractEvent.LeftClickBlock event) {
            Player player = event.getEntity();
            interaction(event.getEntity(), player.position());

            prePos = new Vec3(player.getX(), player.getY(), player.getZ());
        }

        private static void interaction(Player player, Vec3 pos) {
            Level level = player.level();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SCARF_OF_INVISIBILITY.value());

            if (!(stack.getItem() instanceof ScarfOfInvisibilityItem relic) || !player.hasEffect(EffectRegistry.VANISHING))
                return;

            player.removeEffect(EffectRegistry.VANISHING);

            double radius = relic.getStatValue(stack, "invisible", "radius");
            System.out.println(radius);
            int particleCount = 40;

            for (int i = 0; i < particleCount; i++) {
                double angle = 2 * Math.PI * i / particleCount;
                double xOffset = radius * Math.cos(angle);
                double zOffset = radius * Math.sin(angle);

                Random random = new Random();

                level.addParticle(ParticleUtils.constructSimpleSpark(new Color(random.nextInt(50), random.nextInt(50), 50 + random.nextInt(55)), 0.25F, 500, 0.9f),
                        pos.x + xOffset, pos.y, pos.z + zOffset, 0, 0, 0);
            }
        }
    }
}