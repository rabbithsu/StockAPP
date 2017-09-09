package edu.nccu.cs.nccustock.linechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.nccu.cs.nccustock.R;

//import android.app.Fragment;


public class ChatFragment extends Fragment {
    //groupchat
    private ListView mGroupChatView;
    private ArrayAdapter<String> mGroupChatArrayAdapter;


    private ListView mChatView;
    private ArrayAdapter<String> mChatArrayAdapter;
    public static XMPPService mXMPP = null;
    public static String XMPPname;
    public static boolean logged = false;
    private MessageAdapter mConversationArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mChatArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.chat_name);

        mGroupChatArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.chat_name);
        /*Intent serverIntent = new Intent(getActivity(), Login.class);
        startActivity(serverIntent);*/

        //itemDB = new MitemDB(getActivity().getApplicationContext());
        //items = itemDB.getAll();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!logged&&mXMPP==null) {
            Intent serverIntent = new Intent(getActivity(), Login.class);
            startActivity(serverIntent);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if(mXMPP != null){
            mGroupChatArrayAdapter.clear();
            if(XMPPname.equals("broker")){
                mGroupChatArrayAdapter.add("Costumer Group");
            }
            mChatArrayAdapter.clear();
            getList();
        }
    }


    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //mConversationView = (ListView) view.findViewById(R.id.chat);
        mGroupChatView =(ListView) view.findViewById(R.id.groupchat);
        mGroupChatView.setAdapter(mGroupChatArrayAdapter);
        mGroupChatView.setOnItemClickListener(mGroupChatClickListener);

        mChatView =(ListView) view.findViewById(R.id.chat);
        mChatView.setAdapter(mChatArrayAdapter);
        mChatView.setOnItemClickListener(mChatClickListener);
        //mChatArrayAdapter.add("銀行營業專員\nBroker");
    }

    private AdapterView.OnItemClickListener mChatClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            Intent serverIntent = new Intent(getActivity(), ChatWindows.class);
            String info = ((TextView) v).getText().toString();
            String account = info.split("\n")[0];

            //test data

            serverIntent.putExtra("account", account);
            startActivity(serverIntent);
        }
    };

    private AdapterView.OnItemClickListener mGroupChatClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            Intent serverIntent = new Intent(getActivity(), GroupChatWindows.class);

            String account = "all@broadcast.140.119.164.18";
            //account = "broker@140.119.164.5";

            serverIntent.putExtra("account", account);
            startActivity(serverIntent);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.xmpp_login, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.getlog: {
                if (mXMPP == null) {
                    Intent serverIntent = new Intent(getActivity(), Login.class);
                    startActivity(serverIntent);
                } else {
                    Toast.makeText(getActivity(), "Already log in.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        }
        return false;
    }

    public void getList(){
        String list = mXMPP.getRoster();
        for(String listitem : list.split(";")){
            mChatArrayAdapter.add(listitem);
        }
    }

}
