package com.allsop.gerard.network_p_2_p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.allsop.gerard.network_p_2_p.fragments.DeviceDetailFragment;

/**
 * Edited by Skye  on 07/02/2017.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiDirectActivity activity;
    private WifiP2pManager manager;
    private Channel channel;


    public WifiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WifiDirectActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Requesting available peers from the wifi p2p manager.
            // Asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            if (manager != null) {
                manager.requestPeers(channel, (WifiP2pManager.PeerListListener)
                        activity.getFragmentManager().findFragmentById(R.id.fragment_left));
            }
            Log.d("WifiDirectActivity.TAG", "P2P peers changed");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if(manager == null){
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                DeviceDetailFragment fragment = (DeviceDetailFragment) activity.getFragmentManager().findFragmentById(R.id.fragment_right);
                manager.requestConnectionInfo(channel, fragment);
            } else {
                activity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // we'll update our display fragments here when we've created them.
        }
    }
}
