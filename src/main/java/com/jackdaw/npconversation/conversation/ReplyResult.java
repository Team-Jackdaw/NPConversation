package com.jackdaw.npconversation.conversation;

import com.jackdaw.chatwithnpc.AsyncTask;
import com.jackdaw.chatwithnpc.conversation.ConversationManager;
import com.jackdaw.npconversation.NPConversation;

public class ReplyResult implements AsyncTask.TaskResult {

    private final String message;
    private final NPCChat chat;

    public ReplyResult(String message, NPCChat chat) {
        this.message = message;
        this.chat = chat;
    }

    @Override
    public void execute() {
        if (!ConversationManager.conversationMap.containsValue(chat) || !ConversationManager.conversationMap.containsValue(chat.getNextChat())) {
            chat.setBothInterrupted(true);
        } else if (chat.getStartTime() + NPConversation.timeout < System.currentTimeMillis()) {
            chat.getNextChat().replyToEntity("I'm sorry, I have to go now.");
            chat.setBothInterrupted(true);
        } else {
            chat.getNextChat().replyToEntity(message);
        }
    }

    @Override
    public boolean isCallable() {
        return !chat.isInterrupted() && !chat.isTalking() && !chat.getNextChat().isTalking();
    }
}
