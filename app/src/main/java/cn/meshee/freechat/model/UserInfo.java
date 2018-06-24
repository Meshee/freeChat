package cn.meshee.freechat.model;

import android.net.Uri;
import java.util.UUID;

public class UserInfo {

    private String id;

    private String name;

    private Uri portraitUri;

    private UUID contactUuid;

    public UserInfo(UUID contactUuid, String id, String name, Uri portraitUri) {
        this.id = id;
        this.name = name;
        this.portraitUri = portraitUri;
        this.contactUuid = contactUuid;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Uri getPortraitUri() {
        return portraitUri;
    }

    public String getUserId() {
        return id;
    }

    public void setPortraitUri(Uri portraitUri) {
        this.portraitUri = portraitUri;
    }
}
