package cn.meshee.freechat.ui.presenter;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.model.ConversationType;
import cn.meshee.fclib.api.file.model.FileProgress;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.fclib.api.message.model.MessageTypeEnum;
import cn.meshee.freechat.app.MessageManager;
import cn.meshee.freechat.app.MyApp;
import cn.meshee.freechat.ui.activity.ShowBigImageActivity;
import cn.meshee.freechat.ui.adapter.SessionAdapter;
import cn.meshee.freechat.ui.base.BaseFragmentActivity;
import cn.meshee.freechat.ui.base.BaseFragmentPresenter;
import cn.meshee.freechat.ui.view.ISessionAtView;
import cn.meshee.freechat.util.UIUtils;

public class SessionAtPresenter extends BaseFragmentPresenter<ISessionAtView> {

    public ConversationType mConversationType;

    private String mSessionId;

    private int mMessageCount = 5;

    private List<FcMessage> mData = new ArrayList<>();

    private SessionAdapter mAdapter;

    public SessionAtPresenter(BaseFragmentActivity context, String sessionId, ConversationType conversationType, List<Contact> participant) {
        super(context);
        mSessionId = sessionId;
        mConversationType = conversationType;
    }

    public void loadMessage() {
        loadData();
        setAdapter();
    }

    private void loadData() {
        getLocalHistoryMessage();
        setAdapter();
    }

    public void loadMore() {
        getLocalHistoryMessage();
        mAdapter.notifyDataSetChangedWrapper();
    }

    public void receiveNewMessage(FcMessage message) {
        mData.add(message);
        setAdapter();
    }

    public void resetDraft() {
    }

    public void saveDraft() {
        String draft = getView().getEtContent().getText().toString();
        if (!TextUtils.isEmpty(draft)) {
        }
    }

    public void setAdapter() {
        if (mAdapter == null) {
            buildSessionAdapter();
            UIUtils.postTaskDelay(() -> getView().getRvMsg().smoothMoveToPosition(mData.size() - 1), 200);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
            if (getView() != null && getView().getRvMsg() != null)
                rvMoveToBottom();
        }
    }

    private void buildSessionAdapter() {
        mAdapter = new SessionAdapter(mContext, mData, this);
        mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
            FcMessage message = mData.get(position);
            MessageTypeEnum msgType = message.getMessageType();
            if (msgType.equals(MessageTypeEnum.image)) {
                String imagePath = message.getMediaPath();
                Intent intent = new Intent(mContext, ShowBigImageActivity.class);
                intent.putExtra("url", Uri.fromFile(new File(imagePath)).toString());
                mContext.jumpToActivity(intent);
            } else if (msgType.equals(MessageTypeEnum.file)) {
                ackReceiveFile(message);
                mAdapter.notifyDataSetChangedWrapper();
            }
        });
        UIUtils.postTaskDelay(() -> getView().getRvMsg().smoothMoveToPosition(mData.size() - 1), 200);
        getView().getRvMsg().setAdapter(mAdapter);
    }

    private void ackReceiveFile(FcMessage message) {
        if (message != null) {
            MessageManager.getInstance().ackReceiveFile(message);
        }
    }

    private void rvMoveToBottom() {
        getView().getRvMsg().smoothMoveToPosition(mData.size() - 1);
    }

    public void sendTextMsg() {
        sendTextMsg(getView().getEtContent().getText().toString());
        getView().getEtContent().setText("");
    }

    public void sendTextMsg(String content) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp != null) {
            MessageManager mgr = myApp.getMessageManager();
            FcMessage fcMessage = mgr.sendTextMsg(mSessionId, content);
            mgr.addMessage(mSessionId, fcMessage);
        }
    }

    public void sendImgMsg(String imageFileThumbPath, String imageFileSourcePath) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp != null) {
            MessageManager mgr = myApp.getMessageManager();
            FcMessage fcMessage = mgr.sendImgMsg(mSessionId, imageFileSourcePath);
            mgr.addMessage(mSessionId, fcMessage);
        }
    }

    public void sendFileMsg(String filePath) {
        MyApp myApp = (MyApp) mContext.getApplication();
        if (myApp != null) {
            MessageManager mgr = myApp.getMessageManager();
            FcMessage fcMessage = mgr.sendFileMsg(mSessionId, filePath);
            mgr.addMessage(mSessionId, fcMessage);
        }
    }

    public void getLocalHistoryMessage() {
        String messageUuid = null;
        if (mData.size() > 0) {
            messageUuid = mData.get(0).getMessageUuid();
        }
        List<FcMessage> messages = MessageManager.getInstance().getMessage(mSessionId, messageUuid, mMessageCount);
        if (messages != null && messages.size() > 0) {
            saveHistoryMsg(messages);
        }
        getView().getRefreshLayout().endRefreshing();
    }

    private void saveHistoryMsg(List<FcMessage> messages) {
        if (messages != null && messages.size() > 0) {
            for (FcMessage msg : messages) {
                mData.add(0, msg);
            }
            if (getView().getRvMsg().getAdapter() == null) {
                if (mAdapter == null) {
                    buildSessionAdapter();
                }
                getView().getRvMsg().setAdapter(mAdapter);
            }
            getView().getRvMsg().moveToPosition(messages.size() - 1);
        }
    }

    public void receiveFileProgress(FileProgress fileProgress) {
        refreshAdapter();
    }

    public void refreshAdapter() {
        mAdapter.notifyDataSetChangedWrapper();
    }
}
