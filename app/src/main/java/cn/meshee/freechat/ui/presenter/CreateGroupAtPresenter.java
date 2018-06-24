package cn.meshee.freechat.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import java.util.ArrayList;
import java.util.List;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.app.FreechatContact;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.ICreateGroupAtView;
import cn.meshee.freechat.util.SortUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CreateGroupAtPresenter extends BasePresenter<ICreateGroupAtView> {

    private String mGroupName = "";

    private List<FreechatContact> mData = new ArrayList<>();

    private List<FreechatContact> mSelectedData = new ArrayList<>();

    private LQRHeaderAndFooterAdapter mAdapter;

    private LQRAdapterForRecyclerView<FreechatContact> mSelectedAdapter;

    public CreateGroupAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadContacts() {
        loadData();
        setAdapter();
        setSelectedAdapter();
    }

    private void loadData() {
        Observable.just(ContactManager.getInstance().getContacts()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(friends -> {
            if (friends != null && friends.size() > 0) {
                mData.clear();
                mData.addAll(friends);
                SortUtils.sortContacts(mData);
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
        }, this::loadError);
    }

    private void setAdapter() {
    }

    private void setSelectedAdapter() {
        if (mSelectedAdapter == null) {
            mSelectedAdapter = new LQRAdapterForRecyclerView<FreechatContact>(mContext, mSelectedData, R.layout.item_selected_contact) {

                @Override
                public void convert(LQRViewHolderForRecyclerView helper, FreechatContact item, int position) {
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                }
            };
            getView().getRvSelectedContacts().setAdapter(mSelectedAdapter);
        }
    }

    public void addGroupMembers() {
        ArrayList<String> selectedIds = new ArrayList<>(mSelectedData.size());
        for (int i = 0; i < mSelectedData.size(); i++) {
            FreechatContact friend = mSelectedData.get(i);
            selectedIds.add(friend.getContact().getContactUuid().toString());
        }
        Intent data = new Intent();
        data.putStringArrayListExtra("selectedIds", selectedIds);
        mContext.setResult(Activity.RESULT_OK, data);
        mContext.finish();
    }

    public void createGroup() {
    }

    private void loadError(Throwable throwable) {
        mContext.hideWaitingDialog();
    }
}
