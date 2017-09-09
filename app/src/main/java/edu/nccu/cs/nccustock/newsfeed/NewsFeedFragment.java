package edu.nccu.cs.nccustock.newsfeed;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import edu.nccu.cs.nccustock.R;

import edu.nccu.cs.nccustock.StockMain;
import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.newsdetail.NewsDetailActivity_;
import edu.nccu.cs.nccustock.newsfeed.dummy.DummyContent;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsAdapter;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;
import edu.nccu.cs.nccustock.parse.eneity.News;

public class NewsFeedFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ProgressBar feed_loading;



    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private NewsAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new NewsAdapter(getActivity());

    }

    public void onResume(){
        super.onResume();
        initData();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsitem, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem=mAdapter.getItem(position);
                NewsDetailActivity_.intent(getActivity()).content(newsItem.content).image(newsItem.image).title(newsItem.title).datdate(DateStringHelper.dateToString(newsItem.newsDate)).start();

            }
        });
        feed_loading= (ProgressBar) view.findViewById(R.id.feed_loading);

        return view;
    }

    private void initData(){
        // refine later
        ParseQuery parseQuery=new ParseQuery(News.table_name);
        parseQuery.orderByDescending(News.field.news_date);
        parseQuery.whereExists(News.field.image);
        parseQuery.setLimit(20);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e==null){
                    ArrayList<NewsItem> newsItems=new ArrayList<NewsItem>();
                    for(int i=0;i!=parseObjects.size();i++){

                        NewsItem newsItem=new NewsItem(parseObjects.get(i));
                        boolean if_add=true;
                        for(int j=0;j!=newsItems.size();j++){
                            if(newsItems.get(j).image.equals(newsItem.image)){
                                if_add=false;
                                break;
                            }
                        }
                        if(if_add==true) {
                            newsItems.add(newsItem);
                        }
                    }
                    mAdapter.updateData(newsItems);
                    if(getActivity()!=null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                feed_loading.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }else{
                    e.printStackTrace();
                    initData();
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        ((StockMain) activity).onSectionAttached(
               1);
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


}
