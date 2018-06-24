package cn.meshee.freechat.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class PopupWindowUtils {

    public static PopupWindow getPopupWindowInCenter(View contentView, View parentView) {
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        return getPopupWindowInCenter(contentView, width, height, parentView);
    }

    public static PopupWindow getPopupWindowInCenter(View contentView, int width, int height, View parentView) {
        return getPopupWindowAtLocation(contentView, width, height, parentView, Gravity.CENTER, 0, 0);
    }

    public static PopupWindow getPopupWindowAtLocation(View contentView, int width, int height, View parentView, int gravityType, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        popupWindow.showAtLocation(parentView, gravityType, xoff, yoff);
        return popupWindow;
    }

    public static PopupWindow getPopupWindowAtLocation(View contentView, View parentView, int gravityType, int xoff, int yoff) {
        return getPopupWindowAtLocation(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, parentView, gravityType, xoff, yoff);
    }

    public static PopupWindow getPopupWindowAsDropDownParentAuto(View contentView, int width, int height, View anchorView, Activity activity) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        if (isShowBottom(activity, anchorView)) {
            popupWindow.showAsDropDown(anchorView, 0, 0);
        } else {
            popupWindow.showAsDropDown(anchorView, 0, -2 * anchorView.getHeight());
        }
        return popupWindow;
    }

    public static PopupWindow getPopupWindowAsDropDown(View contentView, int width, int height, View anchorView, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        popupWindow.showAsDropDown(anchorView, xoff, yoff);
        return popupWindow;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static PopupWindow getPopupWindowAsDropDown(View contentView, int width, int height, View anchorView, int gravityType, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        popupWindow.showAsDropDown(anchorView, xoff, yoff, gravityType);
        return popupWindow;
    }

    private static boolean isShowBottom(Activity context, View itemView) {
        int screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
        int[] location = new int[2];
        itemView.getLocationInWindow(location);
        int itemViewY = location[1];
        int distance = screenHeight - itemViewY - itemView.getHeight();
        return distance >= itemView.getHeight();
    }

    @NonNull
    private static PopupWindow getPopupWindow(View contentView, int width, int height) {
        PopupWindow popupWindow = new PopupWindow(contentView, width, height, true);
        popupWindow.setOutsideTouchable(false);
        openOutsideTouchable(popupWindow);
        return popupWindow;
    }

    public static void openOutsideTouchable(PopupWindow popupWindow) {
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
    }

    public static void makeWindowDark(Activity activity) {
        makeWindowDark(activity, 0.7f);
    }

    public static void makeWindowDark(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        activity.getWindow().setAttributes(lp);
    }

    public static void makeWindowLight(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }
}
