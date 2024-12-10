package it.hurts.octostudios.rarcompat.mixin.config;

import artifacts.config.ConfigManager;
import artifacts.config.value.Value;
import artifacts.config.value.type.NumberValueType;
import artifacts.config.value.type.ValueType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ConfigManager.class)
public class ConfigManagerMixin<T, C> {

    @Inject(method = "readValuesFromConfig", at = @At("HEAD"), cancellable = true)
    public void readValuesFromConfig(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
    private void setCi(CallbackInfo ci) {
        ci.cancel();
    }
}
