package cn.meshee.freechat.model;

import java.io.Serializable;

public class Groups implements Serializable {

    private String groupId;

    private String name;

    private String portraitUri;

    public Groups(String groupsId, String name, String portraitUri) {
        this.groupId = groupsId;
        this.name = name;
        this.portraitUri = portraitUri;
    }

    public String getGroupId() {
        return groupId;
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
