package cn.meshee.freechat.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import java.util.ArrayList;
import java.util.List;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.ConversationService;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.conversation.model.ConversationType;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.ConversationManager;
import cn.meshee.freechat.app.FreechatConversation;
import cn.meshee.freechat.model.GroupMember;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.activity.SessionActivity;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IRecentMessageFgView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.CustomDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RecentMessageFgPresenter extends BasePresenter<IRecentMessageFgView> {

    private List<Conversation> mData = new ArrayList<>();

    private LQRAdapterForRecyclerView<Conversation> mAdapter;

    private int mUnreadCountTotal = 0;

    private LQRNineGridImageViewAdapter mNgivAdapter = new LQRNineGridImageViewAdapter<GroupMember>() {

        @Override
        protected void onDisplayImage(Context context, ImageView imageView, GroupMember groupMember) {
            Glide.with(context).load(groupMember.getPortraitUri()).centerCrop().into(imageView);
        }
    };

    private CustomDialog mConversationMenuDialog;

    public RecentMessageFgPresenter(BaseActivity context) {
        super(context);
    }

    public void getConversations() {
        loadData();
        setAdapter();
    }

    private void loadError(Throwable throwable) {
        UIUtils.showToast(UIUtils.getString(R.string.load_error));
    }

    private void loadData() {
        ConversationService cs = FCClient.getService(ConversationService.class);
        if (cs != null) {
            Observable.just(cs.queryAllConversations()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(conversations -> {
                if (conversations != null && conversations.size() > 0) {
                    mData.clear();
                    mData.addAll(conversations);
                    filterData(mData);
                }
            }, this::loadError);
        }
    }

    private void filterData(List<Conversation> conversations) {
        for (int i = 0; i < conversations.size(); i++) {
            Conversation item = conversations.get(i);
            if (item.getConversationType() == ConversationType.GROUP) {
                if (item.getParticipant() == null || item.getParticipant().size() <= 0) {
                    conversations.remove(i);
                    i--;
                }
            } else if (item.getConversationType() == ConversationType.P2P) {
                final FreechatConversation freechatConversation = ConversationManager.getInstance().getFreechatConversation(item.getConversationId());
                if (freechatConversation != null && freechatConversation.getTotalMessageCount() <= 0) {
                    conversations.remove(i);
                    i--;
                }
            }
        }
        mUnreadCountTotal = 0;
        for (Conversation conversation : conversations) {
            final FreechatConversation freechatConversation = ConversationManager.getInstance().getFreechatConversation(conversation.getConversationId());
            if (freechatConversation != null) {
                mUnreadCountTotal += freechatConversation.getUnreadMessageCount();
            }
        }
        updateTotalUnreadView();
        if (mAdapter != null) {
            mAdapter.setData(mData);
        }
    }

    private void updateTotalUnreadView() {
        if (mUnreadCountTotal > 0) {
            final int count4Display = mUnreadCountTotal < 99 ? mUnreadCountTotal : 99;
            ((MainActivity) mContext).getTvMessageCount().setText(String.valueOf(count4Display));
            ((MainActivity) mContext).getTvMessageCount().setVisibility(View.VISIBLE);
            mContext.setToolbarTitle(UIUtils.getString(R.string.app_name) + "(" + count4Display + ")");
        } else {
            ((MainActivity) mContext).getTvMessageCount().setVisibility(View.GONE);
            mContext.setToolbarTitle(UIUtils.getString(R.string.app_name));
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<Conversation>(mContext, mData, R.layout.item_recent_message) {

                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Conversation item, int position) {
                    if (item.getConversationType() == ConversationType.P2P) {
                        ImageView ivHeader = helper.getView(R.id.ivHeader);
                        Contact userInfo = item.getParticipant().get(0);
                        if (userInfo != null) {
                            FreechatConversation conversation = ConversationManager.getInstance().getFreechatConversation(item.getConversationId());
                            if (conversation == null)
                                return;
                            int unread = conversation.getUnreadMessageCount();
                            Uri uri = AppUtils.getContactAvatarUri(userInfo);
                            Glide.with(mContext).load(uri).centerCrop().into(ivHeader);
                            helper.setText(R.id.tvDisplayName, item.getConversationTitle()).setViewVisibility(R.id.ngiv, View.GONE).setViewVisibility(R.id.ivHeader, View.VISIBLE).setText(R.id.tvCount, unread <= 0 ? "" : String.valueOf(unread < 99 ? unread : 99)).setViewVisibility(R.id.tvCount, unread > 0 ? View.VISIBLE : View.GONE);
                        }
                    } else {
                        List<Contact> groups = item.getParticipant();
                        LQRNineGridImageView ngiv = helper.getView(R.id.ngiv);
                        ngiv.setAdapter(mNgivAdapter);
                        helper.setText(R.id.tvDisplayName, groups == null ? "" : item.getConversationTitle()).setViewVisibility(R.id.ngiv, View.VISIBLE).setViewVisibility(R.id.ivHeader, View.GONE);
                    }
                }
            };
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                Intent intent = new Intent(mContext, SessionActivity.class);
                Conversation item = mData.get(position);
                intent.putExtra("sessionId", item.getConversationId());
                if (item.getConversationType() == ConversationType.P2P) {
                    intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_PRIVATE);
                } else {
                    intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_GROUP);
                }
                mContext.jumpToActivity(intent);
            });
            if (getView() != null && getView().getRvRecentMessage() != null) {
                getView().getRvRecentMessage().setAdapter(mAdapter);
            }
        }
    }
}
