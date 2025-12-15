package com.oldgods.cult.world;

import net.minecraft.text.Text;

/**
 * Represents the patron Old Gods players can align to. The enum captures basic lore
 * and cursed treasure hints used throughout the mod.
 */
public enum OldGod {
    FIMBULWINTER("Cube of Permafrost", "Shrouds the world in eternal cold and enables freezing rituals."),
    SURTUR("Sword of Finality", "Rules over fire and endings."),
    PLAGUE_QUEEN("Brood Emblem", "Twists nature with insect swarms."),
    BAT_LORD("Blood Chalice", "Rewards predatory combat and blood rituals."),
    ELDER_WITCH("Hexbound Effigy", "Fuels witchcraft and cauldrons."),
    KITSUNE("Kitsune Mask", "Plays with illusions, charm, and foxfire."),
    PHARA("Gilded Scarab", "Bends sand, storms, and ancient curses."),
    DARK_ONE("Corrupted Reliquary", "Consumes light and reshapes every other treasure.");

    private final String cursedTreasure;
    private final String summary;

    OldGod(String cursedTreasure, String summary) {
        this.cursedTreasure = cursedTreasure;
        this.summary = summary;
    }

    public String getCursedTreasure() {
        return cursedTreasure;
    }

    public String getSummary() {
        return summary;
    }

    public String getDisplayName() {
        return switch (this) {
            case FIMBULWINTER -> "Fimbulwinter";
            case SURTUR -> "Surtur";
            case PLAGUE_QUEEN -> "Plague Queen";
            case BAT_LORD -> "Bat Lord";
            case ELDER_WITCH -> "Elder Witch";
            case KITSUNE -> "The Kitsune";
            case PHARA -> "Phara";
            case DARK_ONE -> "The Dark One";
        };
    }

    public Text describe() {
        return Text.literal(getDisplayName() + " — Cursed Treasure: " + cursedTreasure + "\n" + summary);
    }
}
