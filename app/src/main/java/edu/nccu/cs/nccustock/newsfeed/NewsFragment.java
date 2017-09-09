package edu.nccu.cs.nccustock.newsfeed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import edu.nccu.cs.nccustock.DBConnect.NewsConnector;
import edu.nccu.cs.nccustock.MainActivity;
import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.linechat.ChatWindows;
import edu.nccu.cs.nccustock.linechat.Constants;
import edu.nccu.cs.nccustock.newsdetail.NewsClassActivity;
import android.app.ProgressDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by nccu_dct on 15/12/16.
 */
public class NewsFragment extends Fragment {
    //private Button button_get_record;
    private TableLayout user_list;
    private TextView renewtime;
    private Button updatebutton;
    public String dbnews = null;
    public String dbp = null;
    boolean ready = false;
    private ArrayList<String> predictresult = new ArrayList<>();
    private List<String> namelists = Arrays.asList("1101", "台泥", "1102", "亞泥", "1216", "統一", "1301", "台塑", "1303", "南亞", "1326", "台化", "1402", "遠東新", "2002", "中鋼", "2105", "正新", "2207", "和泰車", "2227", "裕日車", "2301", "光寶科", "2303", "聯電", "2308", "台達電", "2311", "日月光", "2317", "鴻海", "2325", "矽品", "2330", "台積電", "2354", "鴻準", "2357", "華碩", "2382", "廣達", "2395", "研華", "2408", "南亞科", "2409", "友達", "2412", "中華電", "2454", "聯發科", "2474", "可成", "2498", "宏達電", "2801", "彰銀", "2880", "華南金", "2881", "富邦金", "2882", "國泰金", "2883", "開發金", "2884", "玉山金", "2885", "元大金", "2886", "兆豐金", "2887", "台新金", "2890", "永豐金", "2891", "中信金", "2892", "第一金", "2912", "統一超", "3008", "大立光", "3045", "台灣大", "3474", "華亞科", "3481", "群創", "4904", "遠傳", "4938", "和碩", "5880", "合庫金", "6505", "台塑化", "9904", "寶成");
    public String[] namelist = {"1101", "台泥", "1102", "亞泥", "1216", "統一", "1301", "台塑", "1303", "南亞", "1326", "台化", "1402", "遠東新", "2002", "中鋼", "2105", "正新", "2207", "和泰車", "2227", "裕日車", "2301", "光寶科", "2303", "聯電", "2308", "台達電", "2311", "日月光", "2317", "鴻海", "2325", "矽品", "2330", "台積電", "2354", "鴻準", "2357", "華碩", "2382", "廣達", "2395", "研華", "2408", "南亞科", "2409", "友達", "2412", "中華電", "2454", "聯發科", "2474", "可成", "2498", "宏達電", "2801", "彰銀", "2880", "華南金", "2881", "富邦金", "2882", "國泰金", "2883", "開發金", "2884", "玉山金", "2885", "元大金", "2886", "兆豐金", "2887", "台新金", "2890", "永豐金", "2891", "中信金", "2892", "第一金", "2912", "統一超", "3008", "大立光", "3045", "台灣大", "3474", "華亞科", "3481", "群創", "4904", "遠傳", "4938", "和碩", "5880", "合庫金", "6505", "台塑化", "9904", "寶成"};


    private ProgressDialog dialog;

    private Button.OnClickListener getDBRecord = new Button.OnClickListener() {
        public void onClick(View v) {
            Thread thread = new Thread(getnewsThread);
            thread.start();

            while (!ready) {

            }
            ready = false;
            user_list.setStretchAllColumns(true);
            TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            try {
                JSONArray jsonArray = new JSONArray(dbnews);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    TableRow tr = new TableRow(getActivity());
                    tr.setLayoutParams(row_layout);
                    tr.setGravity(Gravity.CENTER_HORIZONTAL);

                    TextView user_acc = new TextView(getActivity());
                    user_acc.setText(jsonData.getString("account"));
                    user_acc.setLayoutParams(view_layout);

                    TextView user_pwd = new TextView(getActivity());
                    user_pwd.setText(jsonData.getString("pwd"));
                    user_pwd.setLayoutParams(view_layout);

                    tr.addView(user_acc);
                    tr.addView(user_pwd);
                    user_list.addView(tr);
                }
            } catch (Exception e) {
                Log.e("log_tag", e.toString());
            }

        }
    };


    private Button.OnClickListener getrenewtime = new Button.OnClickListener() {
        public void onClick(View v) {
            user_list.removeAllViews();
            dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
            Thread thread = new Thread(gettimeThread);
            thread.start();
            /*updateP();
            updateStockID();*/

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //button_get_record = (Button) view.findViewById(R.id.get_record);
        user_list = (TableLayout) view.findViewById(R.id.user_list);
        renewtime = (TextView) view.findViewById(R.id.updatetime);
        updatebutton = (Button) view.findViewById(R.id.updatenews);
        updatebutton.setOnClickListener(getrenewtime);
        //button_get_record.setOnClickListener(getDBRecord);

        //user_list.setOnItemClickListener(mChatClickListener);
    }

    private Runnable getnewsThread = new Runnable() {
        public void run() {
            try {
                dbnews = NewsConnector.executeQuery("SELECT * FROM user");
                ready = true;


            } catch (Exception e) {
                Log.e("log_tag", e.toString());
                ready = true;
            }finally {
                //dialog.dismiss();
            }

        }
    };
    private Runnable gettimeThread = new Runnable() {
        public void run() {
            try {
                dbnews = NewsConnector.executeQuery("SELECT DISTINCT(date) FROM predict");
                dbp = NewsConnector.executeQuery("SELECT result FROM predict");
                ready = true;


            } catch (Exception e) {
                Log.e("log_tag", e.toString());
                ready = true;
            }finally {
                dialog.dismiss();
                mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, "".length(), -1, "")
                        .sendToTarget();
            }
        }
    };

    private Runnable getpredictThread = new Runnable() {
        public void run() {
            try {
                dbp = NewsConnector.executeQuery("SELECT result FROM predict");
                ready = true;


            } catch (Exception e) {
                Log.e("log_tag", e.toString());
                ready = true;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        user_list.removeAllViews();
        dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
        Thread thread = new Thread(gettimeThread);
        thread.start();
        /*updateP();
        updateStockID();*/

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stockid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_StockID: {
                dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
                Thread thread = new Thread(gettimeThread);
                thread.start();
                //updateStockID();
                return true;
            }
        }
        return false;
    }

    public void updateStockID() {
        user_list.setStretchAllColumns(true);
        TableLayout.LayoutParams row_layout = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams view_layout = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        boolean end = false;
        TableRow ntr;
        TextView n = new TextView(getActivity());
        int count = 0;

        //欄位名稱
        ntr = new TableRow(getActivity());
        ntr.setBackgroundColor(Color.parseColor("#666666"));


        n = new TextView(getActivity());
        n.setText("股票代號");

        n.setTextSize(25);
        n.setTag(count);
        ntr.addView(n);

        n = new TextView(getActivity());
        n.setText("股票名稱");
        n.setTextSize(25);
        n.setTag(count);
        ntr.addView(n);

        n = new TextView(getActivity());
        n.setText("預測結果");
        n.setTextSize(25);
        n.setTag(count);
        ntr.addView(n);
        count+=1;

        user_list.addView(ntr);

        for (String name : namelists) {
            if (end) {

                ntr = new TableRow(getActivity());
                ntr.addView(n);

                n = new TextView(getActivity());
                n.setText(name);
                n.setTextSize(20);
                n.setTag(count);
                //count += 1;
                n.setOnClickListener(mNewsClickListener);
                ntr.addView(n);

                // add predict
                n = new TextView(getActivity());
                n.setTag(count);
                if(predictresult.size() < count){

                }
                else if(predictresult.get(count).equals("1")) {
                    n.setText("上升");
                    n.setTextColor(Color.RED);
                    n.setTextSize(20);
                }
                else{
                    n.setText("下降");
                    n.setTextColor(Color.GREEN);
                    n.setTextSize(20);
                }

                count += 1;
                n.setOnClickListener(mNewsClickListener);
                ntr.addView(n);

                user_list.addView(ntr);
                end = false;
            } else {
                n = new TextView(getActivity());
                n.setText(name);
                n.setTextSize(20);
                n.setTag(count);
                n.setOnClickListener(mNewsClickListener);
                end = true;
            }
        }
    }

    private View.OnClickListener mNewsClickListener
            = new View.OnClickListener() {
        public void onClick(View v){

            Intent serverIntent = new Intent(getActivity(), NewsClassActivity.class);
            String info = ((TextView) v).getText().toString();
            int index = namelists.indexOf(info);
            index = (Integer) v.getTag();
            serverIntent.putExtra("id", namelists.get((index-1)*2));
            serverIntent.putExtra("name", namelists.get((index-1)*2+1));
            //Toast.makeText(getActivity(),namelists.get(index*2-1),Toast.LENGTH_SHORT).show();


            //test data
            //account = "broker@140.119.164.5";

            startActivity(serverIntent);
        }

    };

    private void updateP(){
        /*Thread thread = new Thread(gettimeThread);
        thread.start();

        while (!ready) {

        }
        ready = false;*/

        try {
            JSONArray jsonArray = new JSONArray(dbnews);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);

                renewtime.setText(jsonData.getString("date"));

            }
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

        /*Thread p = new Thread(getpredictThread);
        //
        p.start();

        while (!ready) {

        }

        ready = false;*/
        predictresult.clear();
        try {
            JSONArray jsonArray = new JSONArray(dbp);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);

                predictresult.add(jsonData.getString("result"));

            }
        } catch (Exception e) {
            Log.e("log_tag", e.toString());
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_XMPP_READ:
                    updateP();
                    updateStockID();
                    break;
            }
        }
    };



}