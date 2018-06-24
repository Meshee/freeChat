package cn.meshee.freechat.ui.presenter;

import android.widget.Button;
import java.util.List;
import java.util.concurrent.TimeUnit;
import cn.meshee.fclib.api.FCClient;
import cn.meshee.fclib.api.Observer;
import cn.meshee.fclib.api.log.TVLog;
import cn.meshee.fclib.api.network.NetworkService;
import cn.meshee.fclib.api.network.NetworkServiceObserve;
import cn.meshee.fclib.api.network.model.ConnectionState;
import cn.meshee.fclib.api.network.model.NetworkPoint;
import cn.meshee.fclib.api.network.model.Role;
import cn.meshee.fclib.api.network.model.RoleEvent;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.ui.view.IDiscoveryFgView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class DiscoveryFgPresenter extends BasePresenter<IDiscoveryFgView> {

    private volatile ConnectionState connectionState = null;

    private Observer<ConnectionState> connectionEventListener = null;

    private Observer<List<NetworkPoint>> networkUpdateListener = null;

    private Observer<RoleEvent> roleEventListener = null;

    public void joinNetwork(ConnectionState state) {
        if (state == null)
            return;
        NetworkService networkService = FCClient.getService(NetworkService.class);
        connectionState = state;
        networkService.joinNetwork(state.getNetworkPoint());
    }

    public ConnectionState getLastState() {
        return connectionState;
    }

    public DiscoveryFgPresenter(BaseActivity context) {
        super(context);
        registerEventListeners();
    }

    public void action() {
        Role publicState = FCClient.getService(NetworkService.class).getRole();
        if (publicState == Role.Idle) {
            FCClient.getService(NetworkService.class).scan();
            updateButtonStatus();
        } else {
            TVLog.log("back to idle from menu");
            FCClient.getService(NetworkService.class).backToIdle();
        }
    }

    private void updateButtonStatus() {
        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {

            @Override
            public void call(Integer integer) {
                Button scanBtn = getView().getButton();
                if (scanBtn != null) {
                    scanBtn.setEnabled(false);
                }
            }
        });
        Observable.just(1).delay(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {

            @Override
            public void call(Integer integer) {
                Button scanBtn = getView().getButton();
                if (scanBtn != null) {
                    scanBtn.setEnabled(true);
                }
            }
        });
    }

    private void registerEventListeners() {
        NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
        this.connectionEventListener = new Observer<ConnectionState>() {

            @Override
            public void onEvent(ConnectionState connectState) {
                TVLog.log(String.format("Network point of %s's connection state is changed to %s", connectState.getNetworkPoint().getNetworkPointId(), connectState.getState()));
                IDiscoveryFgView view = getView();
                if (view != null) {
                    if (connectionState != null && connectionState.getNetworkPoint().getNetworkPointId().equals(connectState.getNetworkPoint().getNetworkPointId())) {
                        connectionState.setState(connectState.getState());
                    }
                    view.onConnectionStateEvent(connectState);
                }
            }
        };
        networkServiceObserve.observeConnectionEvent(connectionEventListener, true);
        this.networkUpdateListener = new Observer<List<NetworkPoint>>() {

            @Override
            public void onEvent(List<NetworkPoint> networkPoints) {
                IDiscoveryFgView view = getView();
                if (view != null) {
                    view.onNetworkPointsUpdated(networkPoints);
                }
            }
        };
        networkServiceObserve.observeNetworkPointsUpdate(this.networkUpdateListener, true);
        this.roleEventListener = new Observer<RoleEvent>() {

            @Override
            public void onEvent(RoleEvent event) {
                IDiscoveryFgView view = getView();
                if (view != null) {
                    switchToNetworkMode(event.getNewRole());
                }
            }
        };
        networkServiceObserve.observeRoleUpdate(this.roleEventListener, true);
    }

    @Override
    public void detachView() {
        super.detachView();
        unregisterEventListeners();
    }

    private void switchToNetworkMode(Role currentRole) {
        if (currentRole != null) {
            if (currentRole.equals(Role.Idle)) {
                getView().switchToIdleMode(Role.Idle.name());
            } else if (currentRole.equals(Role.Member)) {
                getView().switchToMemberMode(currentRole.name());
            } else if (currentRole.equals(Role.Owner)) {
                getView().switchToOwnerMode(Role.Owner.name());
            }
        }
    }

    private void unregisterEventListeners() {
        NetworkServiceObserve networkServiceObserve = FCClient.getService(NetworkServiceObserve.class);
        if (networkServiceObserve != null) {
            if (this.networkUpdateListener != null) {
                networkServiceObserve.observeNetworkPointsUpdate(this.networkUpdateListener, false);
            }
            if (this.connectionEventListener != null) {
                networkServiceObserve.observeConnectionEvent(this.connectionEventListener, false);
            }
            if (this.roleEventListener != null) {
                networkServiceObserve.observeRoleUpdate(this.roleEventListener, false);
            }
        }
    }

    public void initData() {
        NetworkService networkService = FCClient.getService(NetworkService.class);
        switchToNetworkMode(networkService.getRole());
    }
}
