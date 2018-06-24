package cn.meshee.freechat.ui.presenter;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.settings.SettingService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.MyApp;
import cn.meshee.freechat.app.base.BaseApp;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IBindAtView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;

public class BindAtPresenter extends BasePresenter<IBindAtView> {

    private ArrayList<Contact> accounts = new ArrayList<>();

    public BindAtPresenter(BaseActivity context) {
        super(context);
    }

    public void register() {
        String rawId = getView().getSpRawId().getText().toString();
        String nickName = getView().getEtNickName().getText().toString().trim();
        if (TextUtils.isEmpty(rawId) || rawId.equals("No Selection")) {
            UIUtils.showToast(UIUtils.getString(R.string.account_not_empty));
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            UIUtils.showToast(UIUtils.getString(R.string.nickname_not_empty));
            return;
        }
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp != null) {
            UUID contactUuid = UUID.randomUUID();
            for (Contact contact : accounts) {
                if (contact.getContactRawId().equals(rawId)) {
                    contactUuid = contact.getContactUuid();
                    break;
                }
            }
            Contact newContact = new Contact(rawId, nickName, contactUuid);
            FCClient.bindAccount(newContact);
            BaseApp.setUnbinding(false);
            myApp.initManagers();
            saveAccount(newContact);
        }
        mContext.jumpToActivityAndClearTask(MainActivity.class);
        mContext.finish();
    }

    public String[] getAccountIds() {
        List<String> ids = new ArrayList<String>();
        if (accounts != null && accounts.size() > 0) {
            for (Contact contact : accounts) {
                ids.add(contact.getContactRawId());
            }
        }
        String[] idArray = new String[ids.size()];
        idArray = ids.toArray(idArray);
        return idArray;
    }

    public String getNickNameForAccount(int position) {
        if (accounts != null && accounts.size() > position) {
            return accounts.get(position).getNickName();
        }
        return "";
    }

    public void loadAccounts() {
        List<Contact> contacts = AppUtils.loadAccounts();
        if (contacts != null && contacts.size() > 0) {
            accounts = new ArrayList<>();
            for (Contact contact : contacts) {
                if (contact != null) {
                    accounts.add(contact);
                }
            }
        } else {
            accounts = new ArrayList<>();
        }
    }

    private void saveAccount(Contact contact) {
        AppUtils.saveAccount(contact);
        SettingService settingService = FCClient.getService(SettingService.class);
        accounts = new ArrayList<>(settingService.getAllAccounts());
    }
}
