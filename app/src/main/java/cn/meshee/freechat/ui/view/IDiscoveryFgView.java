package cn.meshee.freechat.ui.view;

import android.widget.Button;
import android.widget.ListView;
import java.util.List;
import cn.meshee.fclib.api.network.model.ConnectionState;
import cn.meshee.fclib.api.network.model.NetworkPoint;

public interface IDiscoveryFgView {

    Button getButton();

    ListView getWifiList();

    void onConnectionStateEvent(ConnectionState event);

    void onNetworkPointsUpdated(List<NetworkPoint> networkPoints);

    void switchToIdleMode(String roleName);

    void switchToMemberMode(String roleName);

    void switchToOwnerMode(String roleName);
}
