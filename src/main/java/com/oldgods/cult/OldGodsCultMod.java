package com.oldgods.cult;

import com.oldgods.cult.block.IdolBlock;
import com.oldgods.cult.command.CultCommands;
import com.oldgods.cult.item.CultBookItem;
import com.oldgods.cult.state.CultState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the Old Gods Cult mod. This file wires the high-level content:
 * the idol block, the cult founder book, and command hooks that interact with persistent cult data.
 */
public class OldGodsCultMod implements ModInitializer {
    public static final String MOD_ID = "oldgods";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Block IDOL_BLOCK = new IdolBlock(FabricBlockSettings.copyOf(Blocks.CHISELED_BOOKSHELF).luminance(state -> 8));
    public static final Item CULT_BOOK = new CultBookItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CULT_BOOK))
            .displayName(Text.translatable("itemGroup.oldgods"))
            .entries((displayContext, entries) -> {
                entries.add(CULT_BOOK);
                entries.add(IDOL_BLOCK);
            })
            .build();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Old Gods cult system");

        Registry.register(Registries.ITEM_GROUP, id("core"), ITEM_GROUP);
        Registry.register(Registries.BLOCK, id("idol"), IDOL_BLOCK);
        Registry.register(Registries.ITEM, id("idol"), new BlockItem(IDOL_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, id("cult_founder_book"), CULT_BOOK);

        CultCommands.register();

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (!world.isClient && world instanceof net.minecraft.server.world.ServerWorld serverWorld && state.getBlock() instanceof IdolBlock) {
                CultState stateManager = CultState.get(serverWorld);
                stateManager.handleIdolDestroyed(player, pos);
            }
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
