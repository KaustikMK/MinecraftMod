package com.oldgods.cult.state;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.oldgods.cult.OldGodsCultMod;
import com.oldgods.cult.world.OldGod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CultState extends PersistentState {
    private static final Type<CultState> TYPE = new Type<>(CultState::new, CultState::fromNbt, null);
    private static final String KEY = OldGodsCultMod.MOD_ID;
    private final Map<UUID, CultData> cults = new HashMap<>();
    private final BiMap<UUID, UUID> memberToCult = HashBiMap.create();
    private final Map<BlockPos, UUID> idolLocations = new HashMap<>();

    public static CultState get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(TYPE, KEY);
    }

    public static CultState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        CultState state = new CultState();
        NbtList cultList = nbt.getList("Cults", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : cultList) {
            CultData data = CultData.fromNbt((NbtCompound) element);
            state.cults.put(data.leader(), data);
            for (UUID member : data.members()) {
                state.memberToCult.put(member, data.leader());
            }
        }

        NbtList idols = nbt.getList("Idols", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : idols) {
            NbtCompound entry = (NbtCompound) element;
            BlockPos pos = new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z"));
            UUID leader = entry.getUuid("Leader");
            state.idolLocations.put(pos, leader);
        }
        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList cultList = new NbtList();
        for (CultData data : cults.values()) {
            cultList.add(data.toNbt());
        }
        nbt.put("Cults", cultList);

        NbtList idols = new NbtList();
        idolLocations.forEach((pos, leader) -> {
            NbtCompound entry = new NbtCompound();
            entry.putInt("x", pos.getX());
            entry.putInt("y", pos.getY());
            entry.putInt("z", pos.getZ());
            entry.putUuid("Leader", leader);
            idols.add(entry);
        });
        nbt.put("Idols", idols);
        return nbt;
    }

    public Optional<CultData> getCult(PlayerEntity player) {
        UUID id = memberToCult.get(player.getUuid());
        return Optional.ofNullable(id).map(cults::get);
    }

    public Text describePlayerCult(PlayerEntity player) {
        Optional<CultData> cult = getCult(player);
        if (cult.isPresent()) {
            CultData data = cult.get();
            return Text.literal("Cult: " + data.name() + " (" + data.god().getDisplayName() + ")\nMembers: " + data.members().size() + "\nReputation: " + data.reputation());
        }
        return Text.literal("You do not belong to a cult yet. Use the founder book while sneaking to create one or ask a leader to invite you via /cult.");
    }

    public Text createCult(PlayerEntity player, String name, OldGod god) {
        if (memberToCult.containsKey(player.getUuid())) {
            return Text.literal("You already belong to a cult.");
        }
        if (cults.values().stream().anyMatch(c -> c.name().equalsIgnoreCase(name))) {
            return Text.literal("A cult with that name already exists.");
        }
        CultData data = new CultData(player.getUuid(), name, god);
        cults.put(data.leader(), data);
        memberToCult.put(player.getUuid(), data.leader());
        markDirty();
        return Text.literal("Founded cult '" + name + "' in service of " + god.getDisplayName());
    }

    public Text joinCult(PlayerEntity requester, UUID leader) {
        CultData data = cults.get(leader);
        if (data == null) {
            return Text.literal("No cult was found for that leader.");
        }
        memberToCult.forcePut(requester.getUuid(), leader);
        data.members().add(requester.getUuid());
        markDirty();
        return Text.literal("Joined cult '" + data.name() + "'.");
    }

    public Text changeGod(PlayerEntity leader, OldGod god) {
        CultData data = cults.get(leader.getUuid());
        if (data == null) {
            return Text.literal("Only cult leaders can switch gods.");
        }
        data.setGod(god);
        markDirty();
        return Text.literal("Changed patron to " + god.getDisplayName());
    }

    public void addReputation(PlayerEntity leader, int value) {
        CultData data = cults.get(leader.getUuid());
        if (data != null) {
            data.addReputation(value);
            markDirty();
        }
    }

    public void registerIdol(BlockPos pos, LivingEntity placer) {
        if (placer instanceof PlayerEntity player) {
            idolLocations.put(pos.toImmutable(), player.getUuid());
            markDirty();
        }
    }

    public void handleIdolDestroyed(PlayerEntity player, BlockPos pos) {
        UUID leader = idolLocations.remove(pos);
        if (leader != null) {
            addReputation(player, 5);
            markDirty();
        }
    }
}
