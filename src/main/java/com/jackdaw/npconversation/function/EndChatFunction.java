package com.jackdaw.npconversation.function;

import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.openaiapi.function.CustomFunction;
import com.jackdaw.npconversation.conversation.NPCChat;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EndChatFunction extends CustomFunction {
    public EndChatFunction() {
        description = "Use this function to end this conversation.";
        properties = Map.of();
    }
    @Override
    public Map<String, String> execute(@NotNull ConversationHandler conversation, @NotNull Map<String, Object> args) {
        if (conversation instanceof NPCChat chat) {
            chat.setBothInterrupted(true);
        }
        return Map.of("status", "success");
    }
}
