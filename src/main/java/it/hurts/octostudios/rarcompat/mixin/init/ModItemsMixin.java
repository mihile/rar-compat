package it.hurts.octostudios.rarcompat.mixin.init;

import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.belt.*;
import it.hurts.octostudios.rarcompat.items.hands.*;
import it.hurts.octostudios.rarcompat.items.necklace.CrossNecklaceItem;
import it.hurts.octostudios.rarcompat.items.hat.CowboyHatItem;
import it.hurts.octostudios.rarcompat.items.necklace.*;
import it.hurts.octostudios.rarcompat.items.hat.*;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ModItems.class)
public class ModItemsMixin {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lartifacts/registry/ModItems;wearableItem(Ljava/lang/String;Ljava/util/function/Consumer;)Lnet/minecraft/core/Holder;"))
    private static Holder<Item> redirectWearableItem(String name, Consumer<WearableArtifactItem.Builder> builderConsumer) {
        return switch (name) {
            case "plastic_drinking_hat" -> register(name, DrinkingHatItem::new);
            case "novelty_drinking_hat" -> register(name, DrinkingHatItem::new);
            case "snorkel" -> register(name, SnorkelItem::new);
            case "villager_hat" -> register(name, VillagerHatItem::new);
            case "superstitious_hat" -> register(name, SuperstitiousHatItem::new);
            case "anglers_hat" -> register(name, AnglersHatItem::new);
            case "lucky_scarf" -> register(name, LuckyScarfItem::new);
            case "scarf_of_invisibility" -> register(name, ScarfOfInvisibilityItem::new);
            case "flame_pendant" -> register(name, FlamePendantItem::new);
            case "shock_pendant" -> register(name, ShockPendantItem::new);
            case "thorn_pendant" -> register(name, ThornPendantItem::new);
            case "cowboy_hat" -> register(name, CowboyHatItem::new);
            case "universal_attractor" -> register(name, UniversalAttractorItem::new);
            case "crystal_heart" -> register(name, CrystalHeartItem::new);
            //   case "charm_of_sinking" -> register(name, CharmOfSinkingItem::new);
            case "cross_necklace" -> register(name, CrossNecklaceItem::new);
            case "night_vision_goggles" -> register(name, NightVisionGogglesItem::new);
            //  case "panic_necklace" -> register(name, PanicNecklaceItem::new);
            //    case "helium_flamingo" -> register(name, HeliumFlamingoItem::new);
            case "cloud_in_a_bottle" -> register(name, CloudInBottleItem::new);
            case "vampiric_glove" -> register(name, VampiricGloveItem::new);
            case "golden_hook" -> register(name, GoldenHookItem::new);
            case "onion_ring" -> register(name, OnionRingItem::new);
            case "digging_claws" -> register(name, DiggingClawsItem::new);
            //case "feral_claws" -> register(name, FeralClawsItem::new);
            case "antidote_vessel" -> register(name, AntidoteVesselItem::new);
            case "power_glove" -> register(name, PowerGloveItem::new);
            case "withered_bracelet" -> register(name, WitheredBraceletItem::new);
            default -> wearableItem(name, builderConsumer);
        };
    }

//    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lartifacts/registry/ModItems;register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraft/core/Holder;"))
//    private static Holder<Item> redirectRegister(String name, Supplier<? extends Item> supplier) {
//        return switch (name) {
//                case "umbrella" -> register(name, UmbrellaItem::new);
//            default -> register(name, supplier);
//        };
//    }

    @Shadow
    private static Holder<Item> wearableItem(String name, Consumer<WearableArtifactItem.Builder> consumer) {
        return null;
    }

    @Shadow
    private static Holder<Item> register(String name, Supplier<? extends Item> supplier) {
        return null;
    }
}