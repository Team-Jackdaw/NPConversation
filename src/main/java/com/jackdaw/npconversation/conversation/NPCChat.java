package com.jackdaw.npconversation.conversation;

import com.jackdaw.chatwithnpc.AsyncTask;
import com.jackdaw.chatwithnpc.SettingManager;
import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.npconversation.NPConversation;
import org.jetbrains.annotations.NotNull;

public class NPCChat extends ConversationHandler {
    protected String topic;
    protected NPCChat nextChat;
    protected boolean interrupted = false;
    protected long startTime = System.currentTimeMillis();

    public NPCChat(@NotNull NPCEntity npc, NPCEntity npcNext, NPCChat chat, String topic) {
        super(npc);
        npc.addFunction("end_chat");
        npc.addFunction("query_npc");
        this.topic = topic;
        this.nextChat = chat == null ? new NPCChat(npcNext, null, this, topic) : chat;
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public void startConversation() {}

    public void startChat() {
        replyToEntity("Let's talk about " + this.topic + "! You need to say in " + SettingManager.language + "!");
    }

    public void waitAndReplyTOEntity(@NotNull String message) {
        if (!this.isInterrupted()) {
            AsyncTask.call(() -> {
                try {
                    Thread.sleep(NPConversation.waitTime);
                    return new ReplyResult(message, this);
                } catch (InterruptedException e) {
                    NPConversation.LOGGER.error("[NPConversation] Failed to wait for the NPC to reply", e);
                }
                return AsyncTask.nothingToDo();
            });
        }
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setBothInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
        this.getNextChat().interrupted = interrupted;
    }

    public NPCChat getNextChat() {
        return nextChat;
    }

    public long getStartTime() {
        return startTime;
    }
}
