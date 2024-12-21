package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.items.feet.KittySlippersItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.*;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemColor;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.GemShape;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CowboyHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("cowboy")
                                .stat(StatData.builder("speed")
                                        .initialValue(0.2D, 0.3D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 5, 8).star(1, 7, 22).star(2, 16, 22).star(3, 19, 18).star(4, 12, 17)
                                        .link(4, 0).link(4, 1).link(4, 2).link(4, 3)
                                        .build())
                                .build())
                        .ability(AbilityData.builder("overlord")
                                .build())
                        .build())
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff572814)
                                .borderBottom(0xff473626)
                                .build())
                        .build())
                .leveling(LevelingData.builder()
                        .initialCost(100)
                        .maxLevel(10)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("cowboy")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.getCommandSenderWorld().isClientSide())
            return;

        if (!(player.getRootVehicle() instanceof Mob beingMounted) || !canPlayerUseAbility(player, stack, "cowboy"))
            return;

        if (beingMounted instanceof Horse horse && !horse.isTamed())
            return;

        this.tickRidden(beingMounted, player);
        beingMounted.travel(this.getRiddenInput(player));


        EntityUtils.applyAttribute(beingMounted, stack, Attributes.MOVEMENT_SPEED,
                (float) getStatValue(stack, "cowboy", "speed"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        EntityUtils.applyAttribute(beingMounted, stack, Attributes.JUMP_STRENGTH,
                (float) getStatValue(stack, "cowboy", "speed"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        EntityUtils.applyAttribute(beingMounted, stack, Attributes.SAFE_FALL_DISTANCE,
                (float) getStatValue(stack, "cowboy", "speed"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        if ((beingMounted.getKnownMovement().x != 0 || beingMounted.getKnownMovement().z != 0)
                && beingMounted.getRandom().nextFloat() > 0.5F && player.tickCount % 20 == 0)
            spreadRelicExperience(player, stack, 1);
    }

    protected Vec3 getRiddenInput(Player player) {
        float f = player.xxa * 0.5F;
        float f1 = player.zza;
        if (f1 <= 0.0F)
            f1 *= 0.25F;

        return new Vec3(f, 0.0, f1);
    }

    protected void tickRidden(Mob mob, Player player) {
        Vec2 vec2 = new Vec2(player.getXRot() * 0.5F, player.getYRot());

        mob.setXRot(vec2.x % 360.0F);
        mob.setYRot(vec2.y % 360.0F);
        mob.yRotO = mob.yBodyRot = mob.yHeadRot = mob.getYRot();
    }

    @EventBusSubscriber
    public static class CowboyEvent {
        @SubscribeEvent
        public static void onLivingChangeTargetEvent(LivingChangeTargetEvent event) {
            if (!(event.getNewAboutToBeSetTarget() instanceof Player player) || player.getVehicle() == null)
                return;

            ItemStack itemStack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

            if (!(itemStack.getItem() instanceof CowboyHatItem relic))
                return;

            //event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onEntityMount(EntityMountEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

            if (event.isDismounting() && event.getEntityBeingMounted() instanceof LivingEntity mount && stack.getItem() instanceof CowboyHatItem) {
                EntityUtils.removeAttribute(mount, stack, Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                EntityUtils.removeAttribute(mount, stack, Attributes.JUMP_STRENGTH, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
                EntityUtils.removeAttribute(mount, stack, Attributes.SAFE_FALL_DISTANCE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            }
        }

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
            Player player = event.getEntity();
            Entity target = event.getTarget();

            if (player instanceof ServerPlayer && target instanceof Mob && !player.isPassenger()) {
                player.startRiding(target, true);
            }
        }
    }
}