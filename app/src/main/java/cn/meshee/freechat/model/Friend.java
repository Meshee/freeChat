package cn.meshee.freechat.model;

import android.support.annotation.NonNull;

public class Friend implements Comparable<Friend> {

    private String userId;

    private String name;

    private String portraitUri;

    private String displayName;

    public Friend(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameSpelling() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            Friend friendInfo = (Friend) o;
            return (getUserId() != null && getUserId().equals(friendInfo.getUserId()));
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Friend friend) {
        return this.getDisplayName().compareTo(friend.getDisplayName());
    }
}
