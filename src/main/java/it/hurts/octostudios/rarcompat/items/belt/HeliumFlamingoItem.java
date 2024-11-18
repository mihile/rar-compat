package it.hurts.octostudios.rarcompat.items.belt;

import artifacts.registry.ModItems;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import be.florens.expandability.api.forge.PlayerSwimEvent;

import static it.hurts.octostudios.rarcompat.items.belt.HeliumFlamingoItem.HeliumFlamingoEvent.addTime;
import static it.hurts.octostudios.rarcompat.items.belt.HeliumFlamingoItem.HeliumFlamingoEvent.getTime;

public class HeliumFlamingoItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("flying")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("time")
                                        .icon(StatIcons.DURATION)
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.DESERT)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (slotContext.entity().tickCount % 20 == 0)
            addTime(stack, 1);

        int timeWorked = (int) getStatValue(stack, "flying", "time");

        if (getTime(stack) > timeWorked) {
            player.setDeltaMovement(player.getDeltaMovement().x, -0.1, player.getKnownMovement().z);
            player.fallDistance = 0;
        }
    }

    @EventBusSubscriber
    public static class HeliumFlamingoEvent {

        @SubscribeEvent
        public static void onSwimAir(PlayerSwimEvent event) {
            Player player = event.getEntity();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.HELIUM_FLAMINGO.value());

            if (!(stack.getItem() instanceof HeliumFlamingoItem relic) || player.isInWater())
                return;

            int timeWorked = (int) relic.getStatValue(stack, "flying", "time");

            if (getTime(stack) > timeWorked && player.onGround()) {
                addTime(stack, -getTime(stack));
                return;
            }

            if (relic.isAbilityTicking(stack, "flying") && getTime(stack) <= timeWorked && player.isSprinting())
                event.setResult(EventResult.SUCCESS);
        }

        public static void addTime(ItemStack stack, int val) {
            stack.set(DataComponentRegistry.TIME, getTime(stack) + val);
        }

        public static int getTime(ItemStack stack) {
            return stack.getOrDefault(DataComponentRegistry.TIME, 0);
        }

    }
}