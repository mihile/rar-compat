package it.hurts.octostudios.rarcompat.items.belt;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.PredicateType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.data.WorldPosition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

public class ChorusTotemItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("past")
                                .active(CastData.builder().type(CastType.INSTANTANEOUS)
                                        .predicate("past", PredicateType.CAST, (player, stack) -> {

                                            WorldPosition lastPos = getWorldPos(stack, player);
                                            WorldPosition currentPos = new WorldPosition(player);

                                            double distance = Math.sqrt(Math.pow(currentPos.getPos().x - lastPos.getPos().x, 2) +
                                                    Math.pow(currentPos.getPos().y - lastPos.getPos().y, 2) +
                                                    Math.pow(currentPos.getPos().z - lastPos.getPos().z, 2));

                                            return distance > 5;
                                        })
                                        .build())
                                .stat(StatData.builder("capacity")
                                        .icon(StatIcons.CAPACITY)
                                        .initialValue(120D, 140D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, -0.4D)
                                        .formatValue(value -> MathUtils.round(value * 20, 0))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        Vec3 pos = getWorldPos(stack, player).getPos();

        player.teleportTo(pos.x, pos.y, pos.z);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        int tickCount = player.tickCount;

        if (canPlayerUseAbility(player, stack, "past"))
            stack.set(DataComponentRegistry.TOGGLED, false);

        if (tickCount % 5 == 0 && stack.getOrDefault(DataComponentRegistry.TOGGLED, true))
            setWorldPos(stack, new WorldPosition(player));
        else {
            if (tickCount % 20 == 0)
                addTime(stack, 1);

            if (getTime(stack) >= 3) {
                setWorldPos(stack, new WorldPosition(player));
                addTime(stack, -getTime(stack));

                stack.set(DataComponentRegistry.TOGGLED, true);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack.getItem() == stack.getItem())
            return;

        setWorldPos(stack, null);
    }

    public static void setWorldPos(ItemStack stack, WorldPosition worldPosition) {
        stack.set(DataComponentRegistry.WORLD_POSITION, worldPosition);
    }

    public static WorldPosition getWorldPos(ItemStack stack, Player player) {
        return stack.getOrDefault(DataComponentRegistry.WORLD_POSITION, new WorldPosition(player));
    }

    public static void addTime(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, getTime(stack) + val);
    }

    public static int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }
}
