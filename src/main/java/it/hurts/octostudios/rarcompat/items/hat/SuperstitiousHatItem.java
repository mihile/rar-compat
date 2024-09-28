package it.hurts.octostudios.rarcompat.items.hat;

import artifacts.registry.ModItems;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.octostudios.rarcompat.utils.MathBaseUtils;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class SuperstitiousHatItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("looting")
                                .stat(StatData.builder("chance")
                                        .initialValue(0.1D, 0.2D)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.25)
                                        .formatValue(value -> MathUtils.round(value * 100, 1))
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.ANTHROPOGENIC)
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber
    public static class SuperstitiousHatEvent {

        @SubscribeEvent
        public static void onLootingLevelEvent(LootingLevelEvent event) {
            if (!(event.getDamageSource().getEntity() instanceof Player player)) return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ModItems.SUPERSTITIOUS_HAT.get());

            if (!(stack.getItem() instanceof SuperstitiousHatItem relic))
                return;

            var random = player.getRandom();
            var amount = MathBaseUtils.multicast(random, relic.getAbilityValue(stack, "looting", "chance"), 1F);

            event.setLootingLevel(amount);

            if (amount > 0)
                relic.addExperience(player, stack, random.nextInt(amount) + 1);
        }
    }
}