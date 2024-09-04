package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.hurts.octostudios.rarcompat.items.hat.VillagerHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    public VillagerMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "updateSpecialPrices", at = @At(value = "HEAD"))
    private void increaseReputation(Player player, CallbackInfo ci) {
        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ModItems.VILLAGER_HAT.value());
        if (relicStack == null || !(relicStack.getItem() instanceof VillagerHatItem hat) || offers == null) return;

        for (MerchantOffer offer : this.offers) {
            int discounted = (int) Math.floor(offer.getItemCostA().count() * hat.getStatValue(relicStack, "discount", "multiplier") / 100);

            offer.addToSpecialPriceDiff(-discounted);
        }

    }
}