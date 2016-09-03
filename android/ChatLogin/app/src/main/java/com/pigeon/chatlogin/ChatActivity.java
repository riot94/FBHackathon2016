package com.pigeon.chatlogin;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private WebSocketClient mWebSocketClient;

    private ScrollView chatPanel;

    private ChatUser myChatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatPanel = (ScrollView)findViewById(R.id.scrollView);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            myChatUser = (ChatUser) b.getSerializable(getString(R.string.user_key_name));

        // connectWebSocket();

        final EditText editText = (EditText)findViewById(R.id.editText);
        ImageButton sendButton = (ImageButton)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SendMessage().execute(editText.getText().toString());
                editText.setText("");
            }
        });
    }

    private class SendMessage extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            try {
                mWebSocketClient.send(params[0]);
            }
            catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Void... progress) {

        }

        protected void onPostExecute(Void result) {
        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://172.22.119.14:8080/pigeon");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("user " + myChatUser.getId());
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = new TextView(chatPanel.getContext());
                        chatPanel.addView(tv);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();
    }
}
