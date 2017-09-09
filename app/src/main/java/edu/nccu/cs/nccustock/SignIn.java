package edu.nccu.cs.nccustock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.nccu.cs.nccustock.linechat.ChatFragment;
import edu.nccu.cs.nccustock.linechat.ChatRoom;
import edu.nccu.cs.nccustock.linechat.XMPPChatService;
import edu.nccu.cs.nccustock.linechat.XMPPService;

/**
 * Created by StaceyShih on 2016/7/21.
 */
public class SignIn extends Activity{

    private Button send;
    private EditText nickName;

    private EditText UserEditText;
    private EditText PwEditText;
    private Button SigninButton;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.sign_in_layout);
        setResult(Activity.RESULT_CANCELED);
        ChatRoom.logged = true;
        UserEditText = (EditText) findViewById(R.id.editText);
        PwEditText = (EditText) findViewById(R.id.editText2);
        nickName=(EditText)findViewById(R.id.editText3);
        SigninButton = (Button) findViewById(R.id.button);
        SigninButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
//                TextView userview = (TextView) findViewById(R.id.editText);
//                TextView pwview = (TextView) findViewById(R.id.editText2);
                TextView nameview = (TextView) findViewById(R.id.editText3);
//                String username = userview.getText().toString();
//                String pw = pwview.getText().toString();
                String name = nameview.getText().toString();
                //pwview.setText("");
                trylogin( name);
            }
        });
    }
    private void trylogin( String n){
        //ChatRoom.mXMPP = new XMPPChatService(getApplicationContext(),mHandler,n,n);
        System.out.println("in trylogin");

     if(n.length()>0){
            Toast.makeText(SignIn.this, "Log in.", Toast.LENGTH_SHORT).show();
            ChatRoom.MyName= n;
           // System.out.println("login success");
           System.out.println(n);
             System.out.println(n.length());
            ChatRoom.logged = true;
            finish();
 }
 else{
         Toast.makeText(SignIn.this, "Please enter your nickname", Toast.LENGTH_SHORT).show();
//            ChatRoom.MyName= n;
//            System.out.println(ChatRoom.MyName);
//
//            Toast.makeText(SignIn.this, "Log in fails.", Toast.LENGTH_SHORT).show();
//            ChatRoom.mXMPP = null;
//            ChatRoom.logged = true;
//            System.out.println("login fails2");
 }
//
//        //trylogin(u, p, n);
//        System.out.println("login fails3");
    }

}
