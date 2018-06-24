package cn.meshee.freechat.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.RequestCallback;
import cn.meshee.fclib.api.contact.ContactService;
import cn.meshee.fclib.api.contact.model.Contact;
import cn.meshee.fclib.api.conversation.model.Conversation;
import cn.meshee.fclib.api.file.FileService;
import cn.meshee.fclib.api.file.model.FileIdentity;
import cn.meshee.fclib.api.file.model.FileMessage;
import cn.meshee.fclib.api.file.model.FileProgress;
import cn.meshee.fclib.api.file.model.FileReceiveStatus;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.message.FcMessageBuilder;
import cn.meshee.fclib.api.message.MessageService;
import cn.meshee.fclib.api.message.model.FcMessage;

public class MessageManager extends Observable {

    private static MessageManager instance;

    private final Map<String, ArrayList<FcMessage>> mMessages = new HashMap<>();

    public Observer<List<FcMessage>> getMessageObserver() {
        return messageObserver;
    }

    private Observer<List<FcMessage>> messageObserver = new Observer<List<FcMessage>>() {

        @Override
        public void onEvent(List<FcMessage> fcMessages) {
            if (fcMessages != null) {
                addMessage(fcMessages);
            }
        }
    };

    public Observer<FileProgress> getFileProgressObserver() {
        return fileProgressObserver;
    }

    public Observer<FileMessage> getFileMessageEventObserver() {
        return fileMessageEventObserver;
    }

    public Observer<IOException> getFileSendExceptionObserver() {
        return filesendExceptionObserver;
    }

    private Observer<IOException> filesendExceptionObserver = new Observer<IOException>() {

        @Override
        public void onEvent(IOException ex) {
            ex.printStackTrace();
        }
    };

    private Observer<FileMessage> fileMessageEventObserver = new Observer<FileMessage>() {

        @Override
        public void onEvent(FileMessage fileMessage) {
            if (fileMessage != null) {
                FileIdentity fileIdentity = fileMessage.getFileIdentity();
                if (fileIdentity != null) {
                    ArrayList<FcMessage> fcMessages = getMessage(fileIdentity.getConversationId());
                    if (fcMessages != null) {
                        for (FcMessage fcMessage : fcMessages) {
                            if (fcMessage.getMessageUuid().equals(fileIdentity.getFcMessageId())) {
                                FileMessage fm = fcMessage.getFileMessage();
                                fm.setfTcpHandShake(fileMessage.getfTcpHandShake());
                                fm.setFileStatus(fileMessage.getFileStatus());
                                setChanged();
                                notifyObservers(fileMessage);
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    private Observer<FileProgress> fileProgressObserver = new Observer<FileProgress>() {

        @Override
        public void onEvent(FileProgress fileProgress) {
            if (fileProgress != null) {
                FileIdentity fileIdentity = fileProgress.getFileIdentity();
                if (fileIdentity != null) {
                    ArrayList<FcMessage> fcMessages = getMessage(fileIdentity.getConversationId());
                    if (fcMessages != null) {
                        for (FcMessage fcMessage : fcMessages) {
                            if (fcMessage.getMessageUuid().equals(fileIdentity.getFcMessageId())) {
                                FileMessage fileMessage = fcMessage.getFileMessage();
                                if (fileMessage != null) {
                                    int receivePercent = fileProgress.getPercent();
                                    final FileReceiveStatus fileReceiveStatus = fileMessage.getFileStatus().getFileReceiveStatus();
                                    if (receivePercent == fileReceiveStatus.getReceivePercentage() || receivePercent % 5 != 0)
                                        return;
                                    if (receivePercent > 0) {
                                        fileReceiveStatus.setReceivePercentage(receivePercent);
                                        fileReceiveStatus.setReceiveStatus(FileReceiveStatus.ReceiveStatus.InProgress);
                                    }
                                    if (receivePercent >= 100) {
                                        fileReceiveStatus.setReceiveStatus(FileReceiveStatus.ReceiveStatus.Success);
                                    }
                                    setChanged();
                                    notifyObservers(fileProgress);
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        if (instance == null) {
            instance = new MessageManager();
            instance.init();
        }
        return instance;
    }

    private void init() {
    }

    public void addMessage(List<FcMessage> fcMessages) {
        if (fcMessages != null) {
            for (FcMessage fcMessage : fcMessages) {
                addMessage(fcMessage);
            }
        }
    }

    public void addMessage(FcMessage fcMessage) {
        if (fcMessage != null && fcMessage.getFrom() != null) {
            String convId = fcMessage.getConversationId();
            if (convId != null) {
                addMessage(convId, fcMessage);
            }
        }
    }

    public synchronized void addMessage(String convId, FcMessage fcMessage) {
        if (convId != null) {
            ConversationManager.getInstance().updateConversationTotalCount(convId);
            ArrayList<FcMessage> messages = mMessages.get(fcMessage.getConversationId());
            if (messages == null) {
                messages = new ArrayList<>();
                mMessages.put(fcMessage.getConversationId(), messages);
            }
            messages.add(0, fcMessage);
            setChanged();
            notifyObservers(fcMessage);
        }
    }

    public synchronized void clearMessage() {
        mMessages.clear();
    }

    public ArrayList<FcMessage> getMessage(String convId) {
        return mMessages.get(convId);
    }

    private boolean canAccessFile(final String filePath) {
        if (filePath != null && filePath.trim().length() > 0) {
            File fileToSend = new File(filePath);
            if (fileToSend != null && fileToSend.isFile() && fileToSend.exists() && fileToSend.canRead()) {
                return true;
            }
        }
        return false;
    }

    public FcMessage sendFileMsg(String convId, String filePath) {
        FcMessage fcMessage = FcMessageBuilder.createFileMessage(convId, filePath);
        FCClient.getService(FileService.class).requestSendFile(fcMessage, 60);
        return fcMessage;
    }

    public FcMessage sendImgMsg(String convId, String imageFileSourcePath) {
        FcMessage fcMessage = FcMessageBuilder.createImageMessage(convId, imageFileSourcePath, null);
        FCClient.getService(MessageService.class).sendMessage(fcMessage, new RequestCallback<FcMessage>() {

            @Override
            public void onSuccess(FcMessage fcMessage) {
            }

            @Override
            public void onFailed(int code) {
            }

            @Override
            public void onException(Exception ex) {
                TVLog.log(String.format("Exception: %s", ex));
            }
        });
        return fcMessage;
    }

    public FcMessage sendTextMsg(String convId, String messageText) {
        FcMessage fcMessage = FcMessageBuilder.createTextMessage(convId, messageText);
        FCClient.getService(MessageService.class).sendMessage(fcMessage, new RequestCallback<FcMessage>() {

            @Override
            public void onSuccess(FcMessage fcMessage) {
            }

            @Override
            public void onFailed(int code) {
            }

            @Override
            public void onException(Exception ex) {
                TVLog.log(String.format("Exception: %s", ex));
            }
        });
        return fcMessage;
    }

    public synchronized List<FcMessage> getMessage(String mSessionId, String messageUuid, int mMessageCount) {
        List<FcMessage> messages = getMessage(mSessionId);
        if (messages != null && messages.size() > 0 && mMessageCount > 0) {
            ArrayList<FcMessage> fcMessages = new ArrayList<>();
            boolean found = false;
            int remaining = mMessageCount;
            if (messageUuid == null || messageUuid.trim().length() <= 0) {
                found = true;
            }
            for (int i = 0; i < messages.size(); i++) {
                if (remaining < 0) {
                    break;
                }
                if (found) {
                    fcMessages.add(messages.get(i));
                    remaining--;
                } else {
                    if (messages.get(i).getMessageUuid().equals(messageUuid)) {
                        found = true;
                    }
                }
            }
            return fcMessages;
        }
        return null;
    }

    public void ackReceiveFile(FcMessage message) {
        FCClient.getService(FileService.class).ackReceiveFile(message);
    }
}
