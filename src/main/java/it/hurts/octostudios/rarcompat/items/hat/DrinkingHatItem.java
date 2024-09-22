//package it.hurts.octostudios.rarcompat.items.hat;
//
//import artifacts.registry.ModItems;
//import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
//import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
//import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
//import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
//import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
//import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
//import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
//import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
//import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
//import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
//import it.hurts.sskirillss.relics.utils.EntityUtils;
//import it.hurts.sskirillss.relics.utils.MathUtils;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.UseAnim;
//import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//public class DrinkingHatItem extends WearableRelicItem {
//    @Override
//    public RelicData constructDefaultRelicData() {
//        return RelicData.builder()
//                .abilities(AbilitiesData.builder()
//                        .ability(AbilityData.builder("drinking")
//                                .stat(StatData.builder("speed")
//                                        .initialValue(0.25D, 0.75D)
//                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.2D)
//                                        .formatValue(value -> MathUtils.round(value * 100, 1))
//                                        .build())
//                                .build())
//                        .ability(AbilityData.builder("nutrition")
//                                .requiredLevel(5)
//                                .stat(StatData.builder("hunger")
//                                        .initialValue(1D, 3D)
//                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.15D)
//                                        .formatValue(value -> MathUtils.round(value, 1))
//                                        .build())
//                                .build())
//                        .build())
//                .leveling(new LevelingData(100, 15, 100))
//                .loot(LootData.builder()
//                        .entry(LootCollections.ANTHROPOGENIC)
//                        .build())
//                .build();
//    }
//
//    @Override
//    public RelicAttributeModifier getRelicAttributeModifiers(ItemStack stack) {
//        return RelicAttributeModifier.builder()
//                .attribute(new RelicAttributeModifier.Modifier(.DRINKING_SPEED, (float) getStatQuality(stack, "drinking", "speed") - 1F))
//                .build();
//    }
//
//    @Mod.EventBusSubscriber
//    public static class DrinkingHatEvents {
//        @SubscribeEvent
//        public static void onUseItem(LivingEntityUseItemEvent.Finish event) {
//            if (!(event.getEntity() instanceof Player player) || player.getCommandSenderWorld().isClientSide())
//                return;
//
//            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.PLASTIC_DRINKING_HAT.get());
//
//            if (stack.isEmpty())
//                stack = EntityUtils.findEquippedCurio(player, ModItems.NOVELTY_DRINKING_HAT.get());
//
//            if (!(stack.getItem() instanceof DrinkingHatItem relic) || event.getItem().getUseAnimation() != UseAnim.DRINK)
//                return;
//
//            relic.addExperience(player, stack, (int) Math.ceil(event.getDuration() / 20F));
//
//            if (!relic.canUseAbility(stack, "nutrition"))
//                return;
//
//            int hunger = (int) relic.getStatQuality(stack, "nutrition", "hunger");
//            float saturation = hunger / 2F;
//
//            player.getFoodData().eat(hunger, saturation);
//        }
//    }
//}
