package edu.nccu.cs.nccustock.linechat;

import android.content.Context;
import android.widget.Toast;

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
import android.os.Handler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import android.util.Log;


/**
 * Created by nccu_dct on 15/8/29.
 */
public class XMPPService extends Service{
    private final Handler mshandler;
    private final MessageHandler mshandleThread;

    private static final String TAG = "XMPPService";
    public static boolean log = false;
    public static boolean success = false;
    private AbstractXMPPConnection connection;
    private Chat XMPPchat;
    private MessageAdapter ChatAdapter;
    //public static List<XMPPUser> UserItem = new ArrayList<>();
    //public static List<XMPPMessage> items;
    private Handler mHandler = null;

    //online
    private boolean Online = false;




    //XMPP Inform
    public static final String HOST = "140.119.164.18";
    public static final int PORT = 5222;
    public static final String SERVICE = "140.119.164.18";
    public static String USERNAME;
    public static String PASSWORD;
    private ArrayList<String> messages = new ArrayList<String>();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }



    public XMPPService(final Context context, String username, String pw) {
        mshandleThread = new MessageHandler(context);
        mshandleThread.start();
        mshandler = mshandleThread.getHandler();
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
                config.setUsernameAndPassword(USERNAME, PASSWORD);
                //config.setDebuggerEnabled(true);
                config.setCompressionEnabled(false);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);


                //config.setSASLAuthenticationEnabled(false);
                connection = new XMPPTCPConnection(config.build());

                try {
                    connection.connect();
                    Log.e("XMPPChatDemoActivity", "Connect to " + connection.getHost());


                    //Toast.makeText(context, "try.", Toast.LENGTH_SHORT).show();
                    //Log.d("XMPPChatDemoActivity",
                    //        "Connected to " + connection.getHost());
                } catch (Exception ex) {
                    connection = null;
                    success = false;
                    log = true;
                    Log.e("XMPPChatDemoActivity", "Failed to connect to " + connection.getHost());


                    return;

                    //Log.d("XMPPChatDemoActivity", ex.toString());
                    //connection = null;
                }
                try {
                    // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                    connection.login(USERNAME, PASSWORD);
                    //connection.login();
                    //Log.d("XMPPChatDemoActivity",
                    //        "Logged in as " + connection.getUser());

                    // Set the status to available
                    Presence presence = new Presence(Presence.Type.available);
                    connection.sendStanza(presence);

                    //setReceive(connection);
                    //Presence.Type.


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
                                                //boolean exist = false;
                                                //Log.d("XMPPChatDemoAuto", "Receive: " + message.getBody());
                                                /*for(XMPPUser uitem : UserItem) {
                                                    if(uitem.getName() == message.getFrom()){
                                                        uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, "broker", message.getBody()));
                                                        exist = true;
                                                    }
                                                }
                                                if(!exist){
                                                    items = new ArrayList<>();
                                                    items.add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, "broker", message.getBody()));
                                                    UserItem.add(new XMPPUser(0, message.getFrom(), items));
                                                }*/
                                                String name = "broker@140.119.164.18";
                                                String content = message.getBody();
                                                if(content!= null){
                                                    mshandler.obtainMessage(Constants.MESSAGE_XMPP_READ, content.length(), -1, content)
                                                        .sendToTarget();
                                                    if(mHandler != null){
                                                        mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, "".length(), -1, "")
                                                            .sendToTarget();
                                                    }
                                                }
                                                //ChatAdapter.Refresh();

                                            }

                                        });;
                                }
                            });
                    success = true;
                    log = true;
                } catch (Exception ex) {
                    //Log.d("XMPPChatDemoActivity", "Failed to log in as "
                    //        + USERNAME);
                    //Log.d("XMPPChatDemoActivity", ex.toString());
                    connection = null;
                    success = false;
                    log = true;
                }

                //dialog.dismiss();
            }
        });
        t.start();
    }

    public AbstractXMPPConnection getConnection(){
        return this.connection;
    }

    public void write(String n, String out) {
        // Create temporary object
        //boolean exist = false;
        String name = n;
        String content = n + "##" + out;
        // Perform the write unsynchronized
        try {
            XMPPchat.sendMessage(ChatFragment.XMPPname+"@140.119.164.18##"+out);

            mshandler.obtainMessage(Constants.MESSAGE_XMPP_WRITE, content.length(), -1, content)
                    .sendToTarget();
            mHandler.obtainMessage(Constants.MESSAGE_XMPP_WRITE, "".length(), -1, "")
                    .sendToTarget();
            //ChatAdapter.Refresh();

        }
        catch (Exception ex){
            //Log.d(TAG, "Send message failed.");
        }
    }

    public void Groupwrite(String n, String out) {
        // Create temporary object
        //boolean exist = false;
        String content = n + "##" + out;
        // Perform the write unsynchronized
        try {
            XMPPchat.sendMessage(ChatFragment.XMPPname+"@140.119.164.18##"+out);

            mshandler.obtainMessage(Constants.MESSAGE_XMPP_GROUPWRITE, content.length(), -1, content)
                    .sendToTarget();
            mHandler.obtainMessage(Constants.MESSAGE_XMPP_WRITE, "".length(), -1, "")
                    .sendToTarget();
            //ChatAdapter.Refresh();

        }
        catch (Exception ex){
            //Log.d(TAG, "Send message failed.");
        }
    }

    public void startchat(String account){
       // Log.d(TAG, account);
        new XMPPThread(account);
    }


    private class XMPPThread{
        private final String USRID;


        public XMPPThread(String account) {
            //USRID = account;
            USRID = account;//"broker@140.119.164.5";
            List<XMPPMessage> messageitem;
            //Log.d(TAG, "Chat to user: "+USRID);
            Thread m = new Thread(new Runnable() {
                @Override
                public void run(){
                //chat test
                    Chat chat = ChatManager.getInstanceFor(connection).createChat(USRID, new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                            //Log.d("XMPPChatDemoActivity", "Receive: " + message.getBody());
                            /*boolean exist = false;
                            for(XMPPUser uitem : UserItem){
                                if (uitem.getName() == USRID) {
                                    uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, "broker", message.getBody()));
                                    exist = true;
                                }
                            }
                            if(!exist){
                                items = new ArrayList<>();
                                items.add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, "broker", message.getBody()));
                                UserItem.add(new XMPPUser(0, USRID, items));
                            }*/
                            String content = message.getBody();
                            mshandler.obtainMessage(Constants.MESSAGE_XMPP_READ, content.length(), -1, content)
                                    .sendToTarget();
                            mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, "".length(), -1, "")
                                    .sendToTarget();
                            //ChatAdapter.Refresh();

                            }

                });
                XMPPchat = chat;

                }
            });
            m.start();

        }

    }

    public void setCurrentAdapter(MessageAdapter item){
        ChatAdapter = item;
    }

    public void setHandler(Handler mh){
        mHandler = mh;
    }

    public List<XMPPMessage> getContent(String n){

        return mshandleThread.getContent(n);
    }

    public String getRoster(){
        Roster roster = Roster.getInstanceFor(connection);
        String result = "";
        try{
            if (!roster.isLoaded())
                roster.reloadAndWait();
        }
        catch (Exception ex){

        }
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
            //System.out.println(entry);
            //Log.d("XMPPChatDemoActivity", "USER:  "
            //        + entry.getUser());
            //Toast.makeText(getActivity(), entry.getName(), Toast.LENGTH_SHORT).show();/
            result += entry.getUser() + "\n" + entry.getName() +";";

        }
        return result;
    }

    public void setOnline(){
        Online = true;
    }

    public void setOffline(){
        Online = false;
    }
    public boolean getOnline(){
        return Online;
    }
}
