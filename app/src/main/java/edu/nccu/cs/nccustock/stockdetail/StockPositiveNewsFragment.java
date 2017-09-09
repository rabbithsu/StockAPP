package edu.nccu.cs.nccustock.stockdetail;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.R;

import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.newsdetail.NewsDetailActivity_;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;
import edu.nccu.cs.nccustock.parse.eneity.News;
import edu.nccu.cs.nccustock.parse.eneity.stock_news;
import edu.nccu.cs.nccustock.stockdetail.dummy.DummyContent;

public class StockPositiveNewsFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DIRECTION = "DIRECTION";
    private static final String STOCKNAME = "STOCKNAME";


    // TODO: Rename and change types of parameters
    private boolean mParam1;
    private String mParam2;

    private StockNewsAdapter stockNewsAdapter;


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static StockPositiveNewsFragment newInstance(Boolean direction, String stockname) {
        StockPositiveNewsFragment fragment = new StockPositiveNewsFragment();
        Bundle args = new Bundle();
        args.putBoolean(DIRECTION, direction);
        args.putString(STOCKNAME, stockname);

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StockPositiveNewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getBoolean(DIRECTION);
            mParam2 = getArguments().getString(STOCKNAME);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        stockNewsAdapter = new StockNewsAdapter(getActivity());
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(stockNewsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = stockNewsAdapter.getItem(position);
                NewsDetailActivity_.intent(getActivity()).content(newsItem.content).image(newsItem.image).title(newsItem.title).datdate(DateStringHelper.dateToString(newsItem.newsDate)).start();

            }
        });


        init_data();
        return view;
    }

    private void init_data() {
        ParseQuery parseQuery = new ParseQuery(stock_news.table_name);
        parseQuery.orderByDescending(stock_news.field.news_date);

        parseQuery.whereEqualTo(stock_news.field.is_pos, mParam1);
        parseQuery.whereEqualTo(stock_news.field.stock_name, mParam2);

        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();
                    for (int i = 0; i != parseObjects.size(); i++) {
                        NewsItem newsItem = new NewsItem(parseObjects.get(i));
                        boolean if_add = true;
                        for (int j = 0; j != newsItems.size(); j++) {
                            if (newsItems.get(j).image.equals(newsItem.image)) {
                                if_add = false;
                                break;
                            }
                        }
                        if (if_add == true) {
                            newsItems.add(newsItem);
                        }else{
                            parseObjects.get(i).deleteEventually();
                        }
                    }
                    stockNewsAdapter.updateData(newsItems);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stockNewsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


}
