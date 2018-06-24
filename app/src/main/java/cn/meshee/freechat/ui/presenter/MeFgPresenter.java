package cn.meshee.freechat.ui.presenter;

import com.bumptech.glide.Glide;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ijk.LiveResource;
import cn.meshee.freechat.model.UserInfo;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IMeFgView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;

public class MeFgPresenter extends BasePresenter<IMeFgView> {

    private UserInfo mUserInfo;

    private boolean isFirst = true;

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        mUserInfo = AppUtils.getUserInfo();
        fillView();
    }

    public void fillView() {
        if (mUserInfo != null) {
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
            getView().getTvAccount().setText(UIUtils.getString(R.string.my_chat_account, mUserInfo.getUserId()));
            getView().getTvName().setText(mUserInfo.getName());
        }
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
