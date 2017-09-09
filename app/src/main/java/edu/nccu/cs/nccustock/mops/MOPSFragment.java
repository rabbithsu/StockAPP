package edu.nccu.cs.nccustock.mops;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.nccu.cs.nccustock.DBConnect.MopsConnector;
import edu.nccu.cs.nccustock.MainActivity;
import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.linechat.Constants;
import edu.nccu.cs.nccustock.newsdetail.NewsDetailActivity_;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsAdapter;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;

/**
 * Created by nccu_dct on 16/2/4.
 */
public class MOPSFragment extends Fragment{
    private ListView allclassnews;
    private String dbc = null;
    private boolean ready = false;
    private ArrayList<NewsItem> newsItems=new ArrayList<NewsItem>();

    private NewsAdapter mAdapter;

    public ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //MainActivity.dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
        mAdapter = new NewsAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mops, container, false);



        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //button_get_record = (Button) view.findViewById(R.id.get_record);
        allclassnews =(ListView) view.findViewById(R.id.mopsnews);
        allclassnews.setAdapter(mAdapter);
        allclassnews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = mAdapter.getItem(position);
                NewsDetailActivity_.intent(getActivity()).content(newsItem.content).image(newsItem.image).title(newsItem.title).datdate(DateStringHelper.dateToString(newsItem.newsDate)).start();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        newsItems.clear();
        dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
        Thread a = new Thread(getmopsThread);
        a.start();
        //updateNews();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_stockid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_StockID: {
                //updateNews();
                dialog = ProgressDialog.show(getActivity(), "讀取中", "Loading...", true);
                Thread a = new Thread(getmopsThread);
                a.start();
                return true;
            }
        }
        return false;
    }


    public boolean updateNews(){
        /*Thread a = new Thread(getmopsThread);

        a.start();
        while (!ready) {

        }
        ready = false;*/

        try {
            JSONArray jsonArray = new JSONArray(dbc);
            Log.d("JS", "done.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                NewsItem newsItem=new NewsItem(jsonData);
                newsItems.add(newsItem);
                //user_acc.setText(jsonData.getString("account"));

                //user_pwd.setText(jsonData.getString("pwd"));


            }
            mAdapter.updateData(newsItems);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("log_tag_mops", e.toString());
            Log.e("Content", dbc);
        }



        return true;
    }

    private Runnable getmopsThread = new Runnable() {
        public void run() {
            try {
                dbc = MopsConnector.executeQuery("SELECT * FROM mops");
                ready = true;


            } catch (Exception e) {
                Log.e("log_tag_mops", e.toString());
                ready = true;
            }finally {
                dialog.dismiss();
                mHandler.obtainMessage(Constants.MESSAGE_XMPP_READ, "".length(), -1, "")
                        .sendToTarget();
            }

        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_XMPP_READ:
                    updateNews();

                    break;
            }
        }
    };
}
