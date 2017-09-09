package edu.nccu.cs.nccustock.linechat;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import edu.nccu.cs.nccustock.R;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import android.app.PendingIntent;
import android.content.Intent;
import android.app.TaskStackBuilder;
import edu.nccu.cs.nccustock.MainActivity;


/**
 * Created by nccu_dct on 15/11/5.
 */
public class MessageHandler extends Thread{

    public static List<XMPPUser> UserItem = new ArrayList<>();
    public static List<XMPPMessage> items;
    private static XMPPUser BroadCast = new XMPPUser(0, "Broadcast");
    private static XMPPUser Annonymous = new XMPPUser(0, "Annonymous");
    private static XMPPUser C1 = new XMPPUser(0, "customer1@140.119.164.18");
    private static XMPPUser C2 = new XMPPUser(0, "customer2@140.119.164.18");

    static Context C;
    NotificationCompat.Builder builder ;//=
            //new NotificationCompat.Builder(C);
    //static Notification notification;
    static NotificationManager manager; //= (NotificationManager) C.getSystemService(Context.NOTIFICATION_SERVICE);

    /*@Override
    public synchronized void start(){
        super.start();
        manager = (NotificationManager) C.getSystemService(Context.NOTIFICATION_SERVICE);
    }*/


    public void run() {
        UserItem.add(BroadCast);
        //handle not format message
        UserItem.add(Annonymous);
        UserItem.add(C1);
        UserItem.add(C2);

        builder =
                new NotificationCompat.Builder(C);

        manager = (NotificationManager) C.getSystemService(Context.NOTIFICATION_SERVICE);
        /*
        builder.setSmallIcon(R.drawable.notify_small_icon)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Basic Notification")
                .setContentText("Demo for basic notification control.");
                //.setContentInfo("3");
        // 建立通知物件
        notification = builder.build();
        // 使用CUSTOM_EFFECT_ID為編號發出通知*/
        //manager.notify(1, notification);
        //Log.e("LOGE", "notification");
    }

    public MessageHandler(Context context)
    {
        C = context;
    }


    private static final Handler msHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //message format: name@140.xxx.xxx.xxx##message
                case Constants.MESSAGE_XMPP_READ:
                    String read = (String) msg.obj;
                    String[] mread = read.split("##");

                    //notification
                    String notiname="";
                    String noticontent="";

                    //handle not format message
                    if(mread.length != 2 ){
                        Annonymous.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, "Annonymous", read));
                        notiname = "Annoymous";
                        noticontent = read;
                        break;

                    }

                    boolean existRead = false;

                    for(XMPPUser uitem : UserItem) {
                        if(uitem.getName().equals(mread[0])){
                            if(mread[0].split("@")[0].equals(ChatFragment.XMPPname)) {
                                uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, mread[0].split("@")[0], mread[1]));
                            }else {
                                uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, mread[0].split("@")[0], mread[1]));
                            }
                            existRead = true;
                            notiname = mread[0].split("@")[0];
                            noticontent = mread[1];
                            //break;
                        }

                        if(uitem.getName().equals("Broadcast")){
                            if(mread[0].equals("broker@140.119.164.18")) {

                            }
                            else {  //  continue;
                                uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, mread[0].split("@")[0], mread[1]));
                            }
                        }
                    }
                    if(!existRead){
                        items = new ArrayList<>();
                        items.add(new XMPPMessage(0, 0, XMPPMessage.MessageType_From, mread[0].split("@")[0], mread[1]));
                        UserItem.add(new XMPPUser(0, read.split("##")[0], items));
                        notiname = mread[0].split("@")[0];
                        noticontent = mread[1];
                    }

                    //notification
                    if(!ChatFragment.mXMPP.getOnline()){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(C);

                        Intent serverIntent = new Intent(C, ChatWindows.class);
                        serverIntent.putExtra("account", mread[0]);
                        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
                        PendingIntent pendingIntent = PendingIntent.getActivity(C, 1, serverIntent, flags); // 取得PendingIntent


                        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(C); // 建立TaskStackBuilder
                        stackBuilder.addParentStack(ChatWindows.class); // 加入目前要啟動的Activity，這個方法會將這個Activity的所有上層的Activity(Parents)都加到堆疊中
                        stackBuilder.addNextIntent(serverIntent); // 加入啟動Activity的Intent
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(1, flags);*/
                        builder.setSmallIcon(R.drawable.notify_small_icon)
                                .setWhen(System.currentTimeMillis())
                                .setContentTitle(mread[0].split("@")[0])
                                .setContentText(mread[1])
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                        long[] vibrate_effect =
                                {1000,500};
                        builder.setVibrate(vibrate_effect);

                        Notification notification = builder.build();


                        //final int notifyID = 1; // 通知的識別號碼
                        //final int requestCode = notifyID; // PendingIntent的Request Code
                        //Intent intent ;//= getIntent(); // 目前Activity的Intent

                        //final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
                        //final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher).setContentTitle("內容標題").setContentText("內容文字").setContentIntent(pendingIntent).build(); // 建立通知
                        //notificationManager.notify(notifyID, notification); // 發送通知*/
                        // 使用CUSTOM_EFFECT_ID為編號發出通知
                        manager.notify(1, notification);
                    }

                    break;

                case Constants.MESSAGE_XMPP_WRITE:
                    String write = (String) msg.obj;
                    String[] mwrite = write.split("##");
                    boolean existWrite = false;

                    for(XMPPUser uitem : UserItem) {
                        if(uitem.getName().equals(mwrite[0])) {
                            uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, mwrite[0].split("@")[0], mwrite[1]));
                            existWrite = true;
                            break;
                        }
                    }
                    if(!existWrite){
                        items = new ArrayList<>();
                        items.add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, mwrite[0].split("@")[0], mwrite[1]));
                        UserItem.add(new XMPPUser(0, write.split("##")[0], items));
                    }

                    break;

                case Constants.MESSAGE_XMPP_GROUPWRITE:
                    String gwrite = (String) msg.obj;
                    String[] gmwrite = gwrite.split("##");

                    if(gmwrite[0].equals("broadcast")){
                        for(XMPPUser uitem : UserItem) {
                            uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_Broadcast, "Broadcast", gmwrite[1]));
                        }
                    }
                    else{
                        for(XMPPUser uitem : UserItem) {
                            if(uitem.getName().equals(gmwrite[0])) {
                                uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, gmwrite[0].split("@")[0], gmwrite[1]));
                                //existWrite = true;
                                break;
                            }
                            //uitem.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, gmwrite[0].split("@")[0], gmwrite[1]));
                        }
                        BroadCast.getList().add(new XMPPMessage(0, 0, XMPPMessage.MessageType_To, gmwrite[0].split("@")[0], gmwrite[1]));


                    }



                    break;
            }
        }
    };

    public Handler getHandler(){
        return msHandler;
    }

    public List<XMPPMessage> getContent(String n){
        String name = n;
        for(XMPPUser user : UserItem){
            if(user.getName().equals(name)){
                return user.getList();
            }
        }
        items = new ArrayList<>();
        UserItem.add(new XMPPUser(0, name, items));
        return items;
    }
}
