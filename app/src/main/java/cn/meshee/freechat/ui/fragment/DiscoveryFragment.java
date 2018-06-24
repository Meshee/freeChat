package cn.meshee.freechat.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import butterknife.BindView;
import cn.meshee.fclib.api.network.model.ConnectionState;
import cn.meshee.fclib.api.network.model.NetworkPoint;
import cn.meshee.freechat.R;
import cn.meshee.freechat.ui.activity.MainActivity;
import cn.meshee.freechat.ui.base.BaseFragment;
import cn.meshee.freechat.ui.presenter.DiscoveryFgPresenter;
import cn.meshee.freechat.ui.view.IDiscoveryFgView;
import cn.meshee.freechat.util.UIUtils;

public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter> implements IDiscoveryFgView {

    @BindView(R.id.scanWifiButton)
    Button button;

    @BindView(R.id.wifi_list)
    ListView wifiListView;

    @BindView(R.id.emptyWifiList)
    TextView wifiListEmptyView;

    private WifiListAdapter wifiListAdapter;

    @Override
    public void initData() {
        super.initData();
        mPresenter.initData();
    }

    @Override
    public void initListener() {
        button.setOnClickListener(v -> {
            mPresenter.action();
        });
        wifiListView.setOnItemClickListener((parent, view, position, id) -> {
            ConnectionState state = getDataItemByPosition(position);
            mPresenter.joinNetwork(state);
        });
        wifiListAdapter = new WifiListAdapter(getContext(), null);
        wifiListView.setEmptyView(wifiListEmptyView);
    }

    private ConnectionState getDataItemByPosition(int position) {
        return (ConnectionState) wifiListAdapter.getItem(position);
    }

    @Override
    protected DiscoveryFgPresenter createPresenter() {
        return new DiscoveryFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_discovery;
    }

    public Button getButton() {
        return button;
    }

    @Override
    public ListView getWifiList() {
        return wifiListView;
    }

    @Override
    public void onConnectionStateEvent(ConnectionState event) {
        if (wifiListView != null && wifiListAdapter != null) {
            wifiListAdapter.updateListItemStates(event.getNetworkPoint().getNetworkPointId(), event.getState());
        }
    }

    @Override
    public void onNetworkPointsUpdated(List<NetworkPoint> networkPoints) {
        if (wifiListView != null && wifiListAdapter != null) {
            wifiListView.setAdapter(wifiListAdapter);
            wifiListAdapter.updateList(buildWifiList(networkPoints));
        }
    }

    @Override
    public void switchToIdleMode(String roleName) {
        if (button != null) {
            button.setVisibility(View.VISIBLE);
            button.setText(String.format("%s (%s)", UIUtils.getString(R.string.scan_network), UIUtils.getString(R.string.idle_state)));
            wifiListEmptyView.setText(UIUtils.getString(R.string.no_network));
        }
        if (wifiListView != null) {
            wifiListView.setAdapter(wifiListAdapter);
            wifiListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void switchToMemberMode(String roleName) {
        if (wifiListView != null) {
            wifiListView.setAdapter(wifiListAdapter);
        }
        if (button != null) {
            if (!button.isEnabled()) {
                button.setEnabled(true);
            }
            button.setText(String.format("%s (%s)", UIUtils.getString(R.string.back_to_idle), UIUtils.getString(R.string.member_state)));
        }
    }

    @Override
    public void switchToOwnerMode(String roleName) {
        if (wifiListView != null) {
            wifiListView.setAdapter(null);
        }
        if (button != null) {
            if (!button.isEnabled()) {
                button.setEnabled(true);
            }
            wifiListEmptyView.setText(UIUtils.getString(R.string.network_created));
            button.setText(String.format("%s (%s)", UIUtils.getString(R.string.back_to_idle), UIUtils.getString(R.string.owner_state)));
        }
    }

    private List<ConnectionState> buildWifiList(List<NetworkPoint> networkPoints) {
        List<ConnectionState> list = new ArrayList<>();
        for (NetworkPoint networkPoint : networkPoints) {
            ConnectionState state = ConnectionState.build(networkPoint);
            ConnectionState lastState = mPresenter.getLastState();
            if (lastState != null && lastState.getNetworkPoint() != null && lastState.getNetworkPoint().getNetworkPointId() != null && lastState.getNetworkPoint().getNetworkPointId().equals(networkPoint.getNetworkPointId())) {
                state.setState(lastState.getState());
            }
            list.add(state);
        }
        return list;
    }

    public class WifiListAdapter extends BaseAdapter {

        public WifiListAdapter(Context context, List<ConnectionState> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        final LayoutInflater inflater;

        List<ConnectionState> list;

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            ConnectionState state = null;
            if (position < getCount()) {
                state = list.get(position);
            }
            return state;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);
            ConnectionState state = list.get(position);
            NetworkPoint config = state.getNetworkPoint();
            TextView textView = (TextView) view.findViewById(R.id.textView);
            String groupName = config.getGroupName();
            UUID groupUuid = config.getGroupId();
            String groupIndex = null;
            if (groupUuid != null) {
                groupIndex = groupUuid.toString();
            }
            if (groupName == null) {
                groupName = "NA";
            }
            if (groupIndex == null) {
                groupIndex = "NA";
            }
            String role = config.getNetworkRole().name();
            String text = config.getNetworkPointId() + ":" + role + ":" + groupIndex.substring(0, Math.min(4, groupIndex.length())) + ":" + groupName + ":" + config.getFrequency();
            textView.setText(text);
            TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
            signalStrenth.setText(String.valueOf(config.getRssi()));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            ProgressBar pbar = (ProgressBar) view.findViewById(R.id.progress);
            if (state.getState().equals(ConnectionState.State.CONNECTING)) {
                pbar.setVisibility(View.VISIBLE);
            } else if (state.getState().equals(ConnectionState.State.CONNECTED)) {
                pbar.setVisibility(View.GONE);
            } else {
                pbar.setVisibility(View.GONE);
            }
            return view;
        }

        public void updateList(List<ConnectionState> wifiConfigs) {
            this.list = wifiConfigs;
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }

        public void updateListItemStates(String ssid, ConnectionState.State state) {
            if (list != null) {
                for (ConnectionState item : list) {
                    if (item.getNetworkPoint().getNetworkPointId().equals(ssid)) {
                        item.setState(state);
                    }
                }
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }
}
