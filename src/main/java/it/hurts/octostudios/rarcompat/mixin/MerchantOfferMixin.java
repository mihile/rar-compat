package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.hat.VillagerHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.software.os.mac.MacInternetProtocolStats;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {

    @Inject(method = "getCostB", at = @At(value = "RETURN"), cancellable = true)
    private void setCostB(CallbackInfoReturnable<ItemStack> cir) {
//        Player player = Minecraft.getInstance().player;
//
//        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ModItems.VILLAGER_HAT.value());
//        if (relicStack == null || !(relicStack.getItem() instanceof VillagerHatItem hat)) return;
//        int discounted = (int) Math.floor(cir.getReturnValue().getCount() * (100 - hat.getStatValue(relicStack, "discount", "multiplier")) / 100);
//
//        cir.setReturnValue(new ItemStack(cir.getReturnValue().getItem(), Math.max(discounted, 1)));
    }
}