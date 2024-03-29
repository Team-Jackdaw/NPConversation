package com.jackdaw.npconversation.conversation;

import com.jackdaw.chatwithnpc.AsyncTask;
import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.SettingManager;
import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.chatwithnpc.openaiapi.Assistant;
import com.jackdaw.chatwithnpc.openaiapi.Threads;
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
    public void startConversation() {
        AsyncTask.call(() -> {
            try {
                if (!npc.hasAssistant()) Assistant.createAssistant(npc);
                else Assistant.modifyAssistant(npc);
                if(!npc.hasThreadId()) Threads.createThread(this);
            } catch (Exception e) {
                ChatWithNPCMod.LOGGER.error(e.getMessage());
            }
            return AsyncTask.nothingToDo();
        });
    }

    public void startChat() {
        AsyncTask.call(() -> {
            try {
                long expireTime = System.currentTimeMillis() + NPConversation.timeout;
                while (!npc.hasThreadId() && System.currentTimeMillis() < expireTime) {
                    Thread.sleep(100);
                }
                if (npc.hasThreadId()) return new StartResult("Let's talk about " + this.topic + "! You need to say in " + SettingManager.language + "!", this);
            } catch (Exception e) {
                ChatWithNPCMod.LOGGER.error(e.getMessage());
            }
            return AsyncTask.nothingToDo();
        });
    }

    public static class StartResult implements AsyncTask.TaskResult {
        public String message;
        public NPCChat chat;

        public StartResult(String message, NPCChat chat) {
            this.message = message;
            this.chat = chat;
        }
        @Override
        public void execute() {
            chat.replyToEntity(message);
        }

        @Override
        public boolean isCallable() {
            return chat.npc.hasThreadId();
        }
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
