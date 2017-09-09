package edu.nccu.cs.nccustock.linechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.nccu.cs.nccustock.R;

/**
 * Created by nccu_dct on 15/10/13.
 */
public class Login extends Activity {

    private EditText UserEditText;
    private EditText PwEditText;
    private Button SigninButton;

    public Login(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.xmpplogin);
        setResult(Activity.RESULT_CANCELED);
        ChatFragment.logged = true;
        UserEditText = (EditText) findViewById(R.id.editText);
        PwEditText = (EditText) findViewById(R.id.editText2);
        SigninButton = (Button) findViewById(R.id.button);
        SigninButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView userview = (TextView) findViewById(R.id.editText);
                TextView pwview = (TextView) findViewById(R.id.editText2);
                TextView nameview = (TextView) findViewById(R.id.editText3);
                String username = userview.getText().toString();
                String pw = pwview.getText().toString();
                String name = nameview.getText().toString();
                //pwview.setText("");
                trylogin(username, pw, name);

            }
        });
    }
    private void trylogin(String u, String p, String n){
        ChatFragment.mXMPP = new XMPPService(getApplicationContext(), u, p);
        while(!ChatFragment.mXMPP.log){

        }
        if(ChatFragment.mXMPP.success){
            Toast.makeText(Login.this, "Log in.", Toast.LENGTH_SHORT).show();
            ChatFragment.XMPPname = n;
            ChatFragment.logged = true;
            //ChatFragment.mXMPP = null;
            finish();
        }
        else{
            Toast.makeText(Login.this, "Log in fails.", Toast.LENGTH_SHORT).show();
            ChatFragment.mXMPP = null;
            ChatFragment.logged = true;
        }

        //trylogin(u, p, n);

    }

}
