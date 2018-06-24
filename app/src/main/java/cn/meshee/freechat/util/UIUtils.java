package cn.meshee.freechat.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.widget.Toast;
import cn.meshee.freechat.app.MyApp;
import cn.meshee.freechat.app.base.BaseApp;

public class UIUtils {

    public static Toast mToast;

    public static void showToast(String msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext(), "", duration);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public static Context getContext() {
        return BaseApp.getContext();
    }

    public static Resources getResource() {
        return getContext().getResources();
    }

    public static String getString(int resId) {
        return getResource().getString(resId);
    }

    public static String getString(int id, Object... formatArgs) {
        return getResource().getString(id, formatArgs);
    }

    public static int getColor(int colorId) {
        return getResource().getColor(colorId);
    }

    public static String getPackageName() {
        return getContext().getPackageName();
    }

    public static Handler getMainThreadHandler() {
        return MyApp.getMainHandler();
    }

    public static void postTaskDelay(Runnable task, int delayMillis) {
        getMainThreadHandler().postDelayed(task, delayMillis);
    }

    public static int dip2Px(int dip) {
        float density = getResource().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }
}
