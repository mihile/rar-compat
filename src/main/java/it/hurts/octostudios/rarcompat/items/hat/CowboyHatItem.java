package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.PredicateType;
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
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import top.theillusivec4.curios.api.SlotContext;

import static it.hurts.sskirillss.relics.utils.EntityUtils.rayTraceEntity;

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
                                .active(CastData.builder().type(CastType.INSTANTANEOUS)
                                        .predicate("overlord", PredicateType.CAST, (player, stack) -> rayTraceEntity(player, entity -> entity instanceof Mob && !(entity instanceof Saddleable)
                                                && !player.isPassenger(), player.getAttributes().getBaseValue(Attributes.ENTITY_INTERACTION_RANGE)) != null)
                                        .build())
                                .requiredLevel(5)
                                .stat(StatData.builder("time")
                                        .initialValue(2D, 5D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
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
                        .maxLevel(15)
                        .step(100)
                        .sources(LevelingSourcesData.builder()
                                .source(LevelingSourceData.abilityBuilder("cowboy")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.ORANGE)
                                        .build())
                                .source(LevelingSourceData.abilityBuilder("overlord")
                                        .initialValue(1)
                                        .gem(GemShape.SQUARE, GemColor.BLUE)
                                        .build())
                                .build())
                        .build())
                .loot(LootData.builder()
                        .entry(LootCollections.VILLAGE)
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (!ability.equals("overlord") || player.getCommandSenderWorld().isClientSide)
            return;

        EntityHitResult result = rayTraceEntity(player, entity -> entity instanceof Mob, player.getAttributes().getBaseValue(Attributes.ENTITY_INTERACTION_RANGE));

        if (result == null)
            return;

        spreadRelicExperience(player, stack, 1);

        player.startRiding(result.getEntity());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || player.getCommandSenderWorld().isClientSide())
            return;

        if (getTime(stack) >= getStatValue(stack, "overlord", "time")) {
            //if (player.getRootVehicle() instanceof Mob)
             //   player.stopRiding();

            setTime(stack, 0);
        }

        if (!(player.getRootVehicle() instanceof Mob beingMounted))
            return;

        if (player.tickCount % 20 == 0 && !(beingMounted instanceof Saddleable))
            addTime(stack, 1);

        if (!canPlayerUseAbility(player, stack, "cowboy") || (beingMounted instanceof Horse horse && !horse.isTamed()))
            return;

        changeAttributes(beingMounted, stack, true, Attributes.MOVEMENT_SPEED, Attributes.JUMP_STRENGTH, Attributes.SAFE_FALL_DISTANCE);

        if ((beingMounted.getKnownMovement().x != 0 || beingMounted.getKnownMovement().z != 0) && beingMounted.getRandom().nextFloat() < 0.25F
                && player.tickCount % 20 == 0)
            spreadRelicExperience(player, stack, 1);
    }

    @SafeVarargs
    private void changeAttributes(Mob beingMounted, ItemStack stack, boolean flag, Holder<Attribute>... attributeHolder) {
        for (Holder<Attribute> attributes : attributeHolder)
            if (flag)
                EntityUtils.applyAttribute(beingMounted, stack, attributes, (float) getStatValue(stack, "cowboy", "speed"),
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            else
                EntityUtils.removeAttribute(beingMounted, stack, attributes, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public void addTime(ItemStack stack, int val) {
        setTime(stack, getTime(stack) + val);
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.TIME, 0);
    }

    public void setTime(ItemStack stack, int val) {
        stack.set(DataComponentRegistry.TIME, Math.max(val, 0));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player) || newStack.getItem() == stack.getItem()
                || !(player.getRootVehicle() instanceof Mob mob))
            return;

        changeAttributes(mob, stack, false, Attributes.MOVEMENT_SPEED, Attributes.JUMP_STRENGTH, Attributes.SAFE_FALL_DISTANCE);

        player.stopRiding();
    }

    @EventBusSubscriber
    public static class CowboyEvent {
        @SubscribeEvent
        public static void onEntityMount(EntityMountEvent event) {
            if (!(event.getEntity() instanceof Player player))
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.COWBOY_HAT.value());

            if (event.isDismounting() && event.getEntityBeingMounted() instanceof Mob mount && stack.getItem() instanceof CowboyHatItem relic) {

              //  if (!(mount instanceof Saddleable))
                    //relic.addAbilityCooldown(stack, "overlord", 1200);

                relic.changeAttributes(mount, stack, false, Attributes.MOVEMENT_SPEED, Attributes.JUMP_STRENGTH, Attributes.SAFE_FALL_DISTANCE);
            }
        }
    }
}