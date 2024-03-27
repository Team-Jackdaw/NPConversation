package com.jackdaw.npconversation.function;

import com.google.gson.Gson;
import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.group.Group;
import com.jackdaw.chatwithnpc.group.GroupManager;
import com.jackdaw.chatwithnpc.openaiapi.function.CustomFunction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class QueryNPCFunction extends CustomFunction {
    public QueryNPCFunction() {
        ArrayList<String> npcList = new ArrayList<>();
        GroupManager.getGroupList().stream().map(groupName -> Objects.requireNonNull(GroupManager.getGroup(groupName)).getMemberList()).forEach(npcList::addAll);
        description = "Get the information of a NPC.";
        properties = Map.of(
                "npcName", Map.of(
                        "type", "string",
                        "description", "The NPC to query",
                        "enum", npcList
                )
        );
        required = new String[]{"npcName"};
    }
    @Override
    public Map<String, String> execute(@NotNull ConversationHandler conversation, @NotNull Map<String, Object> args) {
        String npcName = (String) args.get("npcName");
        Group group = Objects.requireNonNull(GroupManager.getGroup(conversation.getNpc().getGroup()));
        if (!group.getMemberList().contains(npcName)) return Map.of("error", "You can only query the information of the NPC in the same group as you.");
        File workingDirectory = ChatWithNPCMod.workingDirectory.resolve("npc").toFile();
        File[] files = workingDirectory.listFiles();
        ArrayList<NPC> npcList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(".json")) {
                    try {
                        String json = new String(Files.readAllBytes(file.toPath()));
                        NPC npc = new Gson().fromJson(json, NPC.class);
                        npcList.add(npc);
                    } catch (IOException ignored) {}
                }
            }
        }
        NPC npc = npcList.stream().filter(n -> n.name.equals(npcName)).findFirst().orElse(null);
        if (npc == null) return Map.of("error", "NPC not found");
        return Map.of("instruction", npc.instructions);
    }

    public static class NPC {
        public String name;
        public String instructions;
    }
}
