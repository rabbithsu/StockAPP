package edu.nccu.cs.nccustock.newsdetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import edu.nccu.cs.nccustock.DBConnect.DBConnector;
import edu.nccu.cs.nccustock.DBConnect.NewsConnector;
import edu.nccu.cs.nccustock.MainActivity;
import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.linechat.Constants;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsAdapter;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;

//HSU 2015/12/18

public class NewsClassActivity extends Activity {

    private String stockid;
    private String stockname;
    private ListView allclassnews;
    private String dbc = null;
    private boolean ready = false;
    private ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();

    private NewsAdapter mAdapter;
    private ProgressDialog dialog;



    //SHIH 0526
    //private String date2;
    public int height;
    public float max=0;
    public String date;
    public String [] date1={"0","0","0","0","0","0","0"};
    private float[] price ={0,0,0,0,0,0,0};

    private int [] prediction ={
            0,0,0,0,0,0,0
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_class);
        Intent intent = this.getIntent();
        mAdapter = new NewsAdapter(NewsClassActivity.this);
        stockid = intent.getStringExtra("id");
        stockname = intent.getStringExtra("name");
        allclassnews = (ListView) findViewById(R.id.classnews);
        allclassnews.setAdapter(mAdapter);
        allclassnews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = mAdapter.getItem(position);
                NewsDetailActivity_.intent(NewsClassActivity.this).content(newsItem.content).image(newsItem.image).title(newsItem.title).datdate(DateStringHelper.dateToString(newsItem.newsDate)).id(stockid).name(stockname).start();

            }
        });
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    @Override
    protected void onStart() {
        super.onStart();

        newsItems.clear();
        dialog = ProgressDialog.show(this, "讀取中", "Loading...", true);
        Thread a = new Thread(getnewsThread);
        a.start();
        //getNews();

    }

    public void  chart(){
        //SHIH 2016/05/27
        Log.e("hi","chart");
        CombinedChart combinedChart = (CombinedChart) findViewById(R.id.chart);
        CombinedData data = new CombinedData(getXAxisValues());
        data.setData(lineData());
        data.setData(barData());

        assert combinedChart != null;
        combinedChart.setData(data);
        combinedChart.setBackgroundColor(Color.rgb(220,220,220));
        combinedChart.setDescription(" ");


    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add(date1[0]);
        labels.add(date1[1]);
        labels.add(date1[2]);
        labels.add(date1[3]);
        labels.add(date1[4]);
        labels.add(date1[5]);
        labels.add(date1[6]);


        return labels;
    }

    // this method is used to create data for line graph
    public LineData lineData() {
        ArrayList<Entry> line = new ArrayList<>();
        line.add(new Entry(price[0], 0));
        line.add(new Entry(price[1], 1));
        line.add(new Entry(price[2], 2));
        line.add(new Entry(price[3], 3));
        line.add(new Entry(price[4], 4));
        line.add(new Entry(price[5], 5));
        line.add(new Entry(price[6], 6));

        LineDataSet lineDataSet = new LineDataSet(line, "stock price");
        lineDataSet.setColor(Color.rgb(255, 255, 240));

     for(int i = 0; i<7;i++) {
         if(price[i]>max){

             max=price[i];

         }System.out.println(max);
     }
       max*=1.2;


        LineData lineData = new LineData(getXAxisValues(), lineDataSet);

        return lineData;

    }

    // this method is used to create data for Bar graph
    public BarData barData() {

        ArrayList<BarEntry> group1 = new ArrayList<>();
       //
        for(int i=0; i<7;i++) {

            height=Math.round(max);
            group1.add(new BarEntry(height, i));
        }
        /*
        group1.add(new BarEntry(80, 0));
        group1.add(new BarEntry(prediction[1], 1));
        group1.add(new BarEntry(prediction[2], 2));
        group1.add(new BarEntry(prediction[3], 3));
        group1.add(new BarEntry(prediction[4], 4));
        group1.add(new BarEntry(prediction[5], 5));
        group1.add(new BarEntry(prediction[6], 6));
        */
        MyBarDataSet set = new MyBarDataSet(group1, "prediction");
        //Context context;
        set.setColors(new int[]{
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.red),
                });
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        BarData barData = new BarData(getXAxisValues(), set);

        //barData.setSpaceTop(2.5);
        barData.setDrawValues(false);
        return barData;

    }



    @Override
    public void onStop() {
        super.onStop();

    }

    public class MyBarDataSet extends BarDataSet {

        public MyBarDataSet(List<BarEntry> predict, String label) {
            super(predict, label);
        }


        @SuppressLint("Override")
        public int getColor(int index) {

            if (prediction[index]==1) // less than 95 green
                return mColors.get(1);
            else
                return mColors.get(0);

        }

            /*
            if (getEntryForXIndex(index).getVal()==2) // less than 95 green
                return mColors.get(0);
            else if (getEntryForXIndex(index).getVal()==1) // less than 100 orange
                return mColors.get(1);
            else // greater or equal than 100 red
                return mColors.get(2);
                */

//
//        @SuppressLint("Override")
//        public int getColor() {
//
//            for (int i = 0; i < 7; i++) {
//                if (prediction[i]==1) // green
//                    return mColors.get(0);
//                else if (prediction[i]==0) //orange
//                    return mColors.get(1);
//                else // greater or equal than 100 red
//                    return mColors.get(2);
//
//
//            }
//
//            return 0;
//        }

    }

    public boolean getData() {
        //Thread a = new Thread(getnewsThread);
        //a.start();

        try {
            JSONArray jsonArray = new JSONArray(date);
            Log.d("JS", "done.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
//                String a=jsonData.getString("id");
//                String b=jsonData.getString("ord");
                String c=jsonData.getString("price");
                String d=jsonData.getString("result");
                String e=jsonData.getString("date");

                date1[6-i]=e;
                price[6-i]= Float.parseFloat(c);
                prediction[6-i]= Integer.parseInt(d);
                Log.e("hi",e+c+d);

            }

        } catch (Exception e) {
            Log.e("log_tag2", e.toString() + stockid);
            Log.e("Content2", date);
        }

        chart();
        return true;
}


    public boolean getNews() {
        //Thread a = new Thread(getnewsThread);
        //a.start();

        try {
            JSONArray jsonArray = new JSONArray(dbc);
            Log.d("JS", "done.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                NewsItem newsItem = new NewsItem(jsonData);
                newsItems.add(newsItem);
                //user_acc.setText(jsonData.getString("account"));
                //user_pwd.setText(jsonData.getString("pwd"));

            }
            mAdapter.updateData(newsItems);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("log_tag", e.toString() + stockid);
            Log.e("Content", dbc);
        }
        if (newsItems.size() == 0) {
            Toast.makeText(this, "暫無新聞: " + stockname, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, stockname, Toast.LENGTH_SHORT).show();
        }


        return true;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_XMPP_READ:
                    getNews();
                    getData();

                    break;
            }
        }
    };

    private Runnable getnewsThread = new Runnable() {
        public void run() {
            try {
                dbc = NewsConnector.executeQuery("SELECT * FROM news WHERE id = " + stockid);
                date = NewsConnector.executeQuery("SELECT * FROM history WHERE id = " + stockid);

                ready = true;


            } catch (Exception e) {
                Log.e("log_tag3", e.toString());
                ready = true;
            } finally {
                dialog.dismiss();
                mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, "".length(), -1, "")
                        .sendToTarget();
            }
        }
    };


}
