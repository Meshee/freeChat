package cn.meshee.freechat.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.conversation.ConversationService;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.message.model.FcMessage;

public class ConversationManager extends Observable {

    private static ConversationManager instance;

    private volatile List<FreechatConversation> mConversation = new ArrayList<>();

    private volatile Map<String, FreechatConversation> mConversationMap = new HashMap<>(10);

    private Observer<Void> conversationChanges = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            refreshConversations();
        }
    };

    public Observer<List<FcMessage>> getMessageObserver() {
        return messageObserver;
    }

    private Observer<List<FcMessage>> messageObserver = new Observer<List<FcMessage>>() {

        @Override
        public void onEvent(List<FcMessage> fcMessages) {
            for (FcMessage message : fcMessages) {
                if (message.isReceived()) {
                    onNewReceivedFcMessage(message);
                }
            }
        }
    };

    private void onNewReceivedFcMessage(FcMessage message) {
        String convId = message.getConversationId();
        updateConversationUnreadCount(convId);
    }

    public FreechatConversation getFreechatConversation(String convId) {
        return mConversationMap.get(convId);
    }

    private void updateConversationUnreadCount(String convId) {
        FreechatConversation freechatConversation = getFreechatConversation(convId);
        if (freechatConversation != null) {
            boolean needNotify = freechatConversation.getUnreadMessageCount() <= 0;
            freechatConversation.increaseUnreadMessageCount();
        }
    }

    public void updateConversationTotalCount(String convId) {
        FreechatConversation freechatConversation = getFreechatConversation(convId);
        if (freechatConversation != null) {
            freechatConversation.increaseTotalMessageCount();
        }
    }

    public static ConversationManager getInstance() {
        if (instance == null) {
            instance = new ConversationManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        refreshConversations();
    }

    private boolean refreshConversations() {
        boolean changed = false;
        List<Conversation> conversations = FCClient.getService(ConversationService.class).queryAllConversations();
        if (conversations != null) {
            TVLog.log(String.format("SSSSSS refreshConversations and conversation size from conversationservice is %d, mConversationMap is %d", conversations.size(), mConversationMap.size()));
            for (Conversation conversation : conversations) {
                if (!mConversationMap.containsKey(conversation.getConversationId())) {
                    FreechatConversation freechatConversation = FreechatConversation.newFreechatConversation(conversation);
                    addFreechatConversation(conversation.getConversationId(), freechatConversation);
                    changed = true;
                }
            }
            for (Iterator<Map.Entry<String, FreechatConversation>> it = mConversationMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, FreechatConversation> entry = it.next();
                if (!isInList(entry.getKey(), conversations)) {
                    it.remove();
                    mConversation.remove(entry.getValue());
                    changed = true;
                }
            }
        } else {
            TVLog.log(String.format("SSSSSS refreshconversation get 0 conversation from conversationServices"));
        }
        TVLog.log(String.format("SSSSSS refreshconversation chagned is %s", changed ? "true" : "false"));
        return changed;
    }

    private boolean isInList(String key, List<Conversation> conversations) {
        for (Conversation conversation : conversations) {
            if (conversation.getConversationId().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean addFreechatConversation(String convId, FreechatConversation freechatConversation) {
        if (!mConversationMap.containsKey(convId)) {
            mConversationMap.put(convId, freechatConversation);
            mConversation.add(freechatConversation);
            return true;
        }
        return false;
    }

    public List<FreechatConversation> getConversations() {
        return mConversation;
    }

    public void destroy() {
        mConversation = null;
        mConversationMap = null;
        instance = null;
    }

    private Observer<Void> mConversationObserver = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            TVLog.log(String.format("SSSSSS receive conversation change event start"));
            boolean changed = refreshConversations();
            TVLog.log(String.format("SSSSSS receive conversation change event end"));
        }
    };

    public Observer<Void> getConversationObserver() {
        return mConversationObserver;
    }

    public void clearUnreadMessageFor(String convId) {
        FreechatConversation conversation = getFreechatConversation(convId);
        if (conversation != null) {
            conversation.clearUnreaeMessageCount();
        }
    }
}
