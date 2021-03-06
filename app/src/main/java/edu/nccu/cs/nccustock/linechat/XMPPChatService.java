package edu.nccu.cs.nccustock.linechat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by nccu_dct on 15/8/29.
 */
public class XMPPChatService extends Service{
    public static boolean log = false;
    public static boolean success = false;

    //online
    private boolean Online = false;




    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }



    private static final String TAG = "XMPPService";
    private AbstractXMPPConnection connection;
    private final Handler mHandler;
    private Chat XMPPchat;



    //XMPP
    public static final String HOST = "140.119.164.18";
    public static final int PORT = 5222;
    public static final String SERVICE = "140.119.164.18";
    public static String USERNAME;
    public static String PASSWORD;
    private ArrayList<String> messages = new ArrayList<String>();



    public XMPPChatService(Context context, Handler handler, String username, String pw) {
        mHandler = handler;
        XMPPchat = null;
        USERNAME = username;
        PASSWORD = pw;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                // Create a connection
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                config.setServiceName(SERVICE);
                config.setHost(HOST);
                config.setPort(PORT);
                //config.setUsernameAndPassword(USERNAME, PASSWORD);
                //config.setDebuggerEnabled(true);
                config.setCompressionEnabled(false);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);


                //config.setSASLAuthenticationEnabled(false);
                connection = new XMPPTCPConnection(config.build());

                try {
                    connection.connect();
                    Log.d("XMPPChatDemoActivity",
                            "Connected to " + connection.getHost());
                } catch (Exception ex) {
                    Log.d("XMPPChatDemoActivity", "Failed to connect to "
                            + connection.getHost());
                    Log.d("XMPPChatDemoActivity", ex.toString());
                    //connection = null;
                }
                try {
                    // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                    connection.login();
                    Log.d("XMPPChatDemoActivity",
                            "Logged in as " + connection.getUser());

                    // Set the status to available
                    Presence presence = new Presence(Presence.Type.available);
                    //Presence.Type.unavailable
                    connection.sendStanza(presence);
                    //setReceive(connection);

                    //chat receiver
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    chatManager.addChatListener(
                            new ChatManagerListener() {
                                @Override
                                public void chatCreated(Chat chat, boolean createdLocally)
                                {
                                    if (!createdLocally)
                                        chat.addMessageListener(new ChatMessageListener(){
                                            @Override
                                            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                                                Log.d("XMPPChatDemoActivity", "Receive: " + message.getBody());
                                                mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, message.getBody().length(), -1, message.getBody())
                                                        .sendToTarget();
                                            }

                                        });;
                                }
                            });
                } catch (Exception ex) {
                    Log.d("XMPPChatDemoActivity", "Failed to log in as "
                            + USERNAME);
                    Log.d("XMPPChatDemoActivity", ex.toString());
                    connection = null;
                }

                //chatroomtest
                startchat("all@broadcast.140.119.164.18");

                //dialog.dismiss();
            }
        });
        t.start();
    }

    public AbstractXMPPConnection getConnection(){
        return this.connection;
    }

    public void write(String out) {
        // Create temporary object

        // Perform the write unsynchronized
        try {
            XMPPchat.sendMessage(out);
            mHandler.obtainMessage(Constants.MESSAGE_XMPP_WRITE, -1, -1, out)
                    .sendToTarget();
        }
        catch (Exception ex){
            Log.d(TAG, "Send message failed.");
        }
    }


    public void startchat(String account){
        Log.d(TAG, account);
        new XMPPThread(account);
    }


    private class XMPPThread{
        private final String USRID;


        public XMPPThread(String account) {
            USRID = account;
            Log.d(TAG, "Chat to user: "+USRID);
            Thread m = new Thread(new Runnable() {
                @Override
                public void run(){
                    //chat test
                    Chat chat = ChatManager.getInstanceFor(connection).createChat(USRID, new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                            Log.d("XMPPChatDemoActivity", "Receive: " + message.getBody());
                            mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, message.getBody().length(), -1, message.getBody())
                                    .sendToTarget();
                        }

                    });
                    XMPPchat = chat;

                }
            });
            m.start();

        }

    }
}
