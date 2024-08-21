package it.hurts.octostudios.rarcompat.handlers;

import artifacts.item.UmbrellaItem;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;

@EventBusSubscriber
public class UmbrellaEvent {

    @SubscribeEvent
    public static void onEntityHurt(AttackEntityEvent event) {
        LivingEntity player = event.getEntity();

        if (player.isUsingItem() && player.getUseItem().getItem() instanceof UmbrellaItem) {
            DamageSource source = event.getEntity().getLastDamageSource();
            Entity target = source.getEntity();

            if (target instanceof LivingEntity) {
                Vec3 kb = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ()).normalize()
                        .scale(((IRelicItem) player.getUseItem().getItem()).getStatValue(player.getUseItem(), "shield", "passive_knockback"));
                target.setDeltaMovement(target.getDeltaMovement().add(kb));
            }
        }
    }
}
