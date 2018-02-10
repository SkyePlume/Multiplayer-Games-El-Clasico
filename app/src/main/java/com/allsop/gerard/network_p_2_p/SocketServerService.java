package com.allsop.gerard.network_p_2_p;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allsop.gerard.network_p_2_p.fragments.DeviceDetailFragment;
import com.allsop.gerard.network_p_2_p.game.PlayerMove;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Edited by Skye  on 09/03/2017.
 */
public class SocketServerService  extends IntentService{
    private static String TAG = "SocketServerService";
    public static final String ACTION_SEND_MESSAGE = "SEND_MESSAGE";
    public static final String EXTRAS_MESSAGE = "MESSAGE";
    public static final String ACTION_START_SERVER = "ACTION_START_SERVER";

    int port = 10051;
    static Boolean myTurn=false;
    Boolean listen=true;
    static String message;
    private String clientMessage;
    private String clientMove;
    private Gson moveObject = new Gson();

    public SocketServerService(String name) {super(name);}
    public SocketServerService() {super("SocketServerService");}

    public static void nextTurn() {myTurn = !myTurn;}

    public static void setMessage(String msg) {message = msg;}

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if(intent.getAction().equals(ACTION_START_SERVER)){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try(ServerSocket dataSocket = new ServerSocket(port);
                    Socket clientSocket = dataSocket.accept();
                        PrintWriter outputStreamWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    )
                    {
                        if(listen) {
                            clientMove = bufferedReader.readLine();
                            if(clientMove != null){
                                PlayerMove move = moveObject.fromJson(clientMove,PlayerMove.class);
                                DeviceDetailFragment.setOpponentMove(move);
                                log(clientMove);
                                listen=false;
                            }
                        }
                        if(!listen) {
                            while (!myTurn){;}
                            outputStreamWriter.print(message + '\n');
                            outputStreamWriter.flush();
                            listen=true;
                            myTurn=false;
                        }
                    }catch (IOException e) {log(e.getMessage());}
                }
            }
        }).start();
    }
}
    private void log(String msg) {
        Log.d(TAG, msg); }

}
