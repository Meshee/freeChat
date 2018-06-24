package cn.meshee.freechat.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.recyclerview.LQRRecyclerView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.meshee.freechat.R;
import cn.meshee.freechat.model.Friend;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.presenter.CreateGroupAtPresenter;
import cn.meshee.freechat.ui.view.ICreateGroupAtView;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.QuickIndexBar;

public class CreateGroupActivity extends BaseActivity<ICreateGroupAtView, CreateGroupAtPresenter> implements ICreateGroupAtView {

    public ArrayList<String> mSelectedTeamMemberAccounts;

    @BindView(R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @BindView(R.id.rvSelectedContacts)
    LQRRecyclerView mRvSelectedContacts;

    @BindView(R.id.etKey)
    EditText mEtKey;

    private View mHeaderView;

    @BindView(R.id.rvContacts)
    LQRRecyclerView mRvContacts;

    @BindView(R.id.qib)
    QuickIndexBar mQib;

    @BindView(R.id.tvLetter)
    TextView mTvLetter;

    @Override
    public void init() {
        mSelectedTeamMemberAccounts = getIntent().getStringArrayListExtra("selectedMember");
    }

    @Override
    public void initView() {
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        mBtnToolbarSend.setText(UIUtils.getString(R.string.sure));
        mBtnToolbarSend.setEnabled(false);
        mHeaderView = View.inflate(this, R.layout.header_group_cheat, null);
    }

    @Override
    public void initData() {
        mPresenter.loadContacts();
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> {
            if (mSelectedTeamMemberAccounts == null) {
                mPresenter.createGroup();
            } else {
                mPresenter.addGroupMembers();
            }
        });
        mHeaderView.findViewById(R.id.tvSelectOneGroup).setOnClickListener(v -> UIUtils.showToast("选择一个群"));
        mQib.setOnLetterUpdateListener(new QuickIndexBar.OnLetterUpdateListener() {

            @Override
            public void onLetterUpdate(String letter) {
                showLetter(letter);
                if ("↑".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else if ("☆".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else {
                    List<Friend> data = ((LQRAdapterForRecyclerView) ((LQRHeaderAndFooterAdapter) mRvContacts.getAdapter()).getInnerAdapter()).getData();
                    for (int i = 0; i < data.size(); i++) {
                        Friend friend = data.get(i);
                        String c = friend.getDisplayNameSpelling().charAt(0) + "";
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

    private void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);
        mTvLetter.setText(letter);
    }

    private void hideLetter() {
        mTvLetter.setVisibility(View.GONE);
    }

    @Override
    protected CreateGroupAtPresenter createPresenter() {
        return new CreateGroupAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_create_group;
    }

    @Override
    public Button getBtnToolbarSend() {
        return mBtnToolbarSend;
    }

    @Override
    public LQRRecyclerView getRvContacts() {
        return mRvContacts;
    }

    @Override
    public LQRRecyclerView getRvSelectedContacts() {
        return mRvSelectedContacts;
    }

    @Override
    public EditText getEtKey() {
        return mEtKey;
    }

    @Override
    public View getHeaderView() {
        return mHeaderView;
    }
}
