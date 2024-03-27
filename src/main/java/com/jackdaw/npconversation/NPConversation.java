package com.jackdaw.npconversation;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.conversation.ConversationManager;
import com.jackdaw.chatwithnpc.listener.PlayerSendMessageCallback;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.chatwithnpc.npc.NPCEntityManager;
import com.jackdaw.chatwithnpc.openaiapi.function.FunctionManager;
import com.jackdaw.npconversation.conversation.ChatManager;
import com.jackdaw.npconversation.conversation.NPCChat;
import com.jackdaw.npconversation.function.EndChatFunction;
import com.jackdaw.npconversation.function.QueryNPCFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;

public class NPConversation implements ModInitializer {
    public static final Logger LOGGER = ChatWithNPCMod.LOGGER;
    public static long waitTime = 10000L;
    public static long timeout = 300000L;

    @Override
    public void onInitialize() {
        FunctionManager.registerFunction("end_chat", new EndChatFunction());
        FunctionManager.registerFunction("query_npc", new QueryNPCFunction());
        // initialize a new NPC Chat
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity.getCustomName() == null) return ActionResult.PASS;
            if (!player.isSneaking()) return ActionResult.PASS;
            NPCEntityManager.registerNPCEntity(entity, false);
            NPCEntity npc1 = NPCEntityManager.getNPCEntity(entity.getUuid());
            if (npc1 == null) return ActionResult.PASS;
            ChatManager.registerNPCChat(npc1);
            return ActionResult.FAIL;
        });
        // interrupt the NPC conversation if the player is talking to the NPC
        PlayerSendMessageCallback.EVENT.register((player, message) -> {
            ConversationHandler conversationHandler = ConversationManager.getConversation(player);
            if (conversationHandler == null) return ActionResult.PASS;
            if (conversationHandler instanceof NPCChat chat) chat.setBothInterrupted(true);
            return ActionResult.PASS;
        });
    }
}
