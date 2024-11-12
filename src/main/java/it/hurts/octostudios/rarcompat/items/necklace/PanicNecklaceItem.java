package it.hurts.octostudios.rarcompat.items.necklace;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
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
                                .stat(StatData.builder("attack")
                                        .initialValue(5D, 4D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.05D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("movement")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(6D, 8D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
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
        if (slotContext.entity().level().isClientSide || !(slotContext.entity() instanceof Player player))
            return;

        int countMob = getLengthRadius(player, player.level(), stack);

        float modifierMovementSpeed = (float) this.getStatValue(stack, "panic", "movement") / 10 * countMob;
        float modifierAttackSpeed = (float) this.getStatValue(stack, "panic", "attack") / 10 * countMob;

        EntityUtils.resetAttribute(player, stack, Attributes.MOVEMENT_SPEED, modifierMovementSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public int getLengthRadius(Player player, Level level, ItemStack stack) {
        Set<Mob> trackedMobs = new HashSet<>();

        for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(getStatValue(stack, "panic", "radius")), entity -> entity instanceof Mob))
            if (mob.getTarget() == player)
                trackedMobs.add(mob);

        trackedMobs.removeIf(mob -> mob.getTarget() != player || !mob.isAlive() || !mob.getBoundingBox().intersects(player.getBoundingBox().inflate(getStatValue(stack, "panic", "radius"))));

        return trackedMobs.toArray().length;
    }
}