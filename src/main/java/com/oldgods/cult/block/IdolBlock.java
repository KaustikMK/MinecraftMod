package com.oldgods.cult.block;

import com.oldgods.cult.OldGodsCultMod;
import com.oldgods.cult.state.CultState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Simple block used to anchor a cult. When placed, it grants the creator a Cult Founder Book.
 */
public class IdolBlock extends Block {
    public IdolBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            CultState cultState = CultState.get(serverWorld);
            cultState.registerIdol(pos, placer);

            if (placer instanceof PlayerEntity player) {
                ItemStack book = new ItemStack(OldGodsCultMod.CULT_BOOK);
                if (!player.giveItemStack(book)) {
                    player.dropItem(book, false);
                }
            }
        }
    }
}
