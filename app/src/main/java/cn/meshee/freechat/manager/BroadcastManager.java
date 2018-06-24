package cn.meshee.freechat.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {

    private Context mContext;

    private static BroadcastManager mInstance;

    private Map<String, BroadcastReceiver> mReceiverMap;

    private BroadcastManager(Context context) {
        mContext = context.getApplicationContext();
        mReceiverMap = new HashMap<>();
    }

    public static BroadcastManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BroadcastManager.class) {
                if (mInstance == null)
                    mInstance = new BroadcastManager(context);
            }
        }
        return mInstance;
    }

    public void register(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            mContext.registerReceiver(receiver, filter);
            mReceiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcast(String action) {
        sendBroadcast(action, "");
    }

    public void sendBroadcast(String action, String s) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("String", s);
        mContext.sendBroadcast(intent);
    }

    public void unregister(String action) {
        if (mReceiverMap != null) {
            BroadcastReceiver receiver = mReceiverMap.remove(action);
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        }
    }
}
