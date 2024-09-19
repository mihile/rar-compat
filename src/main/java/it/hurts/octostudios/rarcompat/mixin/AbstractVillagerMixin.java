package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.items.hat.VillagerHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

    @Shadow
    private Player tradingPlayer;

    @Shadow
    protected MerchantOffers offers;

    @Inject(method = "notifyTrade ", at = @At(value = "HEAD"))
    private void notifyTrade(MerchantOffer p_35274_, CallbackInfo ci) {
        ItemStack relicStack = EntityUtils.findEquippedCurio(tradingPlayer, ModItems.VILLAGER_HAT.value());
        if (relicStack == null || !(relicStack.getItem() instanceof VillagerHatItem hat) || offers == null) return;

        int discounted = (int) Math.floor(p_35274_.getItemCostA().count() * hat.getStatValue(relicStack, "discount", "multiplier") / 100);

        if (discounted > 1)
            hat.addRelicExperience(relicStack, 1 + tradingPlayer.getRandom().nextInt(discounted) + 1);
    }

    @Inject(method = "getOffers", at = @At(value = "HEAD"))
    private void getOffers(CallbackInfoReturnable<MerchantOffers> cir) {
        ItemStack relicStack = EntityUtils.findEquippedCurio(tradingPlayer, ModItems.VILLAGER_HAT.value());
        if (relicStack == null || !(relicStack.getItem() instanceof VillagerHatItem hat) || offers == null) return;

        for (MerchantOffer offer : offers) {

            int discounted = (int) Math.floor(offer.getItemCostA().count() * hat.getStatValue(relicStack, "discount", "multiplier") / 100);
            offer.addToSpecialPriceDiff(-discounted);
        }
    }
}