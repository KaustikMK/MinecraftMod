package com.oldgods.cult.item;

import com.oldgods.cult.state.CultState;
import com.oldgods.cult.state.CultData;
import com.oldgods.cult.world.OldGod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

/**
 * The Cult Founder Book is the player's UI into the cult system. For now it relays instructions and
 * runs the same logic as the commands to initialize or configure a cult.
 */
public class CultBookItem extends Item {
    private static final String SELECTED_GOD_KEY = "SelectedGod";

    public CultBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            CultState state = CultState.get(serverWorld);

            if (user.isSneaking()) {
                OldGod god = getSelectedGod(stack);
                String cultName = getCultName(user, stack);
                Text message = state.createCult(user, cultName, god);
                user.sendMessage(message, false);
                return TypedActionResult.success(stack, world.isClient());
            }

            Optional<CultData> cult = state.getCult(user);
            if (cult.isPresent()) {
                user.sendMessage(state.describePlayerCult(user), false);
            } else {
                OldGod god = cycleSelectedGod(stack);
                user.sendMessage(Text.literal("Selected patron: " + god.getDisplayName() + ". Sneak-use to found \"" + getCultName(user, stack) + "\"."), false);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    private String getCultName(PlayerEntity user, ItemStack stack) {
        if (stack.hasCustomName()) {
            return stack.getName().getString();
        }
        return user.getName().getString() + "'s Cult";
    }

    private OldGod getSelectedGod(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        String stored = nbt.getString(SELECTED_GOD_KEY);
        if (!stored.isEmpty()) {
            try {
                return OldGod.valueOf(stored);
            } catch (IllegalArgumentException ignored) {
                // fall through and reset below
            }
        }
        setSelectedGod(stack, OldGod.FIMBULWINTER);
        return OldGod.FIMBULWINTER;
    }

    private OldGod cycleSelectedGod(ItemStack stack) {
        OldGod[] gods = OldGod.values();
        OldGod current = getSelectedGod(stack);
        OldGod next = gods[(current.ordinal() + 1) % gods.length];
        setSelectedGod(stack, next);
        return next;
    }

    private void setSelectedGod(ItemStack stack, OldGod god) {
        stack.getOrCreateNbt().putString(SELECTED_GOD_KEY, god.name());
    }
}
