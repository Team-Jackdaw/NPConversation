package com.jackdaw.npconversation.mixin;

import com.jackdaw.chatwithnpc.conversation.ConversationHandler;
import com.jackdaw.chatwithnpc.conversation.ConversationManager;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.npconversation.conversation.NPCChat;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(NPCEntity.class)
public abstract class NPCReplyMessageMixin {

    @Shadow(remap = false) public abstract UUID getUUID();

    @Inject(method = "replyMessage", at = @At("RETURN"), remap = false)
    private void onReply(@NotNull String message, double range, CallbackInfo ci) {
        if (message.equals("...")) return;
        ConversationHandler conversation = ConversationManager.getConversation(getUUID());
        if (conversation == null) return;
        if (conversation instanceof NPCChat chat) {
            chat.waitAndReplyTOEntity(message);
        }
    }
}
