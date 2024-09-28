package it.hurts.octostudios.rarcompat.items;

import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public interface IRelicArtifactsFortune {
    int getFortuneLevel(SlotContext slotContext, @Nullable LootContext lootContext);
}
