package cn.meshee.freechat.app;

import cn.meshee.freechat.util.FileUtils;

public class AppConst {

    public static final String FETCH_COMPLETE = "fetch_complete";

    public static final String UPDATE_FRIEND = "update_friend";

    public static final String UPDATE_RED_DOT = "update_red_dot";

    public static final String GROUP_LIST_UPDATE = "group_list_update";

    public static final String UPDATE_GROUP_MEMBER = "update_group_member";

    public static final String CHANGE_INFO_FOR_ME = "change_info_for_me";

    public static final String CHANGE_INFO_FOR_CHANGE_NAME = "change_info_for_change_name";

    public static final String CHANGE_INFO_FOR_USER_INFO = "change_info_for_user_info";

    public static final String UPDATE_CONVERSATIONS = "update_conversations";

    public static final String UPDATE_CURRENT_SESSION_NAME = "update_current_session_name";

    public static final String REFRESH_CURRENT_SESSION = "refresh_current_session";

    public static final String CLOSE_CURRENT_SESSION = "close_current_session";

    public static final class QrCodeCommon {

        public static final String ADD = "add:";

        public static final String JOIN = "join:";
    }

    public static final String VIDEO_SAVE_DIR = FileUtils.getDir("video");

    public static final String PHOTO_SAVE_DIR = FileUtils.getDir("photo");
}
