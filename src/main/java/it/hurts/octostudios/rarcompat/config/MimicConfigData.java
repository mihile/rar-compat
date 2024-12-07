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
            "relics:reflection_necklace", "relics:leafy_ring", "artifacts:snowshoes",
            "artifacts:cloud_in_a_bottle", "relics:jellyfish_necklace", "artifacts:vampiric_glove",
            "artifacts:digging_claws", "relics:chorus_inhibitor", "artifacts:thorn_pendant",
            "artifacts:feral_claws", "artifacts:steadfast_spikes", "relics:shadow_glaive",
            "relics:rage_glove", "artifacts:flame_pendant", "relics:holy_locket",
            "artifacts:superstitious_hat", "artifacts:warp_drive", "artifacts:charm_of_shrinking",
            "artifacts:plastic_drinking_hat", "relics:amphibian_boot", "relics:magic_mirror",
            "artifacts:charm_of_sinking", "artifacts:villager_hat", "relics:phantom_block",
            "artifacts:kitty_slippers", "relics:hunter_belt", "relics:enders_hand",
            "relics:solid_snowball", "artifacts:golden_hook", "artifacts:pickaxe_heater",
            "relics:researching_table", "artifacts:night_vision_goggles", "artifacts:anglers_hat",
            "relics:space_dissector", "artifacts:snorkel", "artifacts:helium_flamingo",
            "relics:bastion_ring", "artifacts:cross_necklace", "relics:drowned_belt",
            "relics:spore_sack", "artifacts:cowboy_hat", "relics:relic_experience_bottle",
            "relics:magma_walker", "artifacts:whoopee_cushion", "artifacts:umbrella",
            "artifacts:running_shoes", "relics:elytra_booster", "relics:wool_mitten",
            "relics:aqua_walker", "artifacts:shock_pendant", "artifacts:crystal_heart",
            "artifacts:obsidian_skull", "relics:roller_skates", "relics:infinity_ham",
            "relics:ice_skates", "relics:leather_belt", "artifacts:antidote_vessel",
            "artifacts:universal_attractor", "relics:midnight_robe", "artifacts:power_glove",
            "artifacts:panic_necklace", "relics:phantom_boot", "artifacts:onion_ring",
            "artifacts:bunny_hoppers", "artifacts:rooted_boots", "artifacts:fire_gauntlet",
            "artifacts:novelty_drinking_hat", "relics:blazing_flask", "artifacts:chorus_totem",
            "artifacts:withered_bracelet", "artifacts:flippers", "relics:ice_breaker",
            "artifacts:lucky_scarf", "artifacts:pocket_piston", "artifacts:scarf_of_invisibility",
            "relics:reflection_necklace", "relics:leafy_ring", "artifacts:snowshoes",
            "artifacts:cloud_in_a_bottle", "relics:jellyfish_necklace", "artifacts:vampiric_glove",
            "artifacts:digging_claws", "relics:chorus_inhibitor", "artifacts:thorn_pendant",
            "artifacts:feral_claws", "artifacts:steadfast_spikes", "relics:shadow_glaive",
            "relics:rage_glove", "artifacts:flame_pendant", "relics:holy_locket",
            "artifacts:superstitious_hat", "artifacts:warp_drive", "artifacts:charm_of_shrinking",
            "artifacts:plastic_drinking_hat", "relics:amphibian_boot", "relics:magic_mirror",
            "artifacts:charm_of_sinking", "artifacts:villager_hat", "relics:phantom_block",
            "artifacts:kitty_slippers", "relics:hunter_belt", "relics:enders_hand",
            "relics:solid_snowball", "artifacts:golden_hook", "artifacts:pickaxe_heater",
            "relics:researching_table", "artifacts:night_vision_goggles", "artifacts:anglers_hat",
            "relics:space_dissector", "artifacts:snorkel", "artifacts:helium_flamingo",
            "relics:bastion_ring", "artifacts:cross_necklace", "relics:drowned_belt",
            "relics:spore_sack", "artifacts:cowboy_hat", "relics:relic_experience_bottle",
            "relics:magma_walker", "artifacts:whoopee_cushion", "artifacts:umbrella",
            "artifacts:running_shoes", "relics:elytra_booster", "relics:wool_mitten",
            "relics:aqua_walker", "artifacts:shock_pendant", "artifacts:crystal_heart",
            "artifacts:obsidian_skull", "relics:roller_skates", "relics:infinity_ham",
            "relics:ice_skates", "relics:leather_belt", "artifacts:antidote_vessel",
            "artifacts:universal_attractor", "relics:midnight_robe", "artifacts:power_glove",
            "artifacts:panic_necklace", "relics:phantom_boot", "artifacts:onion_ring",
            "artifacts:bunny_hoppers", "artifacts:rooted_boots", "artifacts:fire_gauntlet",
            "artifacts:novelty_drinking_hat", "relics:blazing_flask", "artifacts:chorus_totem",
            "artifacts:withered_bracelet", "artifacts:flippers", "relics:ice_breaker",
            "artifacts:lucky_scarf", "artifacts:pocket_piston", "artifacts:scarf_of_invisibility"
    );

    @Override
    public void onLoadObject(Object object) {
        var data = (MimicConfigData) object;
        var entries = data.getItems();

        MimicHandler.ITEMS.clear();

        if (entries.isEmpty())
            return;

        for (var entry : entries) {
            var id = ResourceLocation.parse(entry);

            if (!BuiltInRegistries.ITEM.containsKey(id))
                continue;

            MimicHandler.ITEMS.add(BuiltInRegistries.ITEM.get(id));
        }
    }
}