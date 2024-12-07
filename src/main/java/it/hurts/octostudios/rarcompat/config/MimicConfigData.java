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
            "artifacts:plastic_drinking_hat", "artifacts:novelty_drinking_hat", "artifacts:snorkel",
            "artifacts:night_vision_goggles", "artifacts:villager_hat", "artifacts:superstitious_hat",
            "artifacts:cowboy_hat", "artifacts:anglers_hat", "artifacts:lucky_scarf",
            "artifacts:scarf_of_invisibility", "artifacts:cross_necklace", "artifacts:panic_necklace",
            "artifacts:shock_pendant", "artifacts:flame_pendant", "artifacts:thorn_pendant",
            "artifacts:charm_of_sinking", "artifacts:charm_of_shrinking", "artifacts:cloud_in_a_bottle",
            "artifacts:obsidian_skull", "artifacts:antidote_vessel", "artifacts:universal_attractor",
            "artifacts:crystal_heart", "artifacts:helium_flamingo", "artifacts:chorus_totem",
            "artifacts:warp_drive", "artifacts:digging_claws", "artifacts:feral_claws",
            "artifacts:power_glove", "artifacts:fire_gauntlet", "artifacts:pocket_piston",
            "artifacts:vampiric_glove", "artifacts:golden_hook", "artifacts:onion_ring",
            "artifacts:pickaxe_heater", "artifacts:withered_bracelet", "artifacts:aqua_dashers",
            "artifacts:bunny_hoppers", "artifacts:kitty_slippers", "artifacts:running_shoes",
            "artifacts:steadfast_spikes", "artifacts:flippers", "artifacts:rooted_boots",
            "artifacts:strider_shoes", "artifacts:whoopee_cushion"
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