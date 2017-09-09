package edu.nccu.cs.nccustock.stockdetail;


import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateCompareHelper;
import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.entity.StockInfoEntity;
import edu.nccu.cs.nccustock.entity.StockInfoHelper;
import edu.nccu.cs.nccustock.entity.StockPredictEntity;
import edu.nccu.cs.nccustock.entity.StockPriceEntity;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;
import edu.nccu.cs.nccustock.parse.eneity.StockInfo;
import edu.nccu.cs.nccustock.parse.eneity.stock_news;
import edu.nccu.cs.nccustock.stocklist.data.StockPriceFactory;

@EActivity
public class StockDetailActivity extends ActionBarActivity {
    @Extra("stockid")
    String stockid;
    public StockInfoEntity stockInfo;
    private final int itemcount = 12;
    @ViewById
    CombinedChart mChart;
    @ViewById
    ViewPager mViewPager;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    List<StockPriceEntity > stockInfoEntities;
    List<StockPredictEntity> stockPredictEntities;
    double corr_pos=0;
    double corr_neg=0;

    double min=999999;
    double max=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        stockInfo = StockInfoHelper.getStockInfoEntityById(stockid);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date target_date = new Date();
            stockPredictEntities=new ArrayList<>();
            stockInfoEntities= StockPriceFactory.getStockPricePast(target_date,stockid,14);
            for(int i=0;i!=stockInfoEntities.size();i++){
                double close=stockInfoEntities.get(i).close;
                StockPredictEntity stockPredictEntity=StockInfoHelper.getStockPredictEntityByDateID(stockInfoEntities.get(i).stockid,stockInfoEntities.get(i).dateindex);
                if(stockPredictEntity!=null){
                   stockPredictEntities.add(stockPredictEntity);
                    if(stockPredictEntity.predict>0){
                        stockInfoEntities.get(i).pos=1;
                    }else{
                        stockInfoEntities.get(i).neg=1;
                    }
                }
                if(close>max){
                    max=close;
                }
                if(close<min){
                    min=close;
                }
            }
            init_chart();
            calculate_performance();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }


        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(),stockInfo.stockname);
        mViewPager.setAdapter( mAppSectionsPagerAdapter);
        setActionBar();
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i)).setTabListener(new ActionBar.TabListener() {
                        @Override
                        public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                            mViewPager.setCurrentItem(tab.getPosition());
                        }

                        @Override
                        public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }

                        @Override
                        public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

                        }
                    }));

        }


    }

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private void init_news_data(){

        ParseQuery parseQuery=new ParseQuery(stock_news.table_name);
        parseQuery.orderByDescending(stock_news.field.news_date);
        parseQuery.whereEqualTo(stock_news.field.stock_name,stockInfo.stockname);
        parseQuery.setLimit(1000);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if(e==null){
                    ArrayList<NewsItem> newsItems=new ArrayList<NewsItem>();
                    for(int i=0;i!=parseObjects.size();i++){
                        ParseObject parseObject=parseObjects.get(i);
                        for(int j=0;j!=stockInfoEntities.size();j++){
                           StockPriceEntity stockInfoEntity=stockInfoEntities.get(j);
                            if(DateCompareHelper.isSameDay(stockInfoEntity.datdate,parseObject.getDate(stock_news.field.news_date))){
                                DebugLog.log("match","match!");
                                if(parseObject.getBoolean(stock_news.field.is_pos)){
                                    stockInfoEntity.pos++;
                                }else{
                                    stockInfoEntity.neg++;
                                }

                            }else{
                                DebugLog.log("not match",stockInfoEntity.datdate.toString()+" "+parseObject.getDate(stock_news.field.news_date).toString());
                            }
                        }
                    }
                    calculate_performance();


                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void calculate_performance(){
        PearsonsCorrelation pearsonsCorrelation=new PearsonsCorrelation();
        double [] price=new double[ stockInfoEntities.size()];
        double [] pos=new double[ stockInfoEntities.size()];
        double [] neg=new double[ stockInfoEntities.size()];
        for(int i=0;i!=stockInfoEntities.size();i++){
            price[i]=stockInfoEntities.get(i).close;
            pos[i]=stockInfoEntities.get(i).pos;
            neg[i]=stockInfoEntities.get(i).neg;
        }
        double pos_corr=pearsonsCorrelation.correlation(price,pos);
        corr_pos=pos_corr;
        double neg_corr=pearsonsCorrelation.correlation(price,neg);
        corr_neg=neg_corr;
        CombinedData data = mChart.getData();
        data.setData(generateBarData());
        mChart.setData(data);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.animate();
                mChart.invalidate();
            }
        });
        // set bar chart
        DebugLog.log("stats","pos="+String.valueOf(pos_corr)+" "+"neg"+String.valueOf(neg_corr));
    }

    private void init_chart(){
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);

        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[] {
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue((float) ((float) min*0.95));
        leftAxis.setAxisMaxValue((float)  ((float) max*1.05));
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(true);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ArrayList<String> dateTags=new ArrayList<>();

        DateFormat df = new SimpleDateFormat("MM/dd");
        for(int i=0;i!=stockInfoEntities.size();i++){
            dateTags.add(df.format(stockInfoEntities.get(i).datdate));
        }
        CombinedData data = new CombinedData(dateTags);

        data.setData(generateLineData());

//        data.setData(generateBubbleData());
//         data.setData(generateScatterData());
//         data.setData(generateCandleData());

        mChart.setData(data);
        mChart.invalidate();
        //init_news_data();
    }


    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int []colors = new int[stacksize];

        colors[0]=Color.rgb(154,205,50);
        colors[1]=Color.rgb(255,140,0);


        return colors;
    }

    private LineData generateLineData() {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for(int i=0;i!=stockInfoEntities.size();i++){
            entries.add(new Entry((float) stockInfoEntities.get(i).close, i));
        }
        LineDataSet set = new LineDataSet(entries, "股價");
        set.setColor(Color.rgb(0,0,255));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0,0,255));
        set.setCircleSize(2f);
        set.setFillColor(Color.rgb(0,0,255));
        set.setDrawCubic(true);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {

        BarData d = new BarData();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();


        for (int index = 0; index < stockInfoEntities.size(); index++){
            DebugLog.log("bar pos",String.valueOf(stockInfoEntities.get(index).pos));
            DebugLog.log("bar neg",String.valueOf(stockInfoEntities.get(index).neg));
            yVals1.add(new BarEntry(new float[] {
                    stockInfoEntities.get(index).neg, stockInfoEntities.get(index).pos,
            }, index ));
        }


        BarDataSet set = new BarDataSet(yVals1, ""+"");
        set.setColors(getColors());
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(0f);
        DecimalFormat df = new DecimalFormat("#0.00");
        set.setStackLabels(new String[] {
                "預測股價下跌  ", "預測股價上漲  "
        });

        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        return d;
    }

    private void setActionBar() {
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(stockInfo.stockname);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_stock_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onBackPressed();

        return super.onOptionsItemSelected(item);
    }
    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }



}
