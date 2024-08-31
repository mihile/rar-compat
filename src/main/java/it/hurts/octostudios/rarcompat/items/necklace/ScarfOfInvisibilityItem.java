package it.hurts.octostudios.rarcompat.items.necklace;

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
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;


public class ScarfOfInvisibilityItem extends WearableRelicItem {
    // TODO: Move from Item to ItemStack as a DataComponent
    @Setter
    @Getter
    private boolean flagEffect = true;

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("invisible")
                                .stat(StatData.builder("threshold")
                                        .icon(StatIcons.SPEED)
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.035D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(8D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.05D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || !flagEffect
                || player.getSpeed() > getStatValue(stack, "invisible", "threshold")) return;

        player.displayClientMessage(Component.literal(String.valueOf(player.getSpeed())), true);
        player.addEffect(new MobEffectInstance(EffectRegistry.VANISHING, 5, 0, false, false));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);

        setFlagEffect(true);
    }

    @EventBusSubscriber
    public static class Events {
        // TODO: Use generic RMB/LMB events, then send packet to server to change the ItemStack flag and create area
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

            // TODO: Use static area drawing from item update method instead of custom entity
            InvisibilityZoneEntity zone = new InvisibilityZoneEntity(EntityRegistry.INVISIBILITY_ZONE.get(), level);

            zone.setPlayerOwner(player);
            zone.setPos(player.getPosition(1));
            zone.setRadius(relic.getStatValue(stack, "invisible", "radius"));
            zone.setInvZoneUUID(zone.getUUID());

            InvisibilityZoneEntity.replaceZone(level, zone);
        }
    }
}