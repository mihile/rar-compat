package it.hurts.octostudios.rarcompat.items;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public interface IRelicArtifact {
    default int getFortuneLevel(ItemStack stack, SlotContext slotContext, @Nullable LootContext lootContext) {
        return 0;
    }

    default int getLootingLevel(ItemStack stack, SlotContext slotContext, DamageSource source, LivingEntity target, int baseLooting) {
        return 0;
    }
}
