package cn.meshee.freechat.app;

import java.util.List;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.conversation.model.ConversationType;

public class FreechatConversation {

    private Conversation conversation;

    private int unreadMessageCount;

    private int totalMessageCount;

    public FreechatConversation(Conversation conversation, int unreadMessageCount, int totalMessageCount) {
        this.conversation = conversation;
        this.unreadMessageCount = unreadMessageCount;
        this.totalMessageCount = totalMessageCount;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public int increaseUnreadMessageCount() {
        return increaseUnreadMessageCount(1);
    }

    private int increaseUnreadMessageCount(int delta) {
        unreadMessageCount += delta;
        return unreadMessageCount;
    }

    public void clearUnreaeMessageCount() {
        unreadMessageCount = 0;
    }

    public int getTotalMessageCount() {
        return totalMessageCount;
    }

    public int increaseTotalMessageCount() {
        return increaseTotalMessageCount(1);
    }

    private int increaseTotalMessageCount(int delta) {
        totalMessageCount += delta;
        return totalMessageCount;
    }

    public void clearTotalMessageCount() {
        totalMessageCount = 0;
    }

    public static FreechatConversation newFreechatConversation(Conversation conversation) {
        return new FreechatConversation(conversation, 0, 0);
    }

    public ConversationType getConversationType() {
        return conversation.getConversationType();
    }

    public List<Contact> getParticipant() {
        return conversation.getParticipant();
    }

    public String getConverstionTitle() {
        return conversation.getConversationTitle();
    }

    public String getConverstionId() {
        return conversation.getConversationId();
    }
}
