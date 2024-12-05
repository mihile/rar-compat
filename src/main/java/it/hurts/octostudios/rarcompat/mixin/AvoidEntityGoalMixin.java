package it.hurts.octostudios.rarcompat.mixin;

import artifacts.registry.ModItems;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvoidEntityGoal.class)
public class AvoidEntityGoalMixin<T extends LivingEntity> {
    @Final
    @Shadow
    protected PathfinderMob mob;

    @Final
    @Shadow
    protected PathNavigation pathNav;

    @Shadow
    protected T toAvoid;

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (mob instanceof Creeper && toAvoid instanceof Player player) {
            ItemStack itemStack = EntityUtils.findEquippedCurio(player, ModItems.KITTY_SLIPPERS.value());

            if (itemStack.getItem() == Items.AIR) {
                ci.cancel();

                pathNav.stop();

                toAvoid = null;

                mob.goalSelector.removeGoal((AvoidEntityGoal<?>) (Object) this);
            }
        }
    }
}
