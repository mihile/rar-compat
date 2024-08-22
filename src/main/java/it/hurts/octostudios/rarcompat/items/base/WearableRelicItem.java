package it.hurts.octostudios.rarcompat.items.base;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.necklace.HolyLocketItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TOGGLED;

public abstract class WearableRelicItem extends RelicItem implements IRelicItem, ICurioItem {
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (newStack == stack || !(slotContext.entity() instanceof Player player)) return;
    }

    public boolean checkingConditionsItemPlayer(LivingEntity entity, Item findEquippedCurio) {

        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return false;
        }

        ItemStack stack = EntityUtils.findEquippedCurio(player, findEquippedCurio);

//        if (!(stack.getItem() instanceof relicItem) || stack.getOrDefault(TOGGLED, true))
//            return false;

        return false;
    }
}