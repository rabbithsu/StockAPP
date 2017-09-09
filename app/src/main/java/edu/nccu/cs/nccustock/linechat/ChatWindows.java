package edu.nccu.cs.nccustock.linechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.nccu.cs.nccustock.R;

/**
 * Created by nccu_dct on 15/10/13.
 */
public class ChatWindows extends Activity {

    //private Handler mshandler;
    public static List<XMPPMessage> items = new ArrayList<>();
    public MessageAdapter itemsAdapter;
    private String Broker = "broker@140.119.164.18";//"broker@140.119.164.5";
    // UI set
    ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;


    @Override
    protected  void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_windows);
        Intent intent = this.getIntent();
        Broker = intent.getStringExtra("account");
        mConversationView =(ListView) findViewById(R.id.chat);
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mSendButton = (Button) findViewById(R.id.button_send);


    }

    @Override
    protected void onStart(){
        super.onStart();
        setup();
        ChatFragment.mXMPP.setOnline();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected  void onStop(){
        super.onStop();
        ChatFragment.mXMPP.setOffline();
    }

    private void setup(){
        //items = LoadData();
        mOutEditText.setOnEditorActionListener(mWriteListener);
        itemsAdapter = new MessageAdapter(ChatWindows.this, ChatFragment.mXMPP.getContent(Broker));
        mConversationView.setAdapter(itemsAdapter);
        //ChatFragment.mXMPP.setCurrentAdapter(itemsAdapter);
        ChatFragment.mXMPP.setHandler(mHandler);
        ChatFragment.mXMPP.startchat(Broker);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget

                TextView textView = (TextView) findViewById(R.id.edit_text_out);
                String message = textView.getText().toString();
                sendMessage(message, "TestUser");
            }
        });

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_XMPP_READ:
                    String read = (String) msg.obj;
                    itemsAdapter.Refresh();

                    break;
                case Constants.MESSAGE_XMPP_WRITE:
                    String write = (String) msg.obj;
                    itemsAdapter.Refresh();
                    break;
            }
        }
    };
    private List<XMPPMessage> LoadData(){
        return null;
    }


    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message, "TestUser");
            }
            return true;
        }
    };

    private void sendMessage(String message, String name) {
        // Check that there's actually something to send
        if (message.length() > 0) {
            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();
            String namemessage = message;
            //itemDB.insert(new CheckMessage(0, tsLong, CheckMessage.MessageType_From, name, message));
            ChatFragment.mXMPP.write(Broker, message);

            mOutEditText.setText("");
        }
    }
}
