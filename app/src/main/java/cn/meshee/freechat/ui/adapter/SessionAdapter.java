package cn.meshee.freechat.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.emoji.MoonUtils;
import java.io.File;
import java.util.List;
import cn.meshee.fclib.api.file.model.FileAckStatus;
import cn.meshee.fclib.api.file.model.FileMessage;
import cn.meshee.fclib.api.file.model.FileReceiveStatus;
import cn.meshee.fclib.api.message.model.ChatTypeEnum;
import cn.meshee.fclib.api.message.model.FcMessage;
import cn.meshee.fclib.api.message.model.MessageTypeEnum;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ui.presenter.SessionAtPresenter;
import cn.meshee.freechat.util.AppUtils;
import cn.meshee.freechat.util.TimeUtils;
import cn.meshee.freechat.util.UIUtils;
import cn.meshee.freechat.widget.BubbleImageView;

public class SessionAdapter extends LQRAdapterForRecyclerView<FcMessage> {

    private Context mContext;

    private List<FcMessage> mData;

    private SessionAtPresenter mPresenter;

    private static final int SEND_TEXT = R.layout.item_text_send;

    private static final int RECEIVE_TEXT = R.layout.item_text_receive;

    private static final int SEND_IMAGE = R.layout.item_image_send;

    private static final int RECEIVE_IMAGE = R.layout.item_image_receive;

    private static final int SEND_STICKER = R.layout.item_sticker_send;

    private static final int RECEIVE_STICKER = R.layout.item_sticker_receive;

    private static final int SEND_VIDEO = R.layout.item_video_send;

    private static final int RECEIVE_VIDEO = R.layout.item_video_receive;

    private static final int SEND_LOCATION = R.layout.item_location_send;

    private static final int RECEIVE_LOCATION = R.layout.item_location_receive;

    private static final int RECEIVE_NOTIFICATION = R.layout.item_notification;

    private static final int RECEIVE_VOICE = R.layout.item_audio_receive;

    private static final int SEND_VOICE = R.layout.item_audio_send;

    private static final int RECEIVE_FILE = R.layout.item_file_receive;

    private static final int SEND_FILE = R.layout.item_file_send;

    private static final int UNDEFINE_MSG = R.layout.item_no_support_msg_type;

    private static final int RECALL_NOTIFICATION = R.layout.item_notification;

    public SessionAdapter(Context context, List<FcMessage> data, SessionAtPresenter presenter) {
        super(context, data);
        mContext = context;
        mData = data;
        mPresenter = presenter;
    }

    @Override
    public void convert(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        setTime(helper, item, position);
        setView(helper, item, position);
        MessageTypeEnum msgType = item.getMessageType();
        if (msgType.equals(MessageTypeEnum.text) || msgType.equals(MessageTypeEnum.image) || msgType.equals(MessageTypeEnum.file)) {
            setAvatar(helper, item, position);
            setName(helper, item, position);
            setStatus(helper, item, position);
            setOnClick(helper, item, position);
        }
    }

    private void setView(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        if (item == null)
            return;
        MessageTypeEnum msgType = item.getMessageType();
        if (msgType.equals(MessageTypeEnum.text)) {
            String msgText = item.getContent();
            MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvText), msgText, ImageSpan.ALIGN_BOTTOM);
        } else if (msgType.equals(MessageTypeEnum.image)) {
            String imagePath = item.getMediaPath();
            BubbleImageView bivPic = helper.getView(R.id.bivPic);
            if (bivPic != null) {
                Glide.with(mContext).load(Uri.fromFile(new File(imagePath))).error(R.mipmap.default_img_failed).override(UIUtils.dip2Px(80), UIUtils.dip2Px(150)).centerCrop().into(bivPic);
            }
        } else if (msgType.equals(MessageTypeEnum.file)) {
            FileMessage fileMessage = item.getFileMessage();
            if (fileMessage != null) {
                boolean isSend = !item.isReceived();
                String fileName = fileMessage.getFileLocalSummary().getFileName();
                if (isSend) {
                    String msg = String.format("%s", fileName);
                    if (msg.length() > 63) {
                        msg = msg.substring(0, 30) + "..." + msg.substring(msg.length() - 30, msg.length());
                    }
                    MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvFileReceiving), msg, ImageSpan.ALIGN_BOTTOM);
                } else {
                    String msg = String.format("%s", fileName);
                    if (msg.length() > 43) {
                        msg = msg.substring(0, 20) + "..." + msg.substring(msg.length() - 20, msg.length());
                    }
                    MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvFileReceiving), msg, ImageSpan.ALIGN_BOTTOM);
                    FileAckStatus fileAckStatus = fileMessage.getFileStatus().getFileAckStatus();
                    FileReceiveStatus fileReceiveStatus = fileMessage.getFileStatus().getFileReceiveStatus();
                    String msgTip = "";
                    if (fileAckStatus.getAckStatus().equals(FileAckStatus.AckStatus.Not_Acked)) {
                        msgTip = UIUtils.getString(R.string.click_to_receive);
                    } else if (fileAckStatus.getAckStatus().equals(FileAckStatus.AckStatus.Ack_Failure)) {
                        msgTip = String.format("%s %s", UIUtils.getString(R.string.error), fileAckStatus.getAckFailure());
                    } else if (fileAckStatus.getAckStatus().equals(FileAckStatus.AckStatus.Acked)) {
                        FileReceiveStatus.ReceiveStatus rs = fileReceiveStatus.getReceiveStatus();
                        if (rs.equals(FileReceiveStatus.ReceiveStatus.Not_Start)) {
                            msgTip = UIUtils.getString(R.string.start_receive);
                        } else if (rs.equals(FileReceiveStatus.ReceiveStatus.Failure)) {
                            msgTip = String.format("%s %s", UIUtils.getString(R.string.receive_failure), fileReceiveStatus.getReceiveFailure());
                        } else if (rs.equals(FileReceiveStatus.ReceiveStatus.InProgress) || rs.equals(FileReceiveStatus.ReceiveStatus.Success)) {
                            msgTip = String.format("%s %d%%", UIUtils.getString(R.string.receive_file), fileReceiveStatus.getReceivePercentage());
                        }
                    }
                    TextView tipView = helper.getView(R.id.tvFileReceivingStatus);
                    if (tipView != null && !tipView.getText().equals(msgTip)) {
                        MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvFileReceivingStatus), msgTip, ImageSpan.ALIGN_BOTTOM);
                    }
                }
            } else {
                MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvFileReceivingStatus), UIUtils.getString(R.string.error), ImageSpan.ALIGN_BOTTOM);
            }
        }
    }

    private void setOnClick(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
    }

    private void setStatus(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        if (item != null) {
            MessageTypeEnum msgType = item.getMessageType();
            if (msgType.equals(MessageTypeEnum.image)) {
                BubbleImageView bivPic = helper.getView(R.id.bivPic);
                if (bivPic != null) {
                    bivPic.setProgressVisible(false);
                    bivPic.showShadow(false);
                    helper.setViewVisibility(R.id.llError, View.GONE);
                }
            }
        }
    }

    private void setAvatar(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        if (item.getFrom() != null) {
            Glide.with(mContext).load(AppUtils.getContactAvatarUri(item.getFrom())).centerCrop().into(ivAvatar);
        }
    }

    private void setName(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        if (item.getChatType().equals(ChatTypeEnum.oneonechat)) {
            helper.setViewVisibility(R.id.tvName, View.GONE);
        } else {
            helper.setViewVisibility(R.id.tvName, View.GONE);
        }
    }

    private void setTime(LQRViewHolderForRecyclerView helper, FcMessage item, int position) {
        boolean isSend = !item.isReceived();
        long msgTime = isSend ? item.getMessageSendTime() : item.getMessageReceivedTime();
        if (position > 0) {
            FcMessage preMsg = mData.get(position - 1);
            boolean isSendForPreMsg = false;
            long preMsgTime = isSendForPreMsg ? preMsg.getMessageSendTime() : preMsg.getMessageReceivedTime();
            if (msgTime - preMsgTime > (5 * 60 * 1000)) {
                helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime));
            } else {
                helper.setViewVisibility(R.id.tvTime, View.GONE);
            }
        } else {
            helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime));
        }
    }

    @Override
    public int getItemViewType(int position) {
        FcMessage msg = mData.get(position);
        boolean isSend = !msg.isReceived();
        MessageTypeEnum msgType = msg.getMessageType();
        if (msgType != null) {
            if (msgType.equals(MessageTypeEnum.text)) {
                return isSend ? SEND_TEXT : RECEIVE_TEXT;
            } else if (msgType.equals(MessageTypeEnum.image)) {
                return isSend ? SEND_IMAGE : RECEIVE_IMAGE;
            } else if (msgType.equals(MessageTypeEnum.file)) {
                return isSend ? SEND_FILE : RECEIVE_FILE;
            }
        }
        return UNDEFINE_MSG;
    }
}
