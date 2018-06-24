package cn.meshee.freechat.ui.activity;

import android.view.View;
import com.lqr.optionitemview.OptionItemView;
import butterknife.BindView;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.MyApp;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.widget.CustomDialog;

public class SettingActivity extends BaseActivity {

    private View mExitView;

    @BindView(R.id.oivAbout)
    OptionItemView mOivAbout;

    @BindView(R.id.oivExit)
    OptionItemView mOivExit;

    private CustomDialog mExitDialog;

    @Override
    public void initListener() {
        mOivAbout.setOnClickListener(v -> jumpToActivity(AboutActivity.class));
        mOivExit.setOnClickListener(v -> {
            if (mExitView == null) {
                mExitView = View.inflate(this, R.layout.dialog_exit, null);
                mExitDialog = new CustomDialog(this, mExitView, R.style.MyDialog);
                mExitView.findViewById(R.id.tvExitAccount).setOnClickListener(v1 -> {
                    mExitDialog.dismiss();
                    MyApp.exit(true);
                });
                mExitView.findViewById(R.id.tvExitApp).setOnClickListener(v1 -> {
                    mExitDialog.dismiss();
                    MyApp.exit(false);
                });
            }
            mExitDialog.show();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }
}
