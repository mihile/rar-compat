package it.hurts.octostudios.rarcompat.mixin;

import artifacts.item.wearable.head.DrinkingHatItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(DrinkingHatItem.class)
public class DrinkingHatItemMixin {
    /**
     * @author Amiri163
     * @reason Temp solution til the end of development
     */
    @Overwrite(remap = false)
    public static int getDrinkingHatUseDuration(LivingEntity entity, UseAnim action, int duration) {
        return duration;
    }

}
