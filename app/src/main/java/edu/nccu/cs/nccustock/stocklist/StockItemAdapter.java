package edu.nccu.cs.nccustock.stocklist;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.entity.StockPriceEntity;
import edu.nccu.cs.nccustock.newsfeed.newslist.NewsItem;
import edu.nccu.cs.nccustock.parse.eneity.stock_news;
import edu.nccu.cs.nccustock.stocklist.model.StockItem;

public class StockItemAdapter extends BaseAdapter {
    List<StockItem> data;
    private LayoutInflater myInflater;
    private DisplayImageOptions options;
    private StockItemAdapter self;

    public StockItemAdapter(Context ctxt) {
        myInflater = LayoutInflater.from(ctxt);
        this.data = new ArrayList<>();
        init_image_loading_options();
        self=this;
    }

    public void updateData(List<StockItem> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public StockItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //自訂類別，表達個別listItem中的view物件集合。
        ViewTag viewTag = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.stock_list_item, parent, false);
            viewTag = new ViewTag();
            viewTag.stock_name = (TextView) convertView.findViewById(R.id.stock_list_item_stockname);
            viewTag.close = (TextView) convertView.findViewById(R.id.stock_list_item_closing);
            viewTag.stockchart = (LineChart) convertView.findViewById(R.id.stockchart);
            viewTag.pos_neg_bar=(ProgressBar)convertView.findViewById(R.id.stock_list_bar);
            viewTag.pos_neg_bar.setMax(100);
            // viewTag.close=(TextView)convertView.findViewById(R.id.close)
            convertView.setTag(viewTag);
        } else {
            viewTag = (ViewTag) convertView.getTag();
        }
        final StockItem stockItem = (StockItem) getItem(position);
        viewTag.stock_name.setText(stockItem.stockname);
        viewTag.close.setText(stockItem.yesterday_close);
        init_chart(viewTag.stockchart,stockItem.stockPriceEntities);
        viewTag.stockchart.invalidate();
        if(stockItem.pos!=-1 && stockItem.neg!=-1){
            DebugLog.log("view","pos"+String.valueOf(stockItem.pos));
            DebugLog.log("view","neg"+String.valueOf(stockItem.neg));
            double progress=((float)stockItem.neg/(stockItem.pos+stockItem.neg));
            DebugLog.log("view","progress"+String.valueOf(progress));
          //  viewTag.pos_neg_bar.setProgress((int) (100*progress));
            ObjectAnimator animation = ObjectAnimator.ofInt(viewTag.pos_neg_bar, "progress",(int) (100*progress));
            animation.setDuration(500); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }

        Calendar start = Calendar.getInstance();
        start.setTime(stockItem.datdate);
        start.add(Calendar.DATE, -1*14);
        if(stockItem.pos==-1){
            ParseQuery parseQuery=new ParseQuery(stock_news.table_name);
            parseQuery.whereEqualTo(stock_news.field.stock_name,stockItem.stockname);
            parseQuery.orderByDescending(stock_news.field.news_date);
            parseQuery.whereEqualTo(stock_news.field.is_pos, true);
            parseQuery.whereGreaterThan(stock_news.field.news_date, start.getTime());
            parseQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int i, ParseException e) {
                    if(e==null){
                        DebugLog.log("view","pos got"+String.valueOf(i));
                        stockItem.pos=i;
                        self.notifyDataSetChanged();;
                    }

                }
            });
        }
        if(stockItem.neg==-1){
            ParseQuery parseQuery=new ParseQuery(stock_news.table_name);
            parseQuery.whereEqualTo(stock_news.field.stock_name,stockItem.stockname);
            parseQuery.orderByDescending(stock_news.field.news_date);
            parseQuery.whereEqualTo(stock_news.field.is_pos,false);
            parseQuery.whereGreaterThan(stock_news.field.news_date, start.getTime());
            parseQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int i, ParseException e) {
                    if(e==null){
                        DebugLog.log("view","neg got"+String.valueOf(i));
                        stockItem.neg=i;
                        self.notifyDataSetChanged();;
                    }

                }
            });
        }
        return convertView;
    }

    private void init_chart(LineChart chart,ArrayList<StockPriceEntity> stockPriceEntities) {
        // apply styling
        // holder.chart.setValueTypeface(mTf);
        chart.setDescription("");
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(5);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setLabelCount(5);
        rightAxis.setDrawGridLines(false);

        // set data
        chart.setData((LineData) generateDataLine(chart,stockPriceEntities));

        //chart.animateX(750);
    }


    private LineData generateDataLine(LineChart mChart,ArrayList<StockPriceEntity> stockPriceEntities) {

        ArrayList<Entry> e2 = new ArrayList<Entry>();
        double high=-1;
        double low=9999999;
        for (int i = 0; i < stockPriceEntities.size(); i++) {
            if(stockPriceEntities.get(i).close<low){
                low=stockPriceEntities.get(i).close;
            }
            if(stockPriceEntities.get(i).close>high){
                high=stockPriceEntities.get(i).close;
            }
            e2.add(new Entry((float) stockPriceEntities.get(i).close, i));
        }
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue((float) ((float) low*0.99));
        leftAxis.setAxisMaxValue((float)  ((float) high*1.01));
        leftAxis.setDrawGridLines(false);

        LineDataSet d2 = new LineDataSet(e2, "");
        d2.setLineWidth(1.5f);
        d2.setCircleSize(0.0f);
        //d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(Color.rgb(0, 0, 139));
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);
        d2.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
       // sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(getMonths(), sets);
        mChart.invalidate();
        return cd;
    }
    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("Jan");
        m.add("Feb");
        m.add("Mar");
        m.add("Apr");
        m.add("May");
        m.add("Jun");
        m.add("Jul");
        m.add("Aug");
        m.add("Sep");
        m.add("Okt");
        m.add("Nov");
        m.add("Dec");

        return m;
    }

    //自訂類別，表達個別listItem中的view物件集合。
    class ViewTag {
        public TextView stock_name;
        public TextView close;
        public LineChart stockchart;
        public ProgressBar pos_neg_bar;
    }

    private void init_image_loading_options() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
}
