package it.hurts.octostudios.rarcompat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionInstance.class)
public class SimpleOptionMixin<T> {
    @Shadow
    @Final
    public Component caption;

    @Shadow
    T value;

   // @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void setRealValue(T p_231515_, CallbackInfo ci) {
        if (caption.getString().equals(I18n.get("options.gamma"))) {
            this.value = p_231515_;
            ci.cancel();
        }
    }
}
