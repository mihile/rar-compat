package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.hurts.octostudios.rarcompat.items.VillagerHatItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
public abstract class VillagerMixin {

    @ModifyExpressionValue(method = "updateSpecialPrices", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;getPlayerReputation(Lnet/minecraft/world/entity/player/Player;)I"))
    private int increaseReputation(int original, Player player) {
        ItemStack relicStack = EntityUtils.findEquippedCurio(player, ModItems.VILLAGER_HAT.value());
        if (relicStack != null && relicStack.getItem() instanceof VillagerHatItem hat) {
            double discount = hat.getStatValue(relicStack, "villager_discount", "discount");
            return (int) discount;
        }
        return original;
    }
}