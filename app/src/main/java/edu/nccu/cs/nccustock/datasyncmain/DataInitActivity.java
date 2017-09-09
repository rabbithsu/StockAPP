package edu.nccu.cs.nccustock.datasyncmain;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.StockMain_;
import edu.nccu.cs.nccustock.data.StockPriceGen;

@EActivity
public class DataInitActivity extends ActionBarActivity {
    @ViewById
    TextView status;
    @ViewById
    Button retry;

    Activity me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_init);

        me=this;
        DataInit.init_data(this, new OnDataInitComplete() {
            @Override
            public void done(Exception e) {
                if (e == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("資料初始化完成!");
                            StockMain_.intent(me).start();
                            finish();
                        }
                    });

                } else {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("資料初始化失敗");
                            retry.setVisibility(View.VISIBLE);
                        }
                    });


                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
