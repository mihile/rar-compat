package it.hurts.octostudios.rarcompat.items;

import artifacts.entity.MimicEntity;
import artifacts.registry.ModEntityTypes;
import it.hurts.sskirillss.relics.init.CreativeTabRegistry;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.awt.*;
import java.util.List;

public class MimiDustItem extends Item implements ICreativeTabContent {

    public MimiDustItem() {
        super(new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();

        if (level.isClientSide())
            return InteractionResult.FAIL;

        var pos = context.getClickedPos();

        if (level.getBlockState(pos).getBlock() != Blocks.CHEST || !(level.getBlockEntity(pos) instanceof ChestBlockEntity chest))
            return InteractionResult.FAIL;

        var mimic = new MimicEntity(ModEntityTypes.MIMIC.value(), level);
        var persistentData = mimic.getPersistentData();

        for (int i = 0; i < chest.getContainerSize(); i++)
            if (chest.getItem(i).getItem() instanceof IRelicItem) {
                chest.setItem(i, ItemStack.EMPTY);

                persistentData.putInt("relicCount", persistentData.getInt("relicCount") + 1);
            }

        if (persistentData.getInt("relicCount") < 1)
            return InteractionResult.FAIL;

        level.removeBlock(pos, true);

        mimic.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        context.getItemInHand().shrink(1);

        level.addFreshEntity(mimic);

        var center = pos.getCenter();
        var random = mimic.getRandom();

        ((ServerLevel) level).sendParticles(ParticleUtils.constructSimpleSpark(new Color(255, 223 + random.nextInt(33), random.nextInt(50), 255),
                1F, 60, 0.85F), center.x(), center.y(), center.z(), 25, 0.3, 0.3, 0.3, 0.05);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void gatherCreativeTabContent(CreativeContentConstructor constructor) {
        constructor.entry(CreativeTabRegistry.RELICS_TAB.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this.getDefaultInstance());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.rarcompat.mimi_dust"));
    }
}
