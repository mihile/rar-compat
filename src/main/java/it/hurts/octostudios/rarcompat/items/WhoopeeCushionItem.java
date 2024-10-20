package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.necklace.FlamePendantItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
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
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;

public class WhoopeeCushionItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("push")
                                .active(CastData.builder().type(CastType.INSTANTANEOUS)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(5D, 7D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.057)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("chance")
                                        .icon(StatIcons.CHANCE)
                                        .initialValue(0.2D, 0.4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.075)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    // @EventBusSubscriber
    public static class WhoopeeCushionEvent {

        @SubscribeEvent
        public static void onAttackPlayer(LivingIncomingDamageEvent event) {
            Entity attacker = event.getSource().getEntity();

            if (!(event.getEntity() instanceof Player player) || attacker == player)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.WHOOPEE_CUSHION.value());

            Level level = player.level();

            RandomSource random = player.getRandom();

            if (!(stack.getItem() instanceof WhoopeeCushionItem relic) || random.nextInt(1) > (relic.getStatValue(stack, "push", "chance"))
                    || level.isClientSide)
                return;

            double radius = 10.0;

            for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(radius))) {
                Vec3 direction = mob.position().subtract(player.position()).normalize();

                mob.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 1));

                mob.setDeltaMovement(direction.scale(2));
            }
        }
    }
}
