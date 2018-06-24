package cn.meshee.freechat.ui.activity;

import cn.meshee.freechat.R;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;

public class AboutActivity extends BaseActivity {

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_about;
    }
}
