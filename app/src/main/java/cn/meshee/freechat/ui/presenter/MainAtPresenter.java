package cn.meshee.freechat.ui.presenter;

import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.avatar.AvatarService;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.fclib.api.network.NetworkServiceObserve;
import cn.meshee.fclib.api.network.model.NetworkEvent;
import cn.meshee.fclib.api.network.model.Role;
import cn.meshee.fclib.api.network.model.RoleEvent;
import cn.meshee.freechat.ijk.LiveResource;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IMainAtView;
import cn.meshee.freechat.util.AppUtils;

public class MainAtPresenter extends BasePresenter<IMainAtView> {

    private Observer<NetworkEvent> observer = null;

    private Observer<RoleEvent> roleEventObserver = null;

    public MainAtPresenter(BaseActivity context) {
        super(context);
    }

    public void fetchVideoList() {
        LiveResource.getInstance().refreshVideoList(LiveResource.getInstance().getDefaultUrl());
    }

    public void createNetwork() {
        NetworkService networkService = FCClient.getService(NetworkService.class);
        if (networkService != null) {
            NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
            observer = new Observer<NetworkEvent>() {

                @Override
                public void onEvent(NetworkEvent networkEvent) {
                    if (networkEvent == null)
                        return;
                    NetworkEvent.NetworkEventType networkEventType = networkEvent.getEventType();
                    if (networkEventType.equals(NetworkEvent.NetworkEventType.NETWORK_EVENT_CREATE)) {
                        TVLog.log(String.format("network %s created success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()));
                    } else if (networkEventType.equals(NetworkEvent.NetworkEventType.NETWORK_EVENT_DESTROY)) {
                        TVLog.log(String.format("network %s destory success with groupName %s", networkEvent.getNetworkPointId(), networkEvent.getGroupName()));
                    }
                }
            };
            roleEventObserver = new Observer<RoleEvent>() {

                @Override
                public void onEvent(RoleEvent event) {
                    if (event == null)
                        return;
                    Role role = event.getNewRole();
                    IMainAtView view = getView();
                    if ((role == Role.Idle || role == Role.Member) && view != null) {
                        if (view.isDiscoveryFgSelected()) {
                            softScan();
                        }
                    }
                }
            };
            networkServiceObserve.observeNetworkEvent(observer, true);
            networkServiceObserve.observeRoleUpdate(roleEventObserver, true);
            networkService.createNetwork(AppUtils.randomString(8), false);
        }
    }

    public void restartAutoJoinNetwork() {
        FCClient.getService(NetworkService.class).restartAutoJoinNetwork();
    }

    public void stopAutoJoinNetwork() {
        FCClient.getService(NetworkService.class).stopAutoJoinNetwork();
    }

    @Override
    public void attachView(IMainAtView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
        NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
        if (observer != null) {
            networkServiceObserve.observeNetworkEvent(observer, false);
        }
        if (roleEventObserver != null) {
            networkServiceObserve.observeRoleUpdate(roleEventObserver, false);
        }
    }

    public void softScan() {
        if (!FCClient.getService(NetworkService.class).isAutoJoinNetwork()) {
            FCClient.getService(NetworkService.class).softScan();
        }
    }

    public void stopScan() {
        if (!FCClient.getService(NetworkService.class).isAutoJoinNetwork()) {
            FCClient.getService(NetworkService.class).stopScan();
        }
    }

    public boolean isRole4Scan() {
        return FCClient.getService(NetworkService.class).getRole() == Role.Idle || FCClient.getService(NetworkService.class).getRole() == Role.Member;
    }

    public void showLog(MainActivity context) {
        TVLog.showLog(context);
    }

    public void clearLog(MainActivity context) {
        TVLog.clearLog(context);
    }

    public void pushAvatar() {
        AvatarService as = FCClient.getService(AvatarService.class);
        as.pushAvatarToAll();
    }

    public void setDisconnectWhenBackground(boolean disconnectWhenBackground) {
        FCClient.getService(NetworkService.class).setDisconnectWhenBackground(disconnectWhenBackground);
    }
}
