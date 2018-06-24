package cn.meshee.freechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.optionitemview.OptionItemView;
import java.util.ArrayList;
import butterknife.BindView;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.presenter.MyInfoAtPresenter;
import cn.meshee.freechat.ui.view.IMyInfoAtView;

public class MyInfoActivity extends BaseActivity<IMyInfoAtView, MyInfoAtPresenter> implements IMyInfoAtView {

    public static final int REQUEST_IMAGE_PICKER = 1000;

    @BindView(R.id.llHeader)
    LinearLayout mLlHeader;

    @BindView(R.id.ivHeader)
    ImageView mIvHeader;

    @BindView(R.id.oivName)
    OptionItemView mOivName;

    @BindView(R.id.oivAccount)
    OptionItemView mOivAccount;

    @Override
    public void init() {
        super.init();
        registerBR();
    }

    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initListener() {
        mIvHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MyInfoActivity.this, ShowBigImageActivity.class);
            intent.putExtra("url", mPresenter.mUserInfo.getPortraitUri().toString());
            jumpToActivity(intent);
        });
        mLlHeader.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        });
        mOivName.setOnClickListener(v -> jumpToActivity(ChangeMyNameActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            ImageItem imageItem = images.get(0);
                            mPresenter.setPortrait(imageItem);
                        }
                    }
                }
        }
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.CHANGE_INFO_FOR_CHANGE_NAME, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.CHANGE_INFO_FOR_CHANGE_NAME);
    }

    @Override
    protected MyInfoAtPresenter createPresenter() {
        return new MyInfoAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_my_info;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvHeader;
    }

    @Override
    public OptionItemView getOivName() {
        return mOivName;
    }

    @Override
    public OptionItemView getOivAccount() {
        return mOivAccount;
    }
}
