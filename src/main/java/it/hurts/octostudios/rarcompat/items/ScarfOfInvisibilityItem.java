package it.hurts.octostudios.rarcompat.items;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.entity.InvisibilityZoneEntity;
import it.hurts.octostudios.rarcompat.init.EntityRegistry;
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
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;


public class ScarfOfInvisibilityItem extends WearableRelicItem {
    @Setter
    @Getter
    private Entity posEntity;

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
                                        .initialValue(1D, 2D)
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

        if (getPosEntity() == null || player.distanceTo(posEntity) > getStatValue(stack, "invisible", "radius"))
            player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
    }

    @EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onRight(PlayerInteractEvent.RightClickBlock event) {
            interaction(event.getEntity());
        }

        @SubscribeEvent
        public static void onLeft(PlayerInteractEvent.LeftClickBlock event) {
            interaction(event.getEntity());
        }

        private static void interaction(Player player) {
            Level level = player.level();
            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SCARF_OF_INVISIBILITY.value());

            if (!(stack.getItem() instanceof ScarfOfInvisibilityItem relic) || !player.hasEffect(EffectRegistry.VANISHING) || level.isClientSide)
                return;

            InvisibilityZoneEntity zone = new InvisibilityZoneEntity(EntityRegistry.INVISIBILITY_ZONE.get(), level);
            relic.setPosEntity(zone);
            zone.setPos(player.getPosition(1));
            zone.setRadius(relic.getStatValue(stack, "invisible", "radius"));

            zone.replaceZone(level, zone);

        }
    }
}