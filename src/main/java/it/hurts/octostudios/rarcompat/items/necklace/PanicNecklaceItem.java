package it.hurts.octostudios.rarcompat.items.necklace;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class PanicNecklaceItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("panic")
                                .stat(StatData.builder("movement")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value * 10, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .icon(StatIcons.DISTANCE)
                                        .initialValue(6D, 8D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xffdc291b)
                                .borderBottom(0xff57000d)
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem() || !(slotContext.entity() instanceof Player player))
            return;

        EntityUtils.removeAttribute(player, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.level().isClientSide)
            return;

        double modifierMovementSpeed = getLengthRadius(player, player.level(), stack) * this.getStatValue(stack, "panic", "movement");

        EntityUtils.resetAttribute(player, stack, Attributes.MOVEMENT_SPEED, (float) modifierMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public int getLengthRadius(Player player, Level level, ItemStack stack) {
        Set<Monster> trackedMobs = new HashSet<>();

        for (Monster mob : level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(getStatValue(stack, "panic", "radius")), entity -> entity instanceof Mob))
            if (mob.getTarget() == player)
                trackedMobs.add(mob);

        trackedMobs.removeIf(mob -> mob.getTarget() != player || !mob.isAlive() || !mob.getBoundingBox()
                .intersects(player.getBoundingBox().inflate(getStatValue(stack, "panic", "radius"))));

        return trackedMobs.toArray().length;
    }

    @EventBusSubscriber
    public static class PanicNecklaceEvent {

        @SubscribeEvent
        public static void onPlayerDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.PANIC_NECKLACE.value());

            if (!(stack.getItem() instanceof PanicNecklaceItem relic))
                return;

            relic.spreadRelicExperience(player, stack, 1);
        }
    }
}