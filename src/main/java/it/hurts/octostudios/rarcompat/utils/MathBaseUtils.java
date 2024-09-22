package it.hurts.octostudios.rarcompat.utils;

import net.minecraft.util.RandomSource;

public class MathBaseUtils {

    public static int multicast(RandomSource random, double chance, double chanceMultiplier) {
        return random.nextDouble() <= chance ? multicast(random, chance * chanceMultiplier, chanceMultiplier) + 1 : 0;
    }
}
