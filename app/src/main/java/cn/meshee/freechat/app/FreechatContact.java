package cn.meshee.freechat.app;

import android.support.annotation.NonNull;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.freechat.util.PinyinUtils;

public class FreechatContact implements Comparable<FreechatContact> {

    private Contact contact;

    private String nameSpelling;

    private CharSequence displayNameSpelling;

    public FreechatContact(Contact contact) {
        this.contact = contact;
        this.nameSpelling = PinyinUtils.getPinyin(contact.getNickName());
        this.displayNameSpelling = nameSpelling;
    }

    public Contact getContact() {
        return contact;
    }

    public static FreechatContact newFreechatContact(Contact contact) {
        return new FreechatContact(contact);
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    @Override
    public int compareTo(@NonNull FreechatContact friend) {
        return this.getContact().getNickName().compareTo(friend.getContact().getNickName());
    }

    public CharSequence getDisplayNameSpelling() {
        return displayNameSpelling;
    }
}
