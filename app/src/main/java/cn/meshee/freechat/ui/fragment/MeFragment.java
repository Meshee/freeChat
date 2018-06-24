package cn.meshee.freechat.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.lqr.optionitemview.OptionItemView;
import java.util.List;
import java.util.Random;
import butterknife.BindView;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.avatar.AvatarServiceObserve;
import cn.meshee.fclib.api.avatar.model.Avatar;
import cn.meshee.fclib.api.contact.ContactServiceObserve;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.live.LiveService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.ijk.LiveResource;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.model.UserInfo;
import cn.meshee.freechat.ui.activity.LiveMeActivity;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.activity.MyInfoActivity;
import cn.meshee.freechat.ui.activity.SettingActivity;
import cn.meshee.freechat.ui.base.BaseFragment;
import cn.meshee.freechat.ui.presenter.MeFgPresenter;
import cn.meshee.freechat.ui.view.IMeFgView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.CustomDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MeFragment extends BaseFragment<IMeFgView, MeFgPresenter> implements IMeFgView {

    private CustomDialog mQrCardDialog;

    @BindView(R.id.llMyInfo)
    LinearLayout mLlMyInfo;

    @BindView(R.id.ivHeader)
    ImageView mIvHeader;

    @BindView(R.id.tvName)
    TextView mTvName;

    @BindView(R.id.tvAccount)
    TextView mTvAccount;

    @BindView(R.id.oivSetting)
    OptionItemView mOivSetting;

    @BindView(R.id.oivLiveShow)
    OptionItemView mOivLiveShow;

    @BindView(R.id.oivMeshShow)
    OptionItemView mOivMeshShow;

    private Observer<Void> contactObserver = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            mPresenter.loadUserInfo();
            Contact self = ContactManager.getInstance().getSelf();
            AppUtils.saveAccount(self);
        }
    };

    private Observer<Avatar> avatarObserver = new Observer<Avatar>() {

        @Override
        public void onEvent(Avatar avatar) {
            mPresenter.loadUserInfo();
        }
    };

    @Override
    public void init() {
        registerBR();
        registerChangeListeners();
    }

    private void registerChangeListeners() {
        ContactServiceObserve observeContact = FCClient.getService(ContactServiceObserve.class);
        observeContact.observeContactChange(contactObserver, true);
        AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
        observe.observeAvatarChange(avatarObserver, true);
    }

    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initView(View rootView) {
    }

    @Override
    public void initListener() {
        mLlMyInfo.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(MyInfoActivity.class));
        mOivSetting.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(SettingActivity.class));
        mOivLiveShow.setOnClickListener(v -> {
            String liveResource = getUniqueLiveResource();
            if (liveResource != null) {
                ((MainActivity) getActivity()).jumpToActivityAndClearTop(LiveMeActivity.class, LiveMeActivity.SHOW_URL, liveResource);
            } else {
                Toast.makeText(getActivity(), "Live resource is NOT ready", Toast.LENGTH_SHORT).show();
            }
        });
        mOivMeshShow.setOnClickListener(v -> {
            String meshResource = getSingleMeshResource();
            if (meshResource != null) {
                ((MainActivity) getActivity()).jumpToActivityAndClearTop(LiveMeActivity.class, LiveMeActivity.SHOW_URL, meshResource);
            } else {
                Toast.makeText(getActivity(), "Mesh resource is NOT found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getAllMeshResources() {
        return FCClient.getService(LiveService.class).queryMeshLiveResources();
    }

    private List<String> getAllLiveResources() {
        return LiveResource.getInstance().getVideoResources();
    }

    private String getUniqueLiveResource() {
        return AppUtils.getUniqueResource(getAllLiveResources(), getAllMeshResources());
    }

    private String getSingleMeshResource() {
        List<String> resources = getAllMeshResources();
        if (resources != null && !resources.isEmpty()) {
            return resources.get(new Random().nextInt(resources.size()));
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
        unregisterChangeListener();
    }

    private void unregisterChangeListener() {
        ContactServiceObserve observeContact = FCClient.getService(ContactServiceObserve.class);
        observeContact.observeContactChange(contactObserver, false);
        AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
        observe.observeAvatarChange(avatarObserver, false);
    }

    private void showQRCard() {
        if (mQrCardDialog == null) {
            View qrCardView = View.inflate(getActivity(), R.layout.include_qrcode_card, null);
            ImageView ivHeader = (ImageView) qrCardView.findViewById(R.id.ivHeader);
            TextView tvName = (TextView) qrCardView.findViewById(R.id.tvName);
            ImageView ivCard = (ImageView) qrCardView.findViewById(R.id.ivCard);
            TextView tvTip = (TextView) qrCardView.findViewById(R.id.tvTip);
            tvTip.setText(UIUtils.getString(R.string.qr_code_network_tip));
            UserInfo userInfo = mPresenter.getUserInfo();
            if (userInfo != null) {
                Glide.with(getActivity()).load(userInfo.getPortraitUri()).centerCrop().into(ivHeader);
                tvName.setText(userInfo.getName());
                Observable.just(QRCodeEncoder.syncEncodeQRCode(AppConst.QrCodeCommon.ADD + userInfo.getUserId(), UIUtils.dip2Px(100))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> ivCard.setImageBitmap(bitmap), this::loadQRCardError);
            }
            mQrCardDialog = new CustomDialog(getActivity(), 300, 400, qrCardView, R.style.MyDialog);
        }
        mQrCardDialog.show();
    }

    private void loadQRCardError(Throwable throwable) {
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.CHANGE_INFO_FOR_ME, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.CHANGE_INFO_FOR_ME);
    }

    @Override
    protected MeFgPresenter createPresenter() {
        return new MeFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_me;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvHeader;
    }

    @Override
    public TextView getTvName() {
        return mTvName;
    }

    @Override
    public TextView getTvAccount() {
        return mTvAccount;
    }
}
