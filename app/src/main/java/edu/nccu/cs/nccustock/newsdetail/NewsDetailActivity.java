package edu.nccu.cs.nccustock.newsdetail;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.content.DialogInterface.OnClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import edu.nccu.cs.nccustock.R;
import edu.nccu.cs.nccustock.common.DateStringHelper;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;


@EActivity
public class NewsDetailActivity extends ActionBarActivity {
    private DisplayImageOptions options;

    @Extra("title")
    String title;
    @Extra("datdate")
    String datdate;
    @Extra("content")
    String content;
    @Extra("image")
    String image;
    @Extra("id")
    String id;
    @Extra("name")
    String name;

    @ViewById
    TextView news_title;
    @ViewById
    TextView news_date;
    @ViewById
    TextView news_content;
    @ViewById
    ImageView news_image;



    Spannable titlespan;
    Spannable contentspan;
    Spannable datespan;

    Button sizebutton;

    public int size=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        contentspan = new SpannableString(content);
        titlespan = new SpannableString(title);
        datespan = new SpannableString(datdate);
        sizebutton = (Button) findViewById(R.id.fontsize);

        sizebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesize();
            }
        });

        setData();

        setActionBar();
    }

    private void setData() {

        //settitlecolor(name);
        if(name!=null) {
            setcolor(0, name, content);
            setcolor(0, id, content);
            settitle(0, name, title);
        }
        //setcolor(contentspan, id);
        //contentspan.setSpan(new ForegroundColorSpan(Color.RED), content.indexOf(name), content.indexOf(name)+name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        news_title.setText(titlespan);
        news_content.setText(contentspan);
        news_date.setText(datespan);
        if (!image.equals("")) {
            news_image.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(image, news_image, options);
        }
    }

    private void setActionBar() {
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
    /*public Spannable settitlecolor(String c){
        if (word.toString().contains(c)){
            return settitlecolor(w, c);
        }else{
            return w;
        }
    }*/
    public void setcolor(int i, String n, String w){
        if(w.indexOf(n)==-1){

        }
        else{
            int c = w.indexOf(n)+n.length();
            contentspan.setSpan(new ForegroundColorSpan(Color.RED), i+c-n.length(), i+c, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            setcolor(i + c, n,w.substring(c));
        }
    }

    public void settitle(int i, String n, String w){
        if(w.indexOf(n)==-1){

        }
        else{
            int c = w.indexOf(n)+n.length();
            titlespan.setSpan(new ForegroundColorSpan(Color.RED), i+c-n.length(), i+c, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            settitle(i + c, n, w.substring(c));
        }
    }

    public void changesize(){
        titlespan.setSpan(new AbsoluteSizeSpan(20+(size*4), true), 0, titlespan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentspan.setSpan(new AbsoluteSizeSpan(16+(size*4), true), 0, contentspan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        datespan.setSpan(new AbsoluteSizeSpan(14+(size*4), true), 0, datespan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        news_title.setText(titlespan);
        news_content.setText(contentspan);
        news_date.setText(datespan);
        if(size ==2)
            size = 0;
        else
            size++;
    }



}
