package it.hurts.octostudios.rarcompat.items.hat;

import it.hurts.octostudios.rarcompat.init.SoundRegistry;
import it.hurts.octostudios.rarcompat.items.WearableRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.misc.UpgradeOperation;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootData;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootCollections;
import it.hurts.sskirillss.relics.items.relics.base.data.misc.StatIcons;
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchData;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class NightVisionGogglesItem extends WearableRelicItem {

    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("vision")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .stat(StatData.builder("amount")
                                        .icon(StatIcons.MODIFIER)
                                        .initialValue(0.05D, 0.1)
                                        .upgradeModifier(UpgradeOperation.MULTIPLY_BASE, 0.9D)
                                        .formatValue(value -> MathUtils.round(value * 100D, 1))
                                        .build())
                                .research(ResearchData.builder()
                                        .star(0, 9, 26).star(1, 11, 13).star(2, 6, 7).star(3, 16, 7)
                                        .star(4, 2, 13).star(5, 6, 16).star(6, 20, 13).star(7, 16, 16)
                                        .link(0, 6).link(6, 3).link(3, 1).link(1, 2).link(2, 4).link(4, 5).link(4, 0).link(7, 6).link(7, 1).link(5, 1)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 100))
                .loot(LootData.builder()
                        .entry(LootCollections.SCULK)
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        float percent = (Math.abs(1 - (player.level().getMaxLocalRawBrightness(player.blockPosition()) / 15.0F)));

        if (isAbilityTicking(stack, "vision")) {
            if (player.getRandom().nextFloat() <= percent && player.tickCount % 100 == 0 && !(Math.abs(player.getKnownMovement().x) <= 0.01D || Math.abs(player.getKnownMovement().z) <= 0.01D)) {
                spreadRelicExperience(player, stack, 1);
            }
        }
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        if (ability.equals("vision") && player.level().isClientSide && stage == CastStage.START) {
            Random random = new Random();
            double min = 0.75;
            double max = 1.25;

            player.playSound(SoundRegistry.NIGHT_VISION_TOGGLE.get(), 1F, (float) (min + (max - min) * random.nextDouble()));
        }

    }

}