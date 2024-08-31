package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import artifacts.registry.RegistryHolder;
import com.mojang.serialization.MapCodec;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.components.DataComponent;
import it.hurts.sskirillss.relics.init.DataComponentRegistry;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SuperstitiousHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("looting")
                                .stat(StatData.builder("chance")
                                        .initialValue(20D, 90D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 10D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .build();
    }

    @EventBusSubscriber
    public static class Event {

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)) return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SUPERSTITIOUS_HAT.value());

            if (!(stack.getItem() instanceof SuperstitiousHatItem relic))
                return;

            double lootingLevel = player.getMainHandItem().getOrDefault(DataComponents.ENCHANTMENTS,
                    ItemEnchantments.EMPTY).getLevel(player.getCommandSenderWorld().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING));

            for (ItemEntity itemEntity : event.getDrops())
                itemEntity.getItem().grow(MathUtils.multicast(player.level().getRandom(), relic.getStatValue(stack, "looting", "chance"), 0.4)
                        * lootingLevel > 0 ? (int) Math.max(1.5, lootingLevel / 1.5) : 1);

        }

    }
}