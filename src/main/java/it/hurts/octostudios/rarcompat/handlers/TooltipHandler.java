package it.hurts.octostudios.rarcompat.handlers;

import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber
public class TooltipHandler {
    @SubscribeEvent
    public static void sub(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();

        if (!(itemStack.getItem() instanceof WearableRelicItem))
            return;

        var tooltip = event.getToolTip();

        tooltip.add(4, Component.translatable("tooltip.rar_compat").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));

    }
}
