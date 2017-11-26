package com.example.loggin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.codebutler.android_websockets.WebSocketClient;

import com.example.loggin.other.WsMsg;
import com.google.gson.Gson;

import java.net.URI;
import java.util.Locale;

import com.example.loggin.other.Message;
import com.example.loggin.other.WsConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class MainActivity extends Activity {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final Gson jsonProcessor = new Gson();
    private Button btnSend;
    private WebSocketClient client;


    // Client user
    private String user = null;
    int random = (int)Math.floor((Math.random() * 100000) + 1);
    private String signalcarrier = "" + random;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button) findViewById(R.id.btn_aceptar);

        // Getting the person user from previous screen
        Intent i = getIntent();
        user = i.getStringExtra("user");

        //Broadcast to activate the button
        BroadcastReceiver br = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                btnSend.setEnabled(true);
                playBeep();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.broadcast.CORRECT_USER");
        this.registerReceiver(br, filter);

        //button action to send the confirmation messge
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                WsMsg accept = new WsMsg(user, signalcarrier);
                Message msgAccept = new Message("WsMsgAccept", accept);
                String jsonMessage = jsonProcessor.toJson(msgAccept);

                // Sending message to web socket server
                sendMessageToServer(jsonMessage);
            }
        });


        /**
         * Creating web socket client. This will have callback methods
         * */

        client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET + signalcarrier + "/" + user), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {

            }

            /**
             * On receiving the message from web socket server
             * */
            @Override
            public void onMessage(String message) {

                Log.d(TAG, String.format("Got string message! %s", message));
                parseMessage(message);

            }

            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message! %s",
                        bytesToHex(data)));

                // Message will be in JSON format
                parseMessage(bytesToHex(data));
            }

            /**
             * Called when the connection is terminated
             * */
            @Override
            public void onDisconnect(int code, String reason) {

                String message = String.format(Locale.US,
                        "Disconnected! Code: %d Reason: %s", code, reason);

                showToast(message);


            }
            /**
             * Called on error
             * */
            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error! : " + error);

                showToast("Error! : " + error);
            }

        }, null);

        client.connect();
    }

    /**
     * Method to send message to web socket server
     * */
    private void sendMessageToServer(String message) {
        if (client != null && client.isConnected()) {
            client.send(message);
        }
    }

    /**
     * Parsing the message
     * */

    private void parseMessage(final String msg) {

        JsonParser parser = new JsonParser();
        JsonElement jse = parser.parse(msg);
        if (!jse.isJsonObject()) {
            throw new RuntimeException("Mensaje recibido no es un JsonObject");
        }

        JsonObject jso = jse.getAsJsonObject();
        String typeOfMessage = jso.get("type").getAsString();

        Gson gson = new Gson();
        JsonElement content;

        if (typeOfMessage.equals("WsMsgRequest")) {

            content = jso.get("object");
            WsMsg message = gson.fromJson(content, WsMsg.class);

            if (message.getUsername().equals(user)) {

                Intent intent = new Intent();
                intent.setAction("com.example.broadcast.CORRECT_USER");
                sendBroadcast(intent);
            }
        }
    }


        @Override
        protected void onDestroy() {
            super.onDestroy();

            if(client != null & client.isConnected()){
                client.disconnect();
            }
        }

    /**
     * Appending message to list view
     * */


    private void showToast(final String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Plays device's default notification sound
     * */
    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}

