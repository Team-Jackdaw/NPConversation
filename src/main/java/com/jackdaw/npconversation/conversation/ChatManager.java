package com.jackdaw.npconversation.conversation;

import com.jackdaw.chatwithnpc.conversation.ConversationManager;
import com.jackdaw.chatwithnpc.group.Group;
import com.jackdaw.chatwithnpc.group.GroupManager;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.chatwithnpc.npc.NPCEntityManager;
import com.jackdaw.npconversation.NPConversation;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static com.jackdaw.chatwithnpc.SettingManager.range;

public class ChatManager {
    public static void registerNPCChat(@NotNull NPCEntity npc1) {
        if (ConversationManager.isConversing(npc1.getUUID())) return;
        NPCEntity npc2 = getClosestNPC(npc1);
        if (npc2 == null || ConversationManager.isConversing(npc2.getUUID())) return;
        if (!npc1.getGroup().equals(npc2.getGroup())) return;
        Group group = GroupManager.getGroup(npc1.getGroup());
        if (group == null) return;
        String topic = group.getEvent().isEmpty() ? null : group.getEvent().get(0);
        if (topic == null) return;
        NPCChat chat1 = new NPCChat(npc1, npc2, null, topic);
        ConversationManager.conversationMap.put(npc1.getUUID(), chat1);
        ConversationManager.conversationMap.put(npc2.getUUID(), chat1.nextChat);
        NPConversation.LOGGER.info("[NPConversation] Started a conversation between " + ConversationManager.getConversation(npc1.getUUID()).getNpc().getName() + " and " + ConversationManager.getConversation(npc2.getUUID()).getNpc().getName());
        chat1.startChat();
    }

    public static @Nullable NPCEntity getClosestNPC(@NotNull NPCEntity npc) {
        Entity npcEntity = npc.getEntity();
        List<Entity> entities = npcEntity.world.getEntitiesByClass(Entity.class, npcEntity.getBoundingBox().expand(range), entity -> entity.getCustomName() != null);
        entities.forEach(entity -> NPCEntityManager.registerNPCEntity(entity, false));
        List<UUID> entityUUIDs = entities.stream().map(Entity::getUuid).toList();
        List<NPCEntity> npcEntities = NPCEntityManager.npcMap.keySet().stream().filter(entityUUIDs::contains).map(NPCEntityManager.npcMap::get).toList();
        if (npcEntities.size() < 2) return null;
        return npcEntities.stream().sorted((npc1, npc2) -> {
            double distance1 = npc1.getEntity().getPos().distanceTo(npcEntity.getPos());
            double distance2 = npc2.getEntity().getPos().distanceTo(npcEntity.getPos());
            return Double.compare(distance1, distance2);
        }).toList().get(1);
    }
}
