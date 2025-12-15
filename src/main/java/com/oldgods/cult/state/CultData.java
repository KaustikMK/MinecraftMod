package com.oldgods.cult.state;

import com.oldgods.cult.world.OldGod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CultData {
    private final UUID leader;
    private final Set<UUID> members = new HashSet<>();
    private final String name;
    private OldGod god;
    private int reputation;

    public CultData(UUID leader, String name, OldGod god) {
        this.leader = leader;
        this.name = name;
        this.god = god;
        this.members.add(leader);
    }

    public UUID leader() {
        return leader;
    }

    public Set<UUID> members() {
        return members;
    }

    public String name() {
        return name;
    }

    public OldGod god() {
        return god;
    }

    public int reputation() {
        return reputation;
    }

    public void setGod(OldGod god) {
        this.god = god;
    }

    public void addReputation(int amount) {
        this.reputation += amount;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("Leader", leader);
        nbt.putString("Name", name);
        nbt.putString("God", god.name());
        nbt.putInt("Reputation", reputation);

        NbtList memberList = new NbtList();
        for (UUID member : members) {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("Id", member);
            memberList.add(entry);
        }
        nbt.put("Members", memberList);
        return nbt;
    }

    public static CultData fromNbt(NbtCompound nbt) {
        UUID leader = nbt.getUuid("Leader");
        String name = nbt.getString("Name");
        OldGod god = OldGod.valueOf(nbt.getString("God"));
        CultData data = new CultData(leader, name, god);
        data.reputation = nbt.getInt("Reputation");

        NbtList members = nbt.getList("Members", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : members) {
            NbtCompound entry = (NbtCompound) element;
            data.members.add(entry.getUuid("Id"));
        }
        return data;
    }
}
