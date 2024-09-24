package it.hurts.octostudios.rarcompat;


import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.network.NetworkHandler;
import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RARCompat.MODID)
public class RARCompat {
    public static final String MODID = "rarcompat";

    public RARCompat() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }


}