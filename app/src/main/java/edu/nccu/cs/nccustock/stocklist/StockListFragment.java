package edu.nccu.cs.nccustock.stocklist;

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
import android.widget.Toast;

import java.util.ArrayList;

import edu.nccu.cs.nccustock.R;

import android.util.Log;


import edu.nccu.cs.nccustock.stockdetail.StockDetailActivity_;
import edu.nccu.cs.nccustock.stocklist.data.OnGetStockItemCompleted;
import edu.nccu.cs.nccustock.stocklist.data.StockListItemFactory;
import edu.nccu.cs.nccustock.stocklist.dummy.DummyContent;
import edu.nccu.cs.nccustock.stocklist.model.StockItem;

public class StockListFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private ProgressBar progressBar;

    private StockItemAdapter stockItemAdapter;

    // TODO: Rename and change types of parameters
    public static StockListFragment newInstance(String param1, String param2) {
        StockListFragment fragment = new StockListFragment();
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
    public StockListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        stockItemAdapter = new StockItemAdapter(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stockinfo, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(stockItemAdapter);
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        progressBar=(ProgressBar)view.findViewById(R.id.list_loading);
        //Log.e("Log:", "enter.");
        // load data
        StockListItemFactory.getStockItems(new OnGetStockItemCompleted() {
            @Override
            public void done(ArrayList<StockItem> stockItems, Exception e) {
                if(e==null){
                    stockItemAdapter.updateData(stockItems);
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stockItemAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
        return view;
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
        Toast.makeText(view.getContext(),stockItemAdapter.getItem(position).stockname, Toast.LENGTH_LONG).show();
       StockDetailActivity_.intent(this).stockid(stockItemAdapter.getItem(position).stockid).start();
    }
}
