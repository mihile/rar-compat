package it.hurts.octostudios.rarcompat.items.bunch;

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
                                .stat(StatData.builder("modifier")
                                        .initialValue(2D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> MathUtils.round(value, 1) * 10)
                                        .build())
                                .stat(StatData.builder("radius")
                                        .initialValue(2D, 3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack == newStack || !(slotContext.entity() instanceof Player player)) return;

        removeAttribute(player, stack);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().
                isClientSide || !(slotContext.entity() instanceof Player player)) return;

        //впадлу писать пиздец потом допишу не забудь пж
        int modifier = getLengthRadius(player, player.level(), stack);

        double modifierSpeed = (float) ((modifier / 2) * (getStatValue(stack, "panic", "modifier") / 10));
        EntityUtils.applyAttribute(player, stack, Attributes.ATTACK_SPEED, 10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

//        if (modifier > 1) {
//            EntityUtils.applyAttribute(player, stack, Attributes.ATTACK_SPEED, 102, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
//            EntityUtils.applyAttribute(player, stack, Attributes.MOVEMENT_SPEED, 2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
//        } else
//            removeAttribute(player, stack);
    }

    public void removeAttribute(Player player, ItemStack stack) {
        EntityUtils.removeAttribute(player, stack, Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        EntityUtils.removeAttribute(player, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
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