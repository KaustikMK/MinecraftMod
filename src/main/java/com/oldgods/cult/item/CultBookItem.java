package com.oldgods.cult.item;

import com.oldgods.cult.state.CultState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

/**
 * The Cult Founder Book is the player's UI into the cult system. For now it relays instructions and
 * runs the same logic as the commands to initialize or configure a cult.
 */
public class CultBookItem extends Item {
    public CultBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            CultState state = CultState.get(serverWorld);
            Text message = state.describePlayerCult(user);
            user.sendMessage(message, false);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
