# Old Gods Cult (Fabric 1.20.6)

This early prototype lets players found cults devoted to one of the Old Gods. Drop an idol block to receive a **Cult Founder Book**, then consult it for guidance or use the `/cult` command suite to manage membership.

## Core loops
- **Idol** – place the block to register a cult site and receive the book. Destroying an idol from another cult grants reputation.
- **Cult Founder Book** – right-click to read your current cult summary.
- **Commands** – `/cult create <name> <god>`, `/cult join <leader-uuid>`, `/cult switch <god>`, and `/cult info` manage data until custom GUIs and rituals arrive.
- **Persistent data** – cult membership, idol coordinates, chosen gods, and reputation are saved to world data.

## Old Gods lineup
The enum in `com.oldgods.cult.world.OldGod` includes Fimbulwinter, Surtur, Plague Queen, Bat Lord, Elder Witch, The Kitsune, Phara, and The Dark One. Each lists its cursed treasure for future content hooks.

## Building
Use the included Gradle wrapper:

```sh
./gradlew build
```

A Fabric loader-compatible jar will be produced in `build/libs`.
