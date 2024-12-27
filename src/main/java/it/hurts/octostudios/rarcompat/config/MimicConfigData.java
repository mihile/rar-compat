package it.hurts.octostudios.rarcompat.config;

import it.hurts.octostudios.octolib.modules.config.annotations.Prop;
import it.hurts.octostudios.octolib.modules.config.impl.OctoConfig;
import it.hurts.octostudios.rarcompat.handlers.MimicHandler;
import lombok.Data;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

@Data
public class MimicConfigData implements OctoConfig {
    @Prop(comment = "List of items IDs for the mimic's loot drop.")
    private List<String> items = Arrays.asList(
            "relics:reflection_necklace", "relics:magma_walker", "relics:aqua_walker",
            "relics:midnight_robe", "relics:drowned_belt", "relics:jellyfish_necklace",
            "relics:hunter_belt", "relics:rage_glove", "relics:ice_skates",
            "relics:bastion_ring", "relics:chorus_inhibitor", "relics:space_dissector",
            "relics:holy_locket", "relics:enders_hand", "relics:elytra_booster",
            "relics:magic_mirror", "relics:ice_breaker", "relics:blazing_flask",
            "relics:spore_sack", "relics:shadow_glaive", "relics:roller_skates",
            "relics:infinity_ham", "relics:leather_belt", "relics:wool_mitten",
            "relics:amphibian_boot", "relics:leafy_ring", "relics:phantom_boot",
            "relics:springy_boot", "artifacts:umbrella", "artifacts:plastic_drinking_hat",
            "artifacts:novelty_drinking_hat", "artifacts:snorkel", "artifacts:night_vision_goggles",
            "artifacts:villager_hat", "artifacts:superstitious_hat", "artifacts:cowboy_hat",
            "artifacts:anglers_hat", "artifacts:lucky_scarf", "artifacts:scarf_of_invisibility",
            "artifacts:cross_necklace", "artifacts:panic_necklace", "artifacts:shock_pendant",
            "artifacts:flame_pendant", "artifacts:thorn_pendant", "artifacts:charm_of_sinking",
            "artifacts:charm_of_shrinking", "artifacts:cloud_in_a_bottle", "artifacts:obsidian_skull",
            "artifacts:antidote_vessel", "artifacts:universal_attractor", "artifacts:crystal_heart",
            "artifacts:helium_flamingo", "artifacts:chorus_totem", "artifacts:warp_drive",
            "artifacts:digging_claws", "artifacts:feral_claws", "artifacts:power_glove",
            "artifacts:fire_gauntlet", "artifacts:pocket_piston", "artifacts:vampiric_glove",
            "artifacts:golden_hook", "artifacts:onion_ring", "artifacts:pickaxe_heater",
            "artifacts:withered_bracelet", "artifacts:bunny_hoppers", "artifacts:kitty_slippers",
            "artifacts:running_shoes", "artifacts:snowshoes", "artifacts:steadfast_spikes",
            "artifacts:flippers", "artifacts:rooted_boots", "artifacts:whoopee_cushion"
    );

    @Override
    public void onLoadObject(Object object) {
        MimicHandler.readFromConfig((MimicConfigData) object);
    }
}