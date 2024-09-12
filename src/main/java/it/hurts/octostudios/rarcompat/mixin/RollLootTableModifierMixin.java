package it.hurts.octostudios.rarcompat.mixin;

import artifacts.neoforge.loot.RollLootTableModifier;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RollLootTableModifier.class)
public class RollLootTableModifierMixin {
    @Final
    @Shadow
    private final ResourceKey<LootTable> lootTable = null;
    @Final
    @Shadow
    private final boolean replace = false;

    /**
     * @author SSKirillSS
     * @reason Temp solution til the end of development
     */
    @Overwrite
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (replace)
            generatedLoot.clear();

        context.getResolver().get(Registries.LOOT_TABLE, lootTable).ifPresent(
                table -> table.value().getRandomItemsRaw(context, stack -> {
                    if (!(stack.getItem() instanceof IRelicItem))
                        generatedLoot.add(stack);
                })
        );

        return generatedLoot;
    }
}