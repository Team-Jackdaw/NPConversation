# NPC Conversation Mod

> The mod is still under development, and the current version is not stable. If you encounter any problems, please
> submit an issue.

## 1. Introduction

This is a mod based on chat-with-NPC who triggers conversations between neighboring NPCs of the same group and shows them to nearby players. The ability to discuss ideas between NPCs will be added in the future.

## 2. Dependencies

- [ChatWithNPC](https://npchat.jackdaw.wdr.im/) >= 0.0.3+build.7
- [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) *
- Minecraft Server 1.19.4 or higher
- Fabric Loader 0.12.0 or higher

## 3. Basic Functions Provided

This mod allows NPCs to chat with each other and gives NPCs an end conversation function, or the player can speak up and interrupt their conversation. Currently it is limited to chatting on one topic between nearby (10 frames) NPCs in the same group.

## 4. Usage

Currently, players can only manually hold shift and right-click on an NPC to trigger a discussion between neighboring NPCs of the same group about the first event of their group, and the triggering mechanism will be improved in the future.

## 5. Developer Notes

- [javadoc](https://npchat.doc.ussjackdaw.com)

The npchat-api dependency configuration is as follows:

```groovy
repositories {
    maven {
        name = "Team-Jackdaw"
        url = uri("https://maven.ussjackdaw.com/repository/maven-releases/")
    }
}

dependencies {
    modImplementation "com.jackdaw:chat-with-NPC:${project.chat_with_npc_version}"
}
```

You can start with fork the source code of this mod and write your own custom functions base on this structure to
enhance the interaction between NPCs and players in your game.
