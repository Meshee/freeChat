package cn.meshee.freechat.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lqr.emoji.LQREmotionKit;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.loader.ImageLoader;
import com.lqr.imagepicker.view.CropImageView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.FcService;
import cn.meshee.fclib.api.avatar.AvatarServiceObserve;
import cn.meshee.fclib.api.contact.ContactServiceObserve;
import cn.meshee.fclib.api.conversation.ConversationServiceObserve;
import cn.meshee.fclib.api.file.FileServiceObserve;
import cn.meshee.fclib.api.live.LiveServiceObserve;
import cn.meshee.fclib.api.message.MessageServiceObserve;
import cn.meshee.freechat.app.base.BaseApp;
import cn.meshee.freechat.util.AppUtils;

public class MyApp extends BaseApp {

    private MessageManager messageManager;

    private ContactManager contactManager;

    private ConversationManager conversationManager;

    private LiveManager liveManager;

    public ContactManager getContactManager() {
        return contactManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }

    public LiveManager getLiveManager() {
        return liveManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFreechat();
        initImagePicker();
        LQREmotionKit.init(this, (context, path, imageView) -> Glide.with(context).load(path).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView));
    }

    private void initFreechat() {
        FCClient.init(this);
        startFcService();
    }

    private void startFcService() {
        Intent intent = new Intent(this, FcService.class);
        startService(intent);
    }

    private String getContactNickName(String contactRawId) {
        String accountName = AppUtils.get3rdPartyAccountName(getApplicationContext());
        if (accountName == null) {
            return String.format("%s-%s", contactRawId, AppUtils.getPhoneModelAndId(getApplicationContext()));
        }
        return accountName;
    }

    public void initManagers() {
        final FileServiceObserve fileServiceObserve = FCClient.getService(FileServiceObserve.class);
        final MessageServiceObserve messageServiceObserve = FCClient.getService(MessageServiceObserve.class);
        final ContactServiceObserve contactServiceObserve = FCClient.getService(ContactServiceObserve.class);
        final AvatarServiceObserve avatarServiceObserve = FCClient.getService(AvatarServiceObserve.class);
        final ConversationServiceObserve conversationServiceObserve = FCClient.getService(ConversationServiceObserve.class);
        final LiveServiceObserve liveServiceObserve = FCClient.getService(LiveServiceObserve.class);
        if (messageManager == null) {
            messageManager = MessageManager.getInstance();
            if (messageServiceObserve != null) {
                messageServiceObserve.observeIncomeMessage(messageManager.getMessageObserver(), true);
            }
            if (fileServiceObserve != null) {
                fileServiceObserve.observeFileReceiveProgress(messageManager.getFileProgressObserver(), true);
                fileServiceObserve.observeFileReceiveEvent(messageManager.getFileMessageEventObserver(), true);
                fileServiceObserve.observeFileSendEvent(messageManager.getFileSendExceptionObserver(), true);
            }
        }
        if (contactManager == null) {
            contactManager = ContactManager.getInstance();
            if (contactServiceObserve != null) {
                contactServiceObserve.observeContactChange(contactManager.getContactObserver(), true);
            }
            if (avatarServiceObserve != null) {
                avatarServiceObserve.observeAvatarSendEvent(contactManager.getAvatarSendExceptionObserver(), true);
            }
        }
        if (conversationManager == null) {
            conversationManager = ConversationManager.getInstance();
            if (messageServiceObserve != null) {
                messageServiceObserve.observeIncomeMessage(conversationManager.getMessageObserver(), true);
            }
            if (conversationServiceObserve != null) {
                conversationServiceObserve.observeConversationChange(conversationManager.getConversationObserver(), true);
            }
        }
        if (liveManager == null) {
            liveManager = LiveManager.getInstance(this);
            if (liveServiceObserve != null) {
                liveServiceObserve.observeMeshLiveResourceChange(liveManager.getMeshResourceObserver(), true);
                liveServiceObserve.observeRemoteLiveHostExceptionEvent(liveManager.getRemoteLiveHostExceptionObserver(), true);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        uninitManagers();
    }

    private void uninitManagers() {
        final FileServiceObserve fileServiceObserve = FCClient.getService(FileServiceObserve.class);
        final MessageServiceObserve messageServiceObserve = FCClient.getService(MessageServiceObserve.class);
        final ContactServiceObserve contactServiceObserve = FCClient.getService(ContactServiceObserve.class);
        final AvatarServiceObserve avatarServiceObserve = FCClient.getService(AvatarServiceObserve.class);
        final ConversationServiceObserve conversationServiceObserve = FCClient.getService(ConversationServiceObserve.class);
        final LiveServiceObserve liveServiceObserve = FCClient.getService(LiveServiceObserve.class);
        if (messageManager != null) {
            if (messageServiceObserve != null) {
                messageServiceObserve.observeIncomeMessage(messageManager.getMessageObserver(), false);
            }
            if (fileServiceObserve != null) {
                fileServiceObserve.observeFileReceiveProgress(messageManager.getFileProgressObserver(), false);
                fileServiceObserve.observeFileReceiveEvent(messageManager.getFileMessageEventObserver(), false);
                fileServiceObserve.observeFileSendEvent(messageManager.getFileSendExceptionObserver(), false);
            }
        }
        if (contactManager != null) {
            if (contactServiceObserve != null) {
                contactServiceObserve.observeContactChange(contactManager.getContactObserver(), false);
            }
            if (avatarServiceObserve != null) {
                avatarServiceObserve.observeAvatarSendEvent(contactManager.getAvatarSendExceptionObserver(), false);
            }
        }
        if (conversationManager != null) {
            if (messageServiceObserve != null) {
                messageServiceObserve.observeIncomeMessage(conversationManager.getMessageObserver(), false);
            }
            if (conversationServiceObserve != null) {
                conversationServiceObserve.observeConversationChange(conversationManager.getConversationObserver(), false);
            }
        }
        if (liveManager != null) {
            if (liveServiceObserve != null) {
                liveServiceObserve.observeMeshLiveResourceChange(liveManager.getMeshResourceObserver(), false);
                liveServiceObserve.observeRemoteLiveHostExceptionEvent(liveManager.getRemoteLiveHostExceptionObserver(), false);
            }
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader() {

            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(getContext()).load(Uri.parse("file://" + path).toString()).centerCrop().into(imageView);
            }

            @Override
            public void clearMemoryCache() {
            }
        });
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(true);
        imagePicker.setSaveRectangle(true);
        imagePicker.setSelectLimit(9);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(1000);
        imagePicker.setOutPutY(1000);
    }
}
