package it.hurts.octostudios.rarcompat.items.body;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.entity.InvisibilityZoneEntity;
import it.hurts.octostudios.rarcompat.init.EntityRegistry;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;


public class ScarfOfInvisibilityItem extends WearableRelicItem {
    @Setter
    @Getter
    private boolean flagEffect = true;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("invisible")
                                .stat(StatData.builder("threshold")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(12D, 2D)
                                        .upgradeModifier(UpgradeOperation.ADD, -1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !flagEffect) return;

        player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        setFlagEffect(true);
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

            if (player.getSpeed() > relic.getStatValue(stack, "invisible", "threshold")) return;

            InvisibilityZoneEntity zone = new InvisibilityZoneEntity(EntityRegistry.INVISIBILITY_ZONE.get(), level);

            zone.setPlayerOwner(player);
            zone.setPos(player.getPosition(1));
            zone.setRadius(relic.getStatValue(stack, "invisible", "radius"));
            zone.setInvZoneUUID(zone.getUUID());

            InvisibilityZoneEntity.replaceZone(level, zone);
        }
    }
}