package cn.meshee.freechat.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.avatar.AvatarService;
import cn.meshee.fclib.api.contact.ContactService;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.model.UserInfo;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;

public class ChangeMyNameActivity extends BaseActivity {

    @BindView(R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @BindView(R.id.etName)
    EditText mEtName;

    @Override
    public void initView() {
        mBtnToolbarSend.setText(UIUtils.getString(R.string.save));
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        UserInfo userInfo = AppUtils.getUserInfo();
        if (userInfo != null)
            mEtName.setText(userInfo.getName());
        mEtName.setSelection(mEtName.getText().toString().trim().length());
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> changeMyName());
        mEtName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtName.getText().toString().trim().length() > 0) {
                    mBtnToolbarSend.setEnabled(true);
                } else {
                    mBtnToolbarSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void changeMyName() {
        String nickName = mEtName.getText().toString().trim();
        Contact contact = ContactManager.getInstance().getSelf();
        if (contact != null) {
            FCClient.getService(ContactService.class).updateMyselfNickName(nickName);
            AppUtils.saveAccount(contact);
            AvatarService as = FCClient.getService(AvatarService.class);
            as.pushAvatarToAll();
            BroadcastManager.getInstance(getApplicationContext()).sendBroadcast(AppConst.CHANGE_INFO_FOR_CHANGE_NAME);
            BroadcastManager.getInstance(getApplicationContext()).sendBroadcast(AppConst.CHANGE_INFO_FOR_ME);
            BroadcastManager.getInstance(getApplicationContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
        }
        finish();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_change_name;
    }
}
