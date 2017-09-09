package edu.nccu.cs.nccustock.newsfeed.newslist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateStringHelper;

public class NewsAdapter extends BaseAdapter {
    List<NewsItem> data;
    private LayoutInflater myInflater;
    private static final int type_top = 0;
    private static final int type_other = 1;
    private DisplayImageOptions options;

    public NewsAdapter(Context ctxt) {
        myInflater = LayoutInflater.from(ctxt);
        this.data = new ArrayList<>();
        init_image_loading_options();
    }

    public void updateData(List<NewsItem> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public NewsItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return type_top;
        } else {
            return type_other;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //自訂類別，表達個別listItem中的view物件集合。
        ViewTag viewTag = null;
        TopView topView = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case type_top:
                    convertView = myInflater.inflate(R.layout.feed_top_view,parent, false);
                    topView = new TopView();
                    topView.title = (TextView) convertView.findViewById(R.id.top_title);
                    topView.content = (TextView) convertView.findViewById(R.id.top_content);
                    topView.datdate = (TextView) convertView.findViewById(R.id.top_datdate);
                    topView.image = (ImageView) convertView.findViewById(R.id.top_image);
                    topView.panel = (RelativeLayout) convertView.findViewById(R.id.top_panel);
                    //設置容器內容
                    convertView.setTag(topView);
                    break;
                case type_other:
                    convertView = myInflater.inflate(R.layout.adapter,parent, false);
                    viewTag = new ViewTag();
                    viewTag.title = (TextView) convertView.findViewById(R.id.news_item_title);
                    viewTag.content = (TextView) convertView.findViewById(R.id.news_item_content);
                    viewTag.datdate = (TextView) convertView.findViewById(R.id.news_item_date);
                    viewTag.image=(ImageView)convertView.findViewById(R.id.list_image);
                    //設置容器內容
                    convertView.setTag(viewTag);
                    break;
            }

        } else {
            switch (type) {
                case type_top:
                    topView = (TopView) convertView.getTag();
                    break;
                case type_other:
                    viewTag = (ViewTag) convertView.getTag();
                    break;
            }

        }
        NewsItem newsItem = (NewsItem) getItem(position);
        switch (type) {
            case type_top:
                ImageLoader.getInstance().displayImage(newsItem.image, topView.image, options);
                topView.title.setText(newsItem.title);
                topView.content.setText(newsItem.peak);
                topView.datdate.setText(DateStringHelper.dateToString(newsItem.newsDate));
                break;
            case type_other:
                ImageLoader.getInstance().displayImage(newsItem.image, viewTag.image, options);
                viewTag.title.setText(newsItem.title);
                viewTag.content.setText(newsItem.peak);
                viewTag.datdate.setText(DateStringHelper.dateToString(newsItem.newsDate));
                break;
        }


        return convertView;
    }

    //自訂類別，表達個別listItem中的view物件集合。
    class ViewTag {
        public TextView title;
        public TextView content;
        public TextView datdate;
        public ImageView image;
    }

    class TopView {
        public RelativeLayout panel;
        public ImageView image;
        public TextView title;
        public TextView content;
        public TextView datdate;
    }

    private void init_image_loading_options() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.loading)
                .showImageOnFail(R.drawable.loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

}
