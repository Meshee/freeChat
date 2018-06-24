package cn.meshee.freechat.app;

import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.live.LiveService;

public class LiveManager {

    private static LiveManager instance;

    private MyApp freeChatApplication;

    private String meshLiveResource;

    private Observer<Void> meshResourceObserver = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            List<String> resources = FCClient.getService(LiveService.class).queryMeshLiveResources();
            if (resources != null && resources.size() > 0) {
                meshLiveResource = resources.get(0);
            } else {
                meshLiveResource = null;
            }
        }
    };

    private Observer<IOException> remoteLiveHostExceptionObserver = new Observer<IOException>() {

        @Override
        public void onEvent(IOException ex) {
            Toast.makeText(freeChatApplication, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    public static LiveManager getInstance(MyApp freeChatApplication) {
        if (instance == null) {
            instance = new LiveManager();
            instance.init(freeChatApplication);
        }
        return instance;
    }

    private void init(MyApp freeChatApplication) {
        this.freeChatApplication = freeChatApplication;
        meshLiveResource = null;
    }

    public String getMeshLiveResource() {
        return meshLiveResource;
    }

    public Observer<Void> getMeshResourceObserver() {
        return meshResourceObserver;
    }

    public Observer<IOException> getRemoteLiveHostExceptionObserver() {
        return remoteLiveHostExceptionObserver;
    }
}
