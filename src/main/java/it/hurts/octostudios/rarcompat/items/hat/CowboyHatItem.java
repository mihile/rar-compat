package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CowboyHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("cowboy")
                                .stat(StatData.builder("speed")
                                        .icon(StatIcons.SPEED)
                                        .initialValue(0.4D, 0.45D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.4D)
                                        .formatValue(value -> MathUtils.round(value, 1) * 100)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        Entity mountedEntity = player.getVehicle();

        if (!(mountedEntity instanceof LivingEntity beingMounted) || player.level().isClientSide)
            return;

        EntityUtils.applyAttribute(beingMounted, stack, Attributes.MOVEMENT_SPEED,
                (float) getStatValue(stack, "cowboy", "speed"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onEntityMount(EntityMountEvent event) {
            Entity mountedEntity = event.getEntityBeingMounted();
            if (!(event.getEntity() instanceof Player player)) return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

            if (event.isDismounting() && mountedEntity instanceof LivingEntity mount && stack.getItem() instanceof CowboyHatItem)
                EntityUtils.removeAttribute(mount, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        }

    }
}