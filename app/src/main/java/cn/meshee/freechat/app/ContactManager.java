package cn.meshee.freechat.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.avatar.AvatarService;
import cn.meshee.fclib.api.contact.ContactService;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.log.TVLog;

public class ContactManager extends Observable {

    private static ContactManager instance;

    private volatile List<FreechatContact> mContact = new ArrayList<>();

    private volatile Map<UUID, FreechatContact> mContactMap = new HashMap<>(10);

    public static ContactManager getInstance() {
        if (instance == null) {
            instance = new ContactManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
        refreshContacts();
    }

    private boolean refreshContacts() {
        boolean changed = false;
        List<Contact> contacts = FCClient.getService(ContactService.class).queryAllContacts();
        if (contacts != null) {
            TVLog.log(String.format("SSSSSS refreshContacts and contact size from contactservices is %d, currentContactMap is %d", contacts.size(), mContactMap.size()));
            for (Contact contact : contacts) {
                final UUID contactUuid = contact.getContactUuid();
                if (!mContactMap.containsKey(contactUuid)) {
                    addFreechatContact(contact, contactUuid);
                    changed = true;
                } else {
                    String nickName = mContactMap.get(contactUuid).getContact().getNickName();
                    if (nickName == null || !nickName.equals(contact.getNickName())) {
                        addFreechatContact(contact, contactUuid);
                        changed = true;
                    }
                }
            }
            for (Iterator<Map.Entry<UUID, FreechatContact>> it = mContactMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<UUID, FreechatContact> entry = it.next();
                if (!isInList(entry.getKey(), contacts)) {
                    it.remove();
                    mContact.remove(entry.getValue());
                    changed = true;
                }
            }
        } else {
            TVLog.log(String.format("SSSSSS refreshcontact get 0 contact from contactServices"));
        }
        TVLog.log(String.format("SSSSSS refreshcontact chagned is %s", changed ? "true" : "false"));
        return changed;
    }

    private void addFreechatContact(Contact contact, UUID contactUuid) {
        FreechatContact freechatContact = FreechatContact.newFreechatContact(contact);
        mContact.remove(mContactMap.get(contactUuid));
        mContactMap.put(contactUuid, freechatContact);
        mContact.add(freechatContact);
    }

    private boolean isInList(UUID key, List<Contact> contacts) {
        for (Contact contact : contacts) {
            if (contact.getContactUuid().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public List<FreechatContact> getContacts() {
        return mContact;
    }

    public boolean isContactReachable(UUID guid) {
        return this.mContactMap.containsKey(guid);
    }

    public FreechatContact getContact(UUID guid) {
        if (isContactReachable(guid)) {
            return this.mContactMap.get(guid);
        }
        return null;
    }

    public Contact getSelf() {
        return FCClient.getService(ContactService.class).getMyself();
    }

    public void destroy() {
        mContact = null;
        mContactMap = null;
        instance = null;
    }

    private Observer<IOException> avatarSendExceptionObserver = new Observer<IOException>() {

        @Override
        public void onEvent(IOException ex) {
            ex.printStackTrace();
        }
    };

    public Observer<IOException> getAvatarSendExceptionObserver() {
        return avatarSendExceptionObserver;
    }

    private Observer<Void> mContactObserver = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            TVLog.log(String.format("SSSSSS receive contact change event start"));
            boolean changed = refreshContacts();
            TVLog.log(String.format("SSSSSS receive contact change event end"));
            FCClient.getService(AvatarService.class).pushAvatarToAll();
        }
    };

    public Observer<Void> getContactObserver() {
        return mContactObserver;
    }
}
