package cn.meshee.freechat.ijk;

import android.os.Handler;
import android.os.HandlerThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.freechat.util.InternetUtils;
import http.HttpRequest;

public class LiveResource {

    private static final int INIT_DELAY = 2;

    private static final int DELAY = 30;

    private static LiveResource instance;

    private String defaultUrl = null;

    private List<String> videoResources = new CopyOnWriteArrayList<>();

    private ScheduledFuture<?> liveResourceFuture = null;

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public static LiveResource getInstance() {
        if (instance == null) {
            instance = new LiveResource();
            instance.init();
        }
        return instance;
    }

    private void init() {
        defaultUrl = "http://live.ksmobile.net/live/girls?page=1&pagesize=6";
    }

    public List<String> getVideoResources() {
        return videoResources;
    }

    public void refreshVideoList(String website) {
        if (liveResourceFuture == null) {
            liveResourceFuture = scheduleWithFixedDelay(new Runnable() {

                @Override
                public void run() {
                    TVLog.log(String.format("live request for url %s", website));
                    getLiveResourceFromCloud(website);
                }
            }, INIT_DELAY, DELAY);
        }
    }

    private void getLiveFixedResource() {
        videoResources.add("http://2000.liveplay.myqcloud.com/live/2000_1f4652b179af11e69776e435c87f075e.flv");
        videoResources.add("http://2000.liveplay.myqcloud.com/live/2000_44c6e64e79af11e69776e435c87f075e.flv");
        videoResources.add("http://2000.liveplay.myqcloud.com/live/2000_4eb4da7079af11e69776e435c87f075e.flv");
    }

    private void getLiveResourceFromCloud(String website) {
        try {
            HttpRequest request = InternetUtils.buildGetRequest(website);
            request.getConnection().setRequestProperty("connection", "close");
            request.getConnection().setRequestProperty("Accept-Encoding", "identity");
            String responseBody = request.body();
            int code = request.code();
            if (code == 200) {
                parseVideoResource(responseBody);
            }
        } catch (HttpRequest.HttpRequestException ex) {
            TVLog.log(String.format("Get HttpRequestException: %s", ex));
        } catch (JSONException ex) {
            TVLog.log(String.format("Get response json but parse with ex %s", ex));
        }
    }

    private void parseVideoResource(String jsonString) throws JSONException {
        List<String> latestResources = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonString);
        JSONObject data = obj.getJSONObject("data");
        JSONArray arrays = data.getJSONArray("video_info");
        if (arrays != null && arrays.length() > 0) {
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject jsonObjectVideo = (JSONObject) arrays.get(i);
                if (jsonObjectVideo != null) {
                    latestResources.add(jsonObjectVideo.get("videosource").toString());
                }
            }
        }
        videoResources = latestResources;
        TVLog.log(String.format("%d live resource %s is obtained upon the live request", videoResources.size(), videoResources.toString()));
    }

    private Runnable buildRunnable(final Runnable command) {
        HandlerThread handlerThread = new HandlerThread("fetch live resource");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        return new Runnable() {

            @Override
            public void run() {
                handler.post(command);
            }
        };
    }

    private ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, long initialDelay, long delay) {
        Runnable runnable = buildRunnable(command);
        return Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
    }
}
