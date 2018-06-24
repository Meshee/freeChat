package cn.meshee.freechat.ui.activity;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import butterknife.BindView;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.model.UserInfo;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import static cn.meshee.freechat.R.id.ivCard;

public class QRCodeCardActivity extends BaseActivity {

    private UserInfo mUserInfo;

    private String mGroupId;

    @BindView(R.id.ivHeader)
    ImageView mIvHeader;

    @BindView(R.id.ngiv)
    LQRNineGridImageView mNgiv;

    @BindView(R.id.tvName)
    TextView mTvName;

    @BindView(ivCard)
    ImageView mIvCard;

    @BindView(R.id.tvTip)
    TextView mTvTip;

    @Override
    public void init() {
        mGroupId = getIntent().getStringExtra("groupId");
    }

    @Override
    public void initView() {
        mTvTip.setText(UIUtils.getString(R.string.qr_code_network_tip));
    }

    public void initData() {
        if (TextUtils.isEmpty(mGroupId)) {
            mUserInfo = AppUtils.getUserInfo();
            if (mUserInfo != null) {
                Glide.with(this).load(mUserInfo.getPortraitUri()).centerCrop().into(mIvHeader);
                mTvName.setText(mUserInfo.getName());
                setQRCode(AppConst.QrCodeCommon.ADD + mUserInfo.getUserId());
            }
        }
    }

    private void setQRCode(String content) {
        Observable.just(QRCodeEncoder.syncEncodeQRCode(content, UIUtils.dip2Px(100))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> mIvCard.setImageBitmap(bitmap), this::loadQRCardError);
    }

    private void loadQRCardError(Throwable throwable) {
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_qr_code_card;
    }
}
