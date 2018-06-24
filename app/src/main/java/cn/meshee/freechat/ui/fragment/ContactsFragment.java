package cn.meshee.freechat.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.recyclerview.LQRRecyclerView;
import java.util.List;
import butterknife.BindView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.avatar.AvatarServiceObserve;
import cn.meshee.fclib.api.avatar.model.Avatar;
import cn.meshee.fclib.api.contact.ContactServiceObserve;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.app.FreechatContact;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.base.BaseFragment;
import cn.meshee.freechat.ui.presenter.ContactsFgPresenter;
import cn.meshee.freechat.ui.view.IContactsFgView;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.QuickIndexBar;

public class ContactsFragment extends BaseFragment<IContactsFgView, ContactsFgPresenter> implements IContactsFgView {

    @BindView(R.id.rvContacts)
    LQRRecyclerView mRvContacts;

    @BindView(R.id.qib)
    QuickIndexBar mQib;

    @BindView(R.id.tvLetter)
    TextView mTvLetter;

    private View mHeaderView;

    private TextView mFooterView;

    private Observer<Avatar> avatarObserver = new Observer<Avatar>() {

        @Override
        public void onEvent(Avatar avatar) {
            mPresenter.loadContacts();
        }
    };

    private Observer<Void> contactObserver = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            mPresenter.loadContacts();
        }
    };

    @Override
    public void init() {
        registerBR();
        registerAvatarChangeListener();
        registerContactChangeListener();
    }

    private void registerContactChangeListener() {
        ContactServiceObserve observe = FCClient.getService(ContactServiceObserve.class);
        observe.observeContactChange(contactObserver, true);
    }

    private void registerAvatarChangeListener() {
        AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
        observe.observeAvatarChange(avatarObserver, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadContacts();
    }

    @Override
    public void initView(View rootView) {
        mHeaderView = View.inflate(getActivity(), R.layout.header_rv_contacts, null);
        mFooterView = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(50));
        mFooterView.setLayoutParams(params);
        mFooterView.setGravity(Gravity.CENTER);
    }

    @Override
    public void initData() {
        mPresenter.loadContacts();
    }

    @Override
    public void initListener() {
        mQib.setOnLetterUpdateListener(new QuickIndexBar.OnLetterUpdateListener() {

            @Override
            public void onLetterUpdate(String letter) {
                showLetter(letter);
                if ("↑".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else if ("☆".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else {
                    List<FreechatContact> data = ((LQRAdapterForRecyclerView) ((LQRHeaderAndFooterAdapter) mRvContacts.getAdapter()).getInnerAdapter()).getData();
                    for (int i = 0; i < data.size(); i++) {
                        FreechatContact contact = data.get(i);
                        String c = contact.getDisplayNameSpelling().charAt(0) + "";
                        if (c.equalsIgnoreCase(letter)) {
                            mRvContacts.moveToPosition(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onLetterCancel() {
                hideLetter();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
        unregisterAvatarsChangeListener();
        unregisterContactsChangeListener();
    }

    private void unregisterContactsChangeListener() {
        ContactServiceObserve observe = FCClient.getService(ContactServiceObserve.class);
        observe.observeContactChange(contactObserver, false);
    }

    private void unregisterAvatarsChangeListener() {
        AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
        observe.observeAvatarChange(avatarObserver, false);
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.UPDATE_RED_DOT, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
            }
        });
        BroadcastManager.getInstance(getActivity()).register(AppConst.UPDATE_FRIEND, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadContacts();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_RED_DOT);
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_FRIEND);
    }

    private void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);
        mTvLetter.setText(letter);
    }

    private void hideLetter() {
        mTvLetter.setVisibility(View.GONE);
    }

    public void showQuickIndexBar(boolean show) {
        if (mQib != null) {
            mQib.setVisibility(show ? View.VISIBLE : View.GONE);
            mQib.invalidate();
        }
    }

    @Override
    protected ContactsFgPresenter createPresenter() {
        return new ContactsFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_contacts;
    }

    @Override
    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public LQRRecyclerView getRvContacts() {
        return mRvContacts;
    }

    @Override
    public TextView getFooterView() {
        return mFooterView;
    }
}
