package edu.nccu.cs.nccustock.linechat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.List;
import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.SignIn;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by rabbithsu on 2016/4/27.
 */
public class ChatRoom extends Fragment {
    private MessageAdapter mConversationArrayAdapter;
    private List<XMPPMessage> mdata;

    //public static XMPPChatService mXMPP = null;
    public  static XMPPChatService mXMPP = null;
    public static boolean logged = false;

    private StringBuffer mOutStringBuffer;

    // Layout Views
    ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    boolean XMPPing = false;
    public static String MyName = Build.MODEL;
    public String MyMac = Build.SERIAL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!logged&&mXMPP==null) {
            Intent serverIntent = new Intent(getActivity(),SignIn.class);
            startActivity(serverIntent);
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        setupChat();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_windows, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //mConversationView = (ListView) view.findViewById(R.id.chat);
        mConversationView =(ListView) view.findViewById(R.id.chat);
        mOutEditText = (EditText) view.findViewById(R.id.edit_text_out);
        mSendButton = (Button) view.findViewById(R.id.button_send);

    }

    private void setupChat() {
        Log.d("TAG", "setupChat()");

        //startAdvertising();
        // Initialize the array adapter for the conversation thread

        mXMPP = new XMPPChatService(getActivity(), mHandler, "u", "p");
        mdata = LoadData();

        mConversationArrayAdapter = new MessageAdapter(getActivity(), mdata);

        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationArrayAdapter.Refresh();

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message, MyName);
                }
            }
        });


        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");


    }

    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message, MyName);
            }
            return true;
        }
    };
    private List<XMPPMessage> LoadData(){
        List<XMPPMessage> Messages=new ArrayList<XMPPMessage>();
        //Messages = ;
        //Messages.add(new CheckMessage(CheckMessage.MessageType_To, ""));
        return Messages;
    }

    private void sendMessage(String message, String name) {
        // Check that we're actually connected before trying anything
        if (mXMPP==null) {

            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();
            String namemessage = name+"##"+message+"##"+ts+"##"+MyMac;

                // Get the message bytes and tell the BluetoothChatService to write
                //byte[] send = message.getBytes();
            mXMPP.write(namemessage);
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    /*switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //ArrayAdapter.clear();
                            //mdata.clear();
                            //mConversationArrayAdapter.Refresh();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }*/

                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_XMPP_READ:
                    String read = (String) msg.obj;
                    if(read.split("##").length < 3){
                        read = "unknownXMPP##"+read+"##0##?";
                    }
                    String rread = read.split("##")[1];
                    XMPPMessage ttmp;
                    if(read.split("##")[0].equals(MyName)&&read.split("##")[3].equals(MyMac)){

                    }
                    /*else if(read.split("##")[0].equals(MyName)&&(!MyMac.equals(read.split("##")[3]))) {
                        ttmp = new XMPPMessage(0, Long.parseLong(read.split("##")[2]), XMPPMessage.MessageType_To,
                                read.split("##")[0], rread);
                        mdata.add(ttmp);
                    }*/
                    else{
                        ttmp = new XMPPMessage(0, Long.parseLong(read.split("##")[2]), XMPPMessage.MessageType_From,
                                read.split("##")[0], rread);
                        mdata.add(ttmp);
                    }


                    mConversationArrayAdapter.Refresh();
                    break;
                case Constants.MESSAGE_XMPP_WRITE:


                    String write = (String) msg.obj;
                    String wwrite = write.split("##")[1];
                    mdata.add(new XMPPMessage(0, Long.parseLong(write.split("##")[2]), XMPPMessage.MessageType_To, MyName, wwrite));
                    mConversationArrayAdapter.Refresh();
                    break;
            }
        }
    };

}
