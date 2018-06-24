package cn.meshee.freechat.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.avatar.AvatarService;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.settings.SettingService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.model.UserInfo;

public class AppUtils {

    private static final String[] PROJECTION_PATH = { "_data" };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String query(Context context, Uri uri) {
        final String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            return uri.getPath();
        } else if ("content".equals(scheme)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                final String authority = uri.getAuthority();
                final String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.downloads.documents".equals(authority)) {
                    return query(context, Uri.withAppendedPath(Uri.parse("content://downloads/public_downloads"), docId), PROJECTION_PATH);
                } else if ("com.android.externalstorage.documents".equals(authority)) {
                    final String[] identity = docId.split(":");
                    if ("primary".equals(identity[0])) {
                        return Environment.getExternalStorageDirectory() + File.separator + identity[1];
                    }
                } else if ("com.android.providers.media.documents".equals(authority)) {
                    Uri contentUri = null;
                    final String[] identity = docId.split(":");
                    if ("image".equals(identity[0])) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(identity[0])) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(identity[0])) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    if (contentUri != null) {
                        return query(context, Uri.withAppendedPath(contentUri, identity[1]), PROJECTION_PATH);
                    }
                }
            } else {
                return query(context, uri, PROJECTION_PATH);
            }
        }
        return null;
    }

    private static String query(Context context, Uri uri, String[] projection) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
            }
        } catch (IllegalArgumentException e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static String randomString(int length) {
        assert (length > 0);
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String get3rdPartyAccountName(Context context) {
        String weixin = null;
        String qq = null;
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (isQQ(account.type)) {
                qq = account.name.trim();
            } else if (isWeixin(account.type)) {
                weixin = account.name.trim();
            }
        }
        if (weixin != null && weixin.length() > 0) {
            return weixin;
        }
        if (qq != null && qq.length() > 0) {
            return qq;
        }
        return null;
    }

    private static boolean isQQ(String type) {
        return type.equalsIgnoreCase("com.tencent.mobileqq.account");
    }

    private static boolean isWeixin(String type) {
        return type.equalsIgnoreCase("com.tencent.mm.account");
    }

    public static String getPhoneModelAndId(Context context) {
        String model = Build.MODEL;
        String id = Build.ID;
        return String.format("%s(%s)", model, id);
    }

    public static Uri getContactAvatarUri(Contact contact) {
        if (contact != null) {
            return getContactAvatarUri(contact.getContactUuid());
        }
        return Uri.parse("android.resource://cn.meshee.freechat/" + R.mipmap.default_header);
    }

    public static Uri getContactAvatarUri(UUID contactUuid) {
        if (contactUuid != null) {
            Uri avatarUri = FCClient.getService(AvatarService.class).getAvatarUri(contactUuid);
            if (avatarUri != null) {
                return avatarUri;
            }
        }
        return Uri.parse("android.resource://cn.meshee.freechat/" + R.mipmap.default_header);
    }

    public static UserInfo getUserInfo() {
        Contact contact = ContactManager.getInstance().getSelf();
        UserInfo userInfo = null;
        if (contact != null) {
            try {
                userInfo = new UserInfo(contact.getContactUuid(), contact.getContactRawId(), contact.getNickName(), AppUtils.getContactAvatarUri(contact));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return userInfo;
    }

    public static List<Contact> loadAccounts() {
        SettingService settingService = FCClient.getService(SettingService.class);
        return settingService.getAllAccounts();
    }

    public static void saveAccount(Contact contact) {
        SettingService settingService = FCClient.getService(SettingService.class);
        settingService.saveAccount(contact);
    }

    public static String getUniqueResource(List<String> resource1, List<String> resource2) {
        if (resource1 == null) {
            resource1 = new ArrayList<String>();
        }
        if (resource2 == null) {
            resource2 = new ArrayList<String>();
        }
        Set<String> left = new HashSet<String>(resource1);
        Set<String> right = new HashSet<String>(resource2);
        left.remove(right);
        if (!left.isEmpty()) {
            int size = left.size();
            Object[] items = left.toArray();
            int random = new Random().nextInt(size);
            return (String) items[random];
        }
        return null;
    }
}
