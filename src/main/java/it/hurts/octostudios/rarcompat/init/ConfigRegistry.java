package it.hurts.octostudios.rarcompat.init;

import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.rarcompat.RARCompat;
import it.hurts.octostudios.rarcompat.config.MimicConfigData;

public class ConfigRegistry {
    public static MimicConfigData MIMIC_CONFIG = new MimicConfigData();

    public static void register() {
        ConfigManager.registerConfig(RARCompat.MODID + "/mimic", MIMIC_CONFIG);
    }
}