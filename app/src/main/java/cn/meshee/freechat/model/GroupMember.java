package cn.meshee.freechat.model;

import java.io.Serializable;

public class GroupMember implements Serializable {

    private String userId;

    private String name;

    private String portraitUri;

    public GroupMember(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
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
}
