package cn.meshee.freechat.ui.activity;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import butterknife.BindView;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.util.UIUtils;

public class ShowBigImageActivity extends BaseActivity {

    private String mUrl;

    @BindView(R.id.pv)
    PhotoView mPv;

    @BindView(R.id.pb)
    ProgressBar mPb;

    private FrameLayout mView;

    private PopupWindow mPopupWindow;

    @Override
    public void init() {
        mUrl = getIntent().getStringExtra("url");
    }

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.header_pic));
        if (TextUtils.isEmpty(mUrl)) {
            finish();
            return;
        }
        mPv.enable();
        Glide.with(this).load(Uri.parse(mUrl)).placeholder(R.mipmap.default_image).centerCrop().into(mPv);
    }

    @Override
    public void initListener() {
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_show_big_image;
    }
}
