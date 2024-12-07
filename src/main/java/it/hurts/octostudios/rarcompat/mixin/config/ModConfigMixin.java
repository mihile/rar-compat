package it.hurts.octostudios.rarcompat.mixin.config;

import artifacts.config.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ModConfig.class)
public class ModConfigMixin {

    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
    private void getNightVisionScale(CallbackInfo ci) {
        ci.cancel();
    }

}
