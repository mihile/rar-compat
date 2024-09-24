package it.hurts.octostudios.rarcompat;


import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.NetworkHandler;
import it.hurts.sskirillss.relics.init.DispenserBehaviorRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RARCompat.MODID)
public class RARCompat {
    public static final String MODID = "rarcompat";

    public RARCompat() {
        IEventBus bus = MinecraftForge.EVENT_BUS;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
        bus.addListener(this::fillCreativeTabs);
    }

    public static ObjectArrayList<ItemStack> doApplys(ObjectArrayList<ItemStack> generatedLoot, LootContext context, ResourceLocation lootTable, boolean replace) {
        if (lootTable == null)
            return generatedLoot;

        if (replace)
            generatedLoot.clear();

        context.getResolver().getLootTable(lootTable).getRandomItemsRaw(context, stack -> {
            if (!(stack.getItem() instanceof IRelicItem))
                generatedLoot.add(stack);
        });

        //   generatedLoot.removeIf(item -> item.getItem() instanceof IRelicItem);

        return generatedLoot;
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    public void fillCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == ModItems.CREATIVE_TAB.get()) {
            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                if (item instanceof WearableRelicItem)
                    event.accept(item);
            }
        }
    }
}