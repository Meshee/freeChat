package cn.meshee.freechat.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.app.FreechatContact;
import cn.meshee.freechat.app.base.BaseApp;
import cn.meshee.freechat.ui.activity.UserInfoActivity;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IContactsFgView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.SortUtils;
import cn.meshee.freechat.util.UIUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactsFgPresenter extends BasePresenter<IContactsFgView> {

    private List<FreechatContact> mData = new ArrayList<>();

    private LQRHeaderAndFooterAdapter mAdapter;

    public ContactsFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadContacts() {
        setAdapter();
        loadFreechat();
    }

    private void loadFreechat() {
        Observable.just(ContactManager.getInstance().getContacts()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contacts -> {
            if (contacts != null && contacts.size() > 0) {
                mData.clear();
                mData.addAll(contacts);
                getView().getFooterView().setText(UIUtils.getString(R.string.count_of_contacts, mData.size()));
                SortUtils.sortContacts(mData);
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
        }, this::loadError);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            LQRAdapterForRecyclerView adapter = new LQRAdapterForRecyclerView<FreechatContact>(mContext, mData, R.layout.item_contact) {

                @Override
                public void convert(LQRViewHolderForRecyclerView helper, FreechatContact item, int position) {
                    helper.setText(R.id.tvName, item.getContact().getNickName());
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    Glide.with(mContext).load(AppUtils.getContactAvatarUri(item.getContact())).centerCrop().into(ivHeader);
                    String str = "";
                    String currentLetter = item.getDisplayNameSpelling().charAt(0) + "";
                    if (position == 0) {
                        str = currentLetter;
                    } else {
                        String preLetter = mData.get(position - 1).getDisplayNameSpelling().charAt(0) + "";
                        if (!preLetter.equalsIgnoreCase(currentLetter)) {
                            str = currentLetter;
                        }
                    }
                    int nextIndex = position + 1;
                    if (nextIndex < mData.size() - 1) {
                        String nextLetter = mData.get(nextIndex).getDisplayNameSpelling().charAt(0) + "";
                        if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                            helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                        } else {
                            helper.setViewVisibility(R.id.vLine, View.VISIBLE);
                        }
                    } else {
                        helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                    }
                    if (position == mData.size() - 1) {
                        helper.setViewVisibility(R.id.vLine, View.GONE);
                    }
                    if (TextUtils.isEmpty(str)) {
                        helper.setViewVisibility(R.id.tvIndex, View.GONE);
                    } else {
                        helper.setViewVisibility(R.id.tvIndex, View.VISIBLE);
                        helper.setText(R.id.tvIndex, str);
                    }
                }
            };
            adapter.addHeaderView(getView().getHeaderView());
            adapter.addFooterView(getView().getFooterView());
            mAdapter = adapter.getHeaderAndFooterAdapter();
            getView().getRvContacts().setAdapter(mAdapter);
        }
        ((LQRAdapterForRecyclerView) mAdapter.getInnerAdapter()).setOnItemClickListener((lqrViewHolder, viewGroup, view, i) -> {
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            UUID contactUuid = mData.get(i - 1).getContact().getContactUuid();
            intent.putExtra("contactUUID", contactUuid);
            mContext.jumpToActivity(intent);
        });
    }

    private void loadError(Throwable throwable) {
        if (!(BaseApp.isUnbinding())) {
            UIUtils.showToast(UIUtils.getString(R.string.load_contacts_error));
        }
    }

    @Override
    public void attachView(IContactsFgView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
