package cn.meshee.freechat.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import java.util.ArrayList;
import java.util.List;
import cn.meshee.freechat.R;
import cn.meshee.freechat.model.ConversationType;
import cn.meshee.freechat.model.GroupMember;
import cn.meshee.freechat.ui.activity.CreateGroupActivity;
import cn.meshee.freechat.ui.activity.RemoveGroupMemberActivity;
import cn.meshee.freechat.ui.activity.SessionActivity;
import cn.meshee.freechat.ui.activity.SessionInfoActivity;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.ISessionInfoAtView;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.CustomDialog;

public class SessionInfoAtPresenter extends BasePresenter<ISessionInfoAtView> {

    private ConversationType mConversationType;

    private String mSessionId;

    private List<GroupMember> mData = new ArrayList<>();

    private LQRAdapterForRecyclerView<GroupMember> mAdapter;

    private boolean mIsManager = false;

    public String mDisplayName = "";

    private CustomDialog mSetDisplayNameDialog;

    public SessionInfoAtPresenter(BaseActivity context, String sessionId, ConversationType conversationType) {
        super(context);
        mSessionId = sessionId;
        mConversationType = conversationType;
    }

    public void loadMembers() {
        setAdapter();
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<GroupMember>(mContext, mData, R.layout.item_member_info) {

                @Override
                public void convert(LQRViewHolderForRecyclerView helper, GroupMember item, int position) {
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    if (mIsManager && position >= mData.size() - 2) {
                        if (position == mData.size() - 2) {
                            ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                        } else {
                            ivHeader.setImageResource(R.mipmap.ic_remove_team_member);
                        }
                        helper.setText(R.id.tvName, "");
                    } else if (!mIsManager && position >= mData.size() - 1) {
                        ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                        helper.setText(R.id.tvName, "");
                    } else {
                        Glide.with(mContext).load(item.getPortraitUri()).centerCrop().into(ivHeader);
                        helper.setText(R.id.tvName, item.getName());
                    }
                }
            };
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                if (mIsManager && position >= mData.size() - 2) {
                    if (position == mData.size() - 2) {
                        addMember(mConversationType == ConversationType.GROUP);
                    } else {
                        removeMember();
                    }
                } else if (!mIsManager && position >= mData.size() - 1) {
                    addMember(mConversationType == ConversationType.GROUP);
                } else {
                }
            });
            getView().getRvMember().setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
        }
    }

    private void addMember(boolean isAddMember) {
        Intent intent = new Intent(mContext, CreateGroupActivity.class);
        if (isAddMember) {
            ArrayList<String> selectedTeamMemberAccounts = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                selectedTeamMemberAccounts.add(mData.get(i).getUserId());
            }
            intent.putExtra("selectedMember", selectedTeamMemberAccounts);
        }
        mContext.startActivityForResult(intent, SessionInfoActivity.REQ_ADD_MEMBERS);
    }

    private void removeMember() {
        Intent intent = new Intent(mContext, RemoveGroupMemberActivity.class);
        intent.putExtra("sessionId", mSessionId);
        mContext.startActivityForResult(intent, SessionInfoActivity.REQ_REMOVE_MEMBERS);
    }

    public void addGroupMember(ArrayList<String> selectedIds) {
        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
    }

    public void deleteGroupMembers(ArrayList<String> selectedIds) {
        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
    }

    public void loadOtherInfo(int sessionType, String sessionId) {
        switch(sessionType) {
            case SessionActivity.SESSION_TYPE_PRIVATE:
                break;
        }
    }

    public void quit() {
    }

    public void clearConversationMsg() {
    }

    public void setDisplayName() {
        View view = View.inflate(mContext, R.layout.dialog_group_display_name_change, null);
        mSetDisplayNameDialog = new CustomDialog(mContext, view, R.style.MyDialog);
        EditText etName = (EditText) view.findViewById(R.id.etName);
        etName.setText(mDisplayName);
        etName.setSelection(mDisplayName.length());
        view.findViewById(R.id.tvCancle).setOnClickListener(v -> mSetDisplayNameDialog.dismiss());
        view.findViewById(R.id.tvOk).setOnClickListener(v -> {
            String displayName = etName.getText().toString().trim();
            if (!TextUtils.isEmpty(displayName)) {
            }
        });
        mSetDisplayNameDialog.show();
    }
}
