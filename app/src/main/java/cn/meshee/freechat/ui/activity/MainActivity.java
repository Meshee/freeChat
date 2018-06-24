package cn.meshee.freechat.ui.activity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.FcService;
import cn.meshee.fclib.api.live.LiveService;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.ui.adapter.CommonFragmentPagerAdapter;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BaseFragment;
import cn.meshee.freechat.ui.fragment.FragmentFactory;
import cn.meshee.freechat.ui.presenter.MainAtPresenter;
import cn.meshee.freechat.ui.view.IMainAtView;
import cn.meshee.freechat.util.PopupWindowUtils;
import cn.meshee.freechat.util.UIUtils;

public class MainActivity extends BaseActivity<IMainAtView, MainAtPresenter> implements ViewPager.OnPageChangeListener, IMainAtView {

    private List<BaseFragment> mFragmentList = new ArrayList<>(4);

    private enum PageState {

        Message, Contacts, Discovery, Me
    }

    private PageState currentPage = null;

    View menuView = null;

    PopupWindow popupWindow = null;

    @BindView(R.id.ibAddMenu)
    ImageButton mIbAddMenu;

    @BindView(R.id.vpContent)
    ViewPager mVpContent;

    @BindView(R.id.llMessage)
    LinearLayout mLlMessage;

    @BindView(R.id.tvMessageNormal)
    TextView mTvMessageNormal;

    @BindView(R.id.tvMessagePress)
    TextView mTvMessagePress;

    @BindView(R.id.tvMessageTextNormal)
    TextView mTvMessageTextNormal;

    @BindView(R.id.tvMessageTextPress)
    TextView mTvMessageTextPress;

    @BindView(R.id.tvMessageCount)
    public TextView mTvMessageCount;

    @BindView(R.id.llContacts)
    LinearLayout mLlContacts;

    @BindView(R.id.tvContactsNormal)
    TextView mTvContactsNormal;

    @BindView(R.id.tvContactsPress)
    TextView mTvContactsPress;

    @BindView(R.id.tvContactsTextNormal)
    TextView mTvContactsTextNormal;

    @BindView(R.id.tvContactsTextPress)
    TextView mTvContactsTextPress;

    @BindView(R.id.tvContactCount)
    public TextView mTvContactCount;

    @BindView(R.id.tvContactRedDot)
    public TextView mTvContactRedDot;

    @BindView(R.id.llDiscovery)
    LinearLayout mLlDiscovery;

    @BindView(R.id.tvDiscoveryNormal)
    TextView mTvDiscoveryNormal;

    @BindView(R.id.tvDiscoveryPress)
    TextView mTvDiscoveryPress;

    @BindView(R.id.tvDiscoveryTextNormal)
    TextView mTvDiscoveryTextNormal;

    @BindView(R.id.tvDiscoveryTextPress)
    TextView mTvDiscoveryTextPress;

    @BindView(R.id.tvDiscoveryCount)
    public TextView mTvDiscoveryCount;

    @BindView(R.id.llMe)
    LinearLayout mLlMe;

    @BindView(R.id.tvMeNormal)
    TextView mTvMeNormal;

    @BindView(R.id.tvMePress)
    TextView mTvMePress;

    @BindView(R.id.tvMeTextNormal)
    TextView mTvMeTextNormal;

    @BindView(R.id.tvMeTextPress)
    TextView mTvMeTextPress;

    @BindView(R.id.tvMeCount)
    public TextView mTvMeCount;

    private boolean serviceBind;

    public static final int FOREGROUND_SERVICE = 0x889900;

    public static final String REMOTE_LIVE_HOST = "8461.liveplay.myqcloud.com";

    private final ServiceConnection serviceBinderConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            FcService.LocalBinder binder = (FcService.LocalBinder) service;
            FcService fcService = binder.getService();
            fcService.startForegroundNotification(FOREGROUND_SERVICE, buildForegroundNotification());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);
        b.setOngoing(true);
        b.setSmallIcon(android.R.drawable.stat_sys_download);
        return (b.build());
    }

    private void bindFcService() {
        Intent intent = new Intent(this, FcService.class);
        bindService(intent, serviceBinderConnection, Context.BIND_AUTO_CREATE);
        serviceBind = true;
    }

    private void unbindFcService() {
        if (serviceBind) {
            unbindService(serviceBinderConnection);
            serviceBind = false;
        }
    }

    @Override
    public void init() {
        bindFcService();
        FCClient.getService(LiveService.class).setRemoteLiveHost(REMOTE_LIVE_HOST);
        registerBR();
        FCClient.getService(NetworkService.class).restartAutoJoinNetwork();
    }

    @Override
    public void initData() {
        super.initData();
        mPresenter.fetchVideoList();
    }

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.app_name));
        mIbAddMenu.setVisibility(View.VISIBLE);
        setTransparency();
        mTvMessagePress.getBackground().setAlpha(255);
        mTvMessageTextPress.setTextColor(Color.argb(255, 69, 192, 26));
        mVpContent.setOffscreenPageLimit(3);
        mFragmentList.add(FragmentFactory.getInstance().getRecentMessageFragment());
        mFragmentList.add(FragmentFactory.getInstance().getContactsFragment());
        mFragmentList.add(FragmentFactory.getInstance().getDiscoveryFragment());
        mFragmentList.add(FragmentFactory.getInstance().getMeFragment());
        currentPage = PageState.Message;
        mVpContent.setAdapter(new CommonFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList));
        menuView = View.inflate(MainActivity.this, R.layout.menu_main, null);
    }

    @Override
    public void initListener() {
        mIbAddMenu.setOnClickListener(v -> {
            popupWindow = PopupWindowUtils.getPopupWindowAtLocation(menuView, getWindow().getDecorView(), Gravity.TOP | Gravity.RIGHT, UIUtils.dip2Px(5), mAppBar.getHeight() + 30);
            menuView.findViewById(R.id.tvCreateNetwork).setOnClickListener(v1 -> {
                mPresenter.createNetwork();
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvRestartAutoJoinNetwork).setOnClickListener(v1 -> {
                mPresenter.restartAutoJoinNetwork();
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvStopAutoJoinNetwork).setOnClickListener(v1 -> {
                mPresenter.stopAutoJoinNetwork();
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvEnableDisconnectWhenBackground).setOnClickListener(v1 -> {
                mPresenter.setDisconnectWhenBackground(true);
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvDisableDisconnectWhenBackground).setOnClickListener(v1 -> {
                mPresenter.setDisconnectWhenBackground(false);
                popupWindow.dismiss();
            });
        });
        mLlMessage.setOnClickListener(v -> bottomBtnClick(v));
        mLlContacts.setOnClickListener(v -> bottomBtnClick(v));
        mLlDiscovery.setOnClickListener(v -> bottomBtnClick(v));
        mLlMe.setOnClickListener(v -> bottomBtnClick(v));
        mVpContent.setOnPageChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindFcService();
        unRegisterBR();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.stopScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter.isRole4Scan() && isDiscoveryFgSelected()) {
            mPresenter.softScan();
        }
    }

    public boolean isDiscoveryFgSelected() {
        return currentPage == PageState.Discovery;
    }

    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public void bottomBtnClick(View view) {
        setTransparency();
        switch(view.getId()) {
            case R.id.llMessage:
                currentPage = PageState.Message;
                mVpContent.setCurrentItem(0, false);
                mTvMessagePress.getBackground().setAlpha(255);
                mTvMessageTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mPresenter.stopScan();
                break;
            case R.id.llContacts:
                currentPage = PageState.Contacts;
                mVpContent.setCurrentItem(1, false);
                mTvContactsPress.getBackground().setAlpha(255);
                mTvContactsTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mPresenter.stopScan();
                break;
            case R.id.llDiscovery:
                currentPage = PageState.Discovery;
                mVpContent.setCurrentItem(2, false);
                mTvDiscoveryNormal.getBackground().setAlpha(0);
                mTvDiscoveryPress.getBackground().setAlpha(255);
                mTvDiscoveryTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                if (mPresenter.isRole4Scan()) {
                    mPresenter.softScan();
                }
                break;
            case R.id.llMe:
                currentPage = PageState.Me;
                mVpContent.setCurrentItem(3, false);
                mTvMePress.getBackground().setAlpha(255);
                mTvMeTextPress.setTextColor(Color.argb(255, 69, 192, 26));
                mPresenter.stopScan();
                break;
        }
    }

    private void setTransparency() {
        mTvMessageNormal.getBackground().setAlpha(255);
        mTvContactsNormal.getBackground().setAlpha(255);
        mTvDiscoveryNormal.getBackground().setAlpha(255);
        mTvMeNormal.getBackground().setAlpha(255);
        mTvMessagePress.getBackground().setAlpha(1);
        mTvContactsPress.getBackground().setAlpha(1);
        mTvDiscoveryPress.getBackground().setAlpha(1);
        mTvMePress.getBackground().setAlpha(1);
        mTvMessageTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvContactsTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvDiscoveryTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvMeTextNormal.setTextColor(Color.argb(255, 153, 153, 153));
        mTvMessageTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvContactsTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvDiscoveryTextPress.setTextColor(Color.argb(0, 69, 192, 26));
        mTvMeTextPress.setTextColor(Color.argb(0, 69, 192, 26));
    }

    @Override
    protected MainAtPresenter createPresenter() {
        return new MainAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isToolbarCanBack() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int diaphaneity_one = (int) (255 * positionOffset);
        int diaphaneity_two = (int) (255 * (1 - positionOffset));
        switch(position) {
            case 0:
                mTvMessageNormal.getBackground().setAlpha(diaphaneity_one);
                mTvMessagePress.getBackground().setAlpha(diaphaneity_two);
                mTvContactsNormal.getBackground().setAlpha(diaphaneity_two);
                mTvContactsPress.getBackground().setAlpha(diaphaneity_one);
                mTvMessageTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                mTvMessageTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                mTvContactsTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                mTvContactsTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 192, 26));
                break;
            case 1:
                mTvContactsNormal.getBackground().setAlpha(diaphaneity_one);
                mTvContactsPress.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_two);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_one);
                mTvContactsTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                mTvContactsTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 192, 26));
                break;
            case 2:
                mTvDiscoveryNormal.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryPress.getBackground().setAlpha(diaphaneity_two);
                mTvMeNormal.getBackground().setAlpha(diaphaneity_two);
                mTvMePress.getBackground().setAlpha(diaphaneity_one);
                mTvDiscoveryTextNormal.setTextColor(Color.argb(diaphaneity_one, 153, 153, 153));
                mTvDiscoveryTextPress.setTextColor(Color.argb(diaphaneity_two, 69, 192, 26));
                mTvMeTextNormal.setTextColor(Color.argb(diaphaneity_two, 153, 153, 153));
                mTvMeTextPress.setTextColor(Color.argb(diaphaneity_one, 69, 192, 26));
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            FragmentFactory.getInstance().getContactsFragment().showQuickIndexBar(true);
        } else {
            FragmentFactory.getInstance().getContactsFragment().showQuickIndexBar(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state != ViewPager.SCROLL_STATE_IDLE) {
            FragmentFactory.getInstance().getContactsFragment().showQuickIndexBar(false);
        } else {
            FragmentFactory.getInstance().getContactsFragment().showQuickIndexBar(true);
        }
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.FETCH_COMPLETE, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                hideWaitingDialog();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.FETCH_COMPLETE);
    }

    @Override
    public TextView getTvMessageCount() {
        return mTvMessageCount;
    }

    @Override
    public void onBackPressed() {
    }
}
