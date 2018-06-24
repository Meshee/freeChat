package cn.meshee.freechat.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.live.LiveService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ijk.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class LiveMeActivity extends AppCompatActivity {

    public static final String SHOW_URL = "showUrl";

    private IjkVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveme);
        TableLayout mHudView = (TableLayout) findViewById(R.id.hud_view);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setHudView(mHudView);
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_VERBOSE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        String mVideoPath = getIntent().getStringExtra(SHOW_URL);
        final String uriString = toLocalAddr(mVideoPath);
        if (uriString != null) {
            mVideoView.setVideoURI(Uri.parse(uriString));
        }
        mVideoView.start();
    }

    private String toLocalAddr(String mVideoPath) {
        if (mVideoPath != null && mVideoPath.contains(MainActivity.REMOTE_LIVE_HOST)) {
            return mVideoPath.replace(MainActivity.REMOTE_LIVE_HOST, "127.0.0.1:" + Integer.toString(FCClient.getService(LiveService.class).getLocalLiveProxyServerPort()));
        }
        return mVideoPath;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mVideoView.stopPlayback();
        mVideoView.release(true);
        mVideoView.stopBackgroundPlay();
        IjkMediaPlayer.native_profileEnd();
    }
}
