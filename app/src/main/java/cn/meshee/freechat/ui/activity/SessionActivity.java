package cn.meshee.freechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lqr.audio.AudioRecordManager;
import com.lqr.emoji.EmotionKeyboard;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.lqr.recyclerview.LQRRecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import butterknife.BindView;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.ConversationService;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.conversation.model.ConversationType;
import cn.meshee.fclib.api.file.model.FileMessage;
import cn.meshee.fclib.api.file.model.FileProgress;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.app.ConversationManager;
import cn.meshee.freechat.app.MessageManager;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.ui.base.BaseFragmentActivity;
import cn.meshee.freechat.ui.presenter.SessionAtPresenter;
import cn.meshee.freechat.ui.view.ISessionAtView;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.ImageUtils;
import cn.meshee.freechat.util.UIUtils;

public class SessionActivity extends BaseFragmentActivity<ISessionAtView, SessionAtPresenter> implements ISessionAtView, IEmotionSelectedListener, BGARefreshLayout.BGARefreshLayoutDelegate, Observer {

    public static final int REQUEST_IMAGE_PICKER = 1000;

    public static final int REQUEST_TAKE_PHOTO = 1001;

    public static final int REQUEST_FILE_PICKER = 1002;

    public static final int SESSION_TYPE_PRIVATE = 1;

    public static final int SESSION_TYPE_GROUP = 2;

    private String mSessionId = "";

    private boolean mIsFirst = false;

    private ConversationType mConversationType = ConversationType.P2P;

    private String conversationTitle;

    private List<Contact> mParticipant = new ArrayList<>();

    @BindView(R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;

    @BindView(R.id.llRoot)
    LinearLayout mLlRoot;

    @BindView(R.id.llContent)
    LinearLayout mLlContent;

    @BindView(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;

    @BindView(R.id.rvMsg)
    LQRRecyclerView mRvMsg;

    @BindView(R.id.ivAudio)
    ImageView mIvAudio;

    @BindView(R.id.btnAudio)
    Button mBtnAudio;

    @BindView(R.id.etContent)
    EditText mEtContent;

    @BindView(R.id.ivEmo)
    ImageView mIvEmo;

    @BindView(R.id.ivMore)
    ImageView mIvMore;

    @BindView(R.id.btnSend)
    Button mBtnSend;

    @BindView(R.id.flEmotionView)
    FrameLayout mFlEmotionView;

    @BindView(R.id.elEmotion)
    EmotionLayout mElEmotion;

    @BindView(R.id.llMore)
    LinearLayout mLlMore;

    @BindView(R.id.rlAlbum)
    RelativeLayout mRlAlbum;

    @BindView(R.id.rlTakePhoto)
    RelativeLayout mRlTakePhoto;

    @BindView(R.id.rlLocation)
    RelativeLayout mRlLocation;

    @BindView(R.id.rlFile)
    RelativeLayout mRlFile;

    private EmotionKeyboard mEmotionKeyboard;

    @Override
    public void init() {
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        ConversationService conversationService = FCClient.getService(ConversationService.class);
        if (conversationService != null) {
            Conversation conversation = FCClient.getService(ConversationService.class).findConversationBy(mSessionId);
            ConversationType sessionType = conversation.getConversationType();
            mConversationType = sessionType;
            conversationTitle = conversation.getConversationTitle();
            mParticipant = conversation.getParticipant();
            registerBR();
        }
    }

    @Override
    public void initView() {
        mIbToolbarMore.setImageResource(R.mipmap.ic_session_info);
        mIbToolbarMore.setVisibility(View.VISIBLE);
        mElEmotion.attachEditText(mEtContent);
        initEmotionKeyboard();
        initRefreshLayout();
        setTitle();
    }

    private void setTitle() {
        setToolbarTitle(conversationTitle);
    }

    @Override
    public void initData() {
        mPresenter.loadMessage();
    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, SessionInfoActivity.class);
            intent.putExtra("sessionId", mSessionId);
            intent.putExtra("sessionType", mConversationType.equals(ConversationType.P2P) ? SessionActivity.SESSION_TYPE_PRIVATE : SessionActivity.SESSION_TYPE_GROUP);
            jumpToActivity(intent);
        });
        mElEmotion.setEmotionSelectedListener(this);
        mElEmotion.setEmotionAddVisiable(true);
        mElEmotion.setEmotionSettingVisiable(true);
        mElEmotion.setEmotionExtClickListener(new IEmotionExtClickListener() {

            @Override
            public void onEmotionAddClick(View view) {
                UIUtils.showToast("add");
            }

            @Override
            public void onEmotionSettingClick(View view) {
                UIUtils.showToast("setting");
            }
        });
        mLlContent.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    closeBottomAndKeyboard();
                    break;
            }
            return false;
        });
        mRvMsg.setOnTouchListener((v, event) -> {
            closeBottomAndKeyboard();
            return false;
        });
        mIvAudio.setOnClickListener(v -> {
            if (mBtnAudio.isShown()) {
                hideAudioButton();
                mEtContent.requestFocus();
                if (mEmotionKeyboard != null) {
                    mEmotionKeyboard.showSoftInput();
                }
            } else {
                mEtContent.clearFocus();
                showAudioButton();
                hideEmotionLayout();
                hideMoreLayout();
            }
            UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
        });
        mEtContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtContent.getText().toString().trim().length() > 0) {
                    mBtnSend.setVisibility(View.VISIBLE);
                    mIvMore.setVisibility(View.GONE);
                } else {
                    mBtnSend.setVisibility(View.GONE);
                    mIvMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEtContent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
            }
        });
        mBtnSend.setOnClickListener(v -> mPresenter.sendTextMsg());
        mBtnAudio.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    AudioRecordManager.getInstance(SessionActivity.this).startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isCancelled(v, event)) {
                        AudioRecordManager.getInstance(SessionActivity.this).willCancelRecord();
                    } else {
                        AudioRecordManager.getInstance(SessionActivity.this).continueRecord();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    AudioRecordManager.getInstance(SessionActivity.this).stopRecord();
                    AudioRecordManager.getInstance(SessionActivity.this).destroyRecord();
                    break;
            }
            return false;
        });
        mRlAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        });
        mRlTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, TakePhotoActivity.class);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        });
        mRlFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            FCClient.getService(NetworkService.class).setForegroundForNonNativeActivity(true);
            startActivityForResult(Intent.createChooser(intent, "Select a File to Send"), REQUEST_FILE_PICKER);
        });
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth() || event.getRawY() < location[1] - 40;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsFirst) {
            mEtContent.clearFocus();
        } else {
            mIsFirst = false;
        }
        mPresenter.resetDraft();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        Log.e("CSDN_LQR", isOrig ? "发原图" : "不发原图");
                        for (ImageItem imageItem : images) {
                            File imageFileSource;
                            if (isOrig) {
                                imageFileSource = new File(imageItem.path);
                            } else {
                                imageFileSource = ImageUtils.genThumbImgFile(imageItem.path);
                            }
                            if (imageFileSource != null)
                                mPresenter.sendImgMsg(null, imageFileSource.getAbsolutePath());
                        }
                    }
                }
                break;
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    if (data.getBooleanExtra("take_photo", true)) {
                        mPresenter.sendImgMsg(null, path);
                    } else {
                    }
                }
                break;
            case REQUEST_FILE_PICKER:
                FCClient.getService(NetworkService.class).setForegroundForNonNativeActivity(false);
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = AppUtils.query(this, uri);
                        mPresenter.sendFileMsg(path);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.saveDraft();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof FcMessage) {
            FcMessage msg = (FcMessage) arg;
            if (msg.getConversationId().equals(mSessionId)) {
                mPresenter.receiveNewMessage(msg);
            }
        } else if (arg instanceof FileProgress) {
            mPresenter.receiveFileProgress((FileProgress) arg);
        } else if (arg instanceof FileMessage) {
            mPresenter.refreshAdapter();
        }
    }

    private void registerBR() {
        MessageManager.getInstance().addObserver(this);
        BroadcastManager.getInstance(this).register(AppConst.REFRESH_CURRENT_SESSION, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadMessage();
            }
        });
        BroadcastManager.getInstance(this).register(AppConst.UPDATE_CURRENT_SESSION_NAME, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                setTitle();
            }
        });
        BroadcastManager.getInstance(this).register(AppConst.CLOSE_CURRENT_SESSION, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        });
    }

    private void unRegisterBR() {
        MessageManager.getInstance().deleteObserver(this);
        BroadcastManager.getInstance(this).unregister(AppConst.REFRESH_CURRENT_SESSION);
        BroadcastManager.getInstance(this).unregister(AppConst.UPDATE_CURRENT_SESSION_NAME);
        BroadcastManager.getInstance(this).unregister(AppConst.CLOSE_CURRENT_SESSION);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        refreshViewHolder.setRefreshingText("");
        refreshViewHolder.setPullDownRefreshText("");
        refreshViewHolder.setReleaseRefreshText("");
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void initEmotionKeyboard() {
        mEmotionKeyboard = EmotionKeyboard.with(this);
        mEmotionKeyboard.bindToEditText(mEtContent);
        mEmotionKeyboard.bindToContent(mLlContent);
        mEmotionKeyboard.setEmotionLayout(mFlEmotionView);
        mEmotionKeyboard.bindToEmotionButton(mIvEmo, mIvMore);
        mEmotionKeyboard.setOnEmotionButtonOnClickListener(view -> {
            switch(view.getId()) {
                case R.id.ivEmo:
                    UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
                    mEtContent.clearFocus();
                    if (!mElEmotion.isShown()) {
                        if (mLlMore.isShown()) {
                            showEmotionLayout();
                            hideMoreLayout();
                            hideAudioButton();
                            return true;
                        }
                    } else if (mElEmotion.isShown() && !mLlMore.isShown()) {
                        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
                        return false;
                    }
                    showEmotionLayout();
                    hideMoreLayout();
                    hideAudioButton();
                    break;
                case R.id.ivMore:
                    UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
                    mEtContent.clearFocus();
                    if (!mLlMore.isShown()) {
                        if (mElEmotion.isShown()) {
                            showMoreLayout();
                            hideEmotionLayout();
                            hideAudioButton();
                            return true;
                        }
                    }
                    showMoreLayout();
                    hideEmotionLayout();
                    hideAudioButton();
                    break;
            }
            return false;
        });
    }

    private void showAudioButton() {
        mBtnAudio.setVisibility(View.VISIBLE);
        mEtContent.setVisibility(View.GONE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_keyboard);
        if (mFlEmotionView.isShown()) {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.interceptBackPress();
            }
        } else {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.hideSoftInput();
            }
        }
    }

    private void hideAudioButton() {
        mBtnAudio.setVisibility(View.GONE);
        mEtContent.setVisibility(View.VISIBLE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_voice);
    }

    private void showEmotionLayout() {
        mElEmotion.setVisibility(View.VISIBLE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_keyboard);
    }

    private void hideEmotionLayout() {
        mElEmotion.setVisibility(View.GONE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
    }

    private void showMoreLayout() {
        mLlMore.setVisibility(View.VISIBLE);
    }

    private void hideMoreLayout() {
        mLlMore.setVisibility(View.GONE);
    }

    private void closeBottomAndKeyboard() {
        mElEmotion.setVisibility(View.GONE);
        mLlMore.setVisibility(View.GONE);
        if (mEmotionKeyboard != null) {
            mEmotionKeyboard.interceptBackPress();
            mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
        }
    }

    @Override
    public void onBackPressed() {
        if (mElEmotion.isShown() || mLlMore.isShown()) {
            mEmotionKeyboard.interceptBackPress();
            mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
        } else {
            super.onBackPressed();
        }
        final ConversationService conversationService = FCClient.getService(ConversationService.class);
        if (conversationService != null) {
            Conversation conversation = conversationService.findConversationBy(mSessionId);
            if (conversation != null) {
                ConversationManager.getInstance().clearUnreadMessageFor(conversation.getConversationId());
            }
        }
    }

    @Override
    protected SessionAtPresenter createPresenter() {
        return new SessionAtPresenter(this, mSessionId, mConversationType, mParticipant);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_session;
    }

    @Override
    public void onEmojiSelected(String key) {
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
    }

    @Override
    public BGARefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    @Override
    public LQRRecyclerView getRvMsg() {
        return mRvMsg;
    }

    @Override
    public EditText getEtContent() {
        return mEtContent;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.loadMore();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
