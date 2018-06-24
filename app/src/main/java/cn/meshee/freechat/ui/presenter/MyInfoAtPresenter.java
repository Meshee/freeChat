package cn.meshee.freechat.ui.presenter;

import android.net.Uri;
import com.bumptech.glide.Glide;
import com.lqr.imagepicker.bean.ImageItem;
import java.io.File;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.avatar.AvatarService;
import cn.meshee.fclib.api.avatar.model.Avatar;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.model.UserInfo;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IMyInfoAtView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyInfoAtPresenter extends BasePresenter<IMyInfoAtView> {

    public UserInfo mUserInfo;

    public MyInfoAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        mUserInfo = AppUtils.getUserInfo();
        if (mUserInfo != null) {
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
            getView().getOivName().setRightText(mUserInfo.getName());
            getView().getOivAccount().setRightText(mUserInfo.getUserId());
        }
    }

    public void setPortrait(ImageItem imageItem) {
        if (imageItem != null && imageItem.path != null) {
            AvatarService avatarService = FCClient.getService(AvatarService.class);
            Observable.just(1).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(num -> {
                Avatar avatar = avatarService.saveMyAvatar(imageItem.path);
                avatarService.pushAvatarToAll();
                Observable.just(avatar).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(avatar1 -> {
                    Uri uri = Uri.fromFile(new File(avatar1.getAvatarPath()));
                    if (uri != null) {
                        mUserInfo.setPortraitUri(uri);
                        Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
                        BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
                        BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_FRIEND);
                    }
                }, this::uploadError);
            }, this::uploadError);
        }
    }

    private void uploadError(Throwable throwable) {
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(R.string.set_fail));
    }
}
