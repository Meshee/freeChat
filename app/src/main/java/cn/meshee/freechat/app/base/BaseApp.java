package cn.meshee.freechat.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import java.util.LinkedList;
import java.util.List;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.freechat.app.MessageManager;

public class BaseApp extends MultiDexApplication {

    public static List<Activity> activities = new LinkedList<>();

    private static Context mContext;

    private static Thread mMainThread;

    private static long mMainThreadId;

    private static Looper mMainLooper;

    private static Handler mHandler;

    private static boolean unbinding = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mHandler = new Handler();
    }

    public static boolean isUnbinding() {
        return unbinding;
    }

    public static void setUnbinding(boolean unbinding) {
        BaseApp.unbinding = unbinding;
    }

    public static void exit(boolean restart) {
        Activity last = null;
        for (Activity activity : activities) {
            last = activity;
            activity.finish();
        }
        if (restart) {
            MessageManager.getInstance().clearMessage();
            FCClient.unbindAccount();
            unbinding = true;
            restart();
        } else {
            FCClient.exit();
        }
    }

    public static void restart() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        BaseApp.mContext = mContext;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static void setMainThread(Thread mMainThread) {
        BaseApp.mMainThread = mMainThread;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }

    public static void setMainThreadId(long mMainThreadId) {
        BaseApp.mMainThreadId = mMainThreadId;
    }

    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static void setMainThreadLooper(Looper mMainLooper) {
        BaseApp.mMainLooper = mMainLooper;
    }

    public static Handler getMainHandler() {
        return mHandler;
    }

    public static void setMainHandler(Handler mHandler) {
        BaseApp.mHandler = mHandler;
    }
}
