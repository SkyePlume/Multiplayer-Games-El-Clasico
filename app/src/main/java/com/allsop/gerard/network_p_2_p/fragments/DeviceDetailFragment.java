package com.allsop.gerard.network_p_2_p.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allsop.gerard.network_p_2_p.DataSocketManager;
import com.allsop.gerard.network_p_2_p.DeviceActionListener;
import com.allsop.gerard.network_p_2_p.R;
import com.allsop.gerard.network_p_2_p.SocketServerService;
import com.allsop.gerard.network_p_2_p.game.PlayerMove;
import com.google.gson.Gson;

/**
 * Edited by Skye on 10/02/2017.
 */

public class DeviceDetailFragment extends Fragment
        implements WifiP2pManager.ConnectionInfoListener {
    private View mContentView = null;
    private WifiP2pInfo info;
    private ProgressDialog progressDialog = null;
    private static int port = 10051;
    private static boolean server=false;
    private Gson moveObject = new Gson();
    public static DeviceDetailFragment deviceDetailFragment;

    public void sendClientMove(String nextMove) {
        Intent serviceIntent = new Intent(getActivity(), DataSocketManager.class);
        serviceIntent.setAction(DataSocketManager.ACTION_SEND_MESSAGE);
        serviceIntent.putExtra(DataSocketManager.EXTRAS_MESSAGE, nextMove);
        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(DataSocketManager.EXTRAS_GROUP_OWNER_PORT, port);
        getActivity().startService(serviceIntent);
    }

    public void sendServerMove(String nextMove) {
        SocketServerService.setMessage(nextMove);
        SocketServerService.nextTurn();
    }

    private String moveToJson(PlayerMove move){
        String s = moveObject.toJson(move);
        return s;
    }

    public static void setOpponentMove(PlayerMove playerMove){
        Activity activity = deviceDetailFragment.getActivity();
        FragmentManager fManager = activity.getFragmentManager();
        final OXOFragment oxoFragment = (OXOFragment)
                fManager.findFragmentById(R.id.fragment_centre);
        final PlayerMove nextMove = playerMove;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oxoFragment.handleOpponentMove(nextMove);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.setVisibility(View.INVISIBLE);
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });
        mContentView.findViewById(R.id.btn_playAgain).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OXOFragment.gameOverRoutine();
                    }
                });
        deviceDetailFragment = this;
        return mContentView;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        if (info.groupFormed && info.isGroupOwner) {
            server=true;
            OXOFragment.enableInterface(false);
            startSocketServerService();
        } else if (info.groupFormed && !info.isGroupOwner) {
            server=false;
        }
    }

    //If group owner must start server socket code....
    public void startSocketServerService(){
        Intent serviceIntent = new Intent(getActivity(), SocketServerService.class);
        serviceIntent.setAction(SocketServerService.ACTION_START_SERVER);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static boolean isServer(){
        return server;
    }

    private void log(String msg){
        Log.d(this.getClass().toString(), msg);
    }
}