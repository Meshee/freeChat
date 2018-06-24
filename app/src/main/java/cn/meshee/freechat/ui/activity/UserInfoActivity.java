package cn.meshee.freechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lqr.optionitemview.OptionItemView;
import java.util.UUID;
import butterknife.BindView;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.ConversationService;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.app.ContactManager;
import cn.meshee.freechat.app.FreechatContact;
import cn.meshee.freechat.manager.BroadcastManager;
import cn.meshee.freechat.model.Friend;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.util.AppUtils;

public class UserInfoActivity extends BaseActivity {

    FreechatContact freechatContact;

    @BindView(R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;

    @BindView(R.id.ivHeader)
    ImageView mIvHeader;

    @BindView(R.id.tvName)
    TextView mTvName;

    @BindView(R.id.ivGender)
    ImageView mIvGender;

    @BindView(R.id.tvAccount)
    TextView mTvAccount;

    @BindView(R.id.tvNickName)
    TextView mTvNickName;

    @BindView(R.id.tvArea)
    TextView mTvArea;

    @BindView(R.id.tvSignature)
    TextView mTvSignature;

    @BindView(R.id.oivRemarkAndTag)
    OptionItemView mOivRemarkAndTag;

    @BindView(R.id.llArea)
    LinearLayout mLlArea;

    @BindView(R.id.llSignature)
    LinearLayout mLlSignature;

    @BindView(R.id.btnCheat)
    Button mBtnCheat;

    @BindView(R.id.btnAddToContact)
    Button mBtnAddToContact;

    @BindView(R.id.rlMenu)
    RelativeLayout mRlMenu;

    @BindView(R.id.svMenu)
    ScrollView mSvMenu;

    @BindView(R.id.oivAlias)
    OptionItemView mOivAlias;

    @BindView(R.id.oivDelete)
    OptionItemView mOivDelete;

    private Friend mFriend;

    @Override
    public void init() {
        Intent intent = getIntent();
        UUID contactUUID = (UUID) intent.getSerializableExtra("contactUUID");
        freechatContact = ContactManager.getInstance().getContact(contactUUID);
        registerBR();
    }

    @Override
    public void initView() {
        if (freechatContact == null) {
            finish();
            return;
        }
        mIbToolbarMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        Contact contact = freechatContact.getContact();
        Glide.with(this).load(AppUtils.getContactAvatarUri(contact)).centerCrop().into(mIvHeader);
        mTvAccount.setText(contact.getContactRawId());
        mTvName.setText(contact.getNickName());
        mTvNickName.setText(contact.getNickName());
    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> showMenu());
        mOivRemarkAndTag.setOnClickListener(v -> jumpToSetAlias());
        mBtnCheat.setOnClickListener(v -> {
            Intent intent = new Intent(UserInfoActivity.this, SessionActivity.class);
            ConversationService conversationService = FCClient.getService(ConversationService.class);
            final Contact contact = freechatContact.getContact();
            Conversation conversation = conversationService.createP2PConverstaion(contact, contact.getNickName(), AppUtils.getContactAvatarUri(contact));
            intent.putExtra("sessionId", conversation.getConversationId());
            jumpToActivity(intent);
            finish();
        });
        mRlMenu.setOnClickListener(v -> hideMenu());
        mOivAlias.setOnClickListener(v -> {
            jumpToSetAlias();
            hideMenu();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    private void jumpToSetAlias() {
    }

    private void showMenu() {
        mRlMenu.setVisibility(View.VISIBLE);
        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        ta.setDuration(200);
        mSvMenu.startAnimation(ta);
    }

    private void hideMenu() {
        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        ta.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRlMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ta.setDuration(200);
        mSvMenu.startAnimation(ta);
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.CHANGE_INFO_FOR_USER_INFO, new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                initData();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.CHANGE_INFO_FOR_USER_INFO);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_user_info;
    }
}
