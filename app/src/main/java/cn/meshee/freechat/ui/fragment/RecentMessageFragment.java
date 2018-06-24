package cn.meshee.freechat.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lqr.recyclerview.LQRRecyclerView;
import java.util.List;
import butterknife.BindView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.avatar.AvatarServiceObserve;
import cn.meshee.fclib.api.avatar.model.Avatar;
import cn.meshee.fclib.api.conversation.ConversationServiceObserve;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.message.MessageServiceObserve;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.base.BaseFragment;
import cn.meshee.freechat.ui.presenter.RecentMessageFgPresenter;
import cn.meshee.freechat.ui.view.IRecentMessageFgView;
import cn.meshee.freechat.util.UIUtils;

public class RecentMessageFragment extends BaseFragment<IRecentMessageFgView, RecentMessageFgPresenter> implements IRecentMessageFgView {

    @BindView(R.id.rvRecentMessage)
    LQRRecyclerView mRvRecentMessage;

    private Observer<List<FcMessage>> messageObserver = new Observer<List<FcMessage>>() {

        @Override
        public void onEvent(List<FcMessage> fcMessages) {
            if (fcMessages != null) {
                mPresenter.getConversations();
            }
        }
    };

    private Observer<Void> conversationChanges = new Observer<Void>() {

        @Override
        public void onEvent(Void aVoid) {
            mPresenter.getConversations();
        }
    };

    private Observer<Avatar> avatarObserver = new Observer<Avatar>() {

        @Override
        public void onEvent(Avatar avatar) {
            mPresenter.getConversations();
        }
    };

    @Override
    public void init() {
        registerBR();
        registerChangeListeners();
    }

    private void registerChangeListeners() {
        try {
            ConversationServiceObserve observeConversation = FCClient.getService(ConversationServiceObserve.class);
            observeConversation.observeConversationChange(conversationChanges, true);
            MessageServiceObserve observeMessage = FCClient.getService(MessageServiceObserve.class);
            observeMessage.observeIncomeMessage(messageObserver, true);
            AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
            observe.observeAvatarChange(avatarObserver, true);
        } catch (NullPointerException ex) {
            TVLog.log(String.format("RecentMessageFragment registerChangeListeners null exception "));
            TVLog.log(String.format("isAccountBound is %s", String.valueOf(FCClient.isAccountBound())));
            TVLog.saveLogs();
            throw ex;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getConversations();
    }

    @Override
    public void initData() {
        UIUtils.postTaskDelay(() -> {
            mPresenter.getConversations();
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBR();
        unregisterChangeListener();
    }

    private void unregisterChangeListener() {
        ConversationServiceObserve observeConversation = FCClient.getService(ConversationServiceObserve.class);
        observeConversation.observeConversationChange(conversationChanges, false);
        MessageServiceObserve observeMessage = FCClient.getService(MessageServiceObserve.class);
        observeMessage.observeIncomeMessage(messageObserver, false);
        AvatarServiceObserve observe = FCClient.getService(AvatarServiceObserve.class);
        observe.observeAvatarChange(avatarObserver, false);
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.UPDATE_CONVERSATIONS, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.getConversations();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.UPDATE_CONVERSATIONS);
    }

    @Override
    protected RecentMessageFgPresenter createPresenter() {
        return new RecentMessageFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_recent_message;
    }

    @Override
    public LQRRecyclerView getRvRecentMessage() {
        return mRvRecentMessage;
    }
}
