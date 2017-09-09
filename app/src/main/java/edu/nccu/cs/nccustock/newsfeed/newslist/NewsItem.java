package edu.nccu.cs.nccustock.newsfeed.newslist;

import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.Date;

import edu.nccu.cs.nccustock.parse.eneity.News;

/**
 * Created by mrjedi on 2015/6/10.
 */
public class NewsItem {
    public String title;
    public String peak;
    public Date newsDate;
    public String image;
    public String content;
    public NewsItem(ParseObject newsParseObject) {
        content=newsParseObject.getString(News.field.news_content);
        title = newsParseObject.getString(News.field.news_title);
        peak = newsParseObject.getString(News.field.news_content);
        newsDate = newsParseObject.getDate(News.field.news_date);
        if(newsParseObject.has(News.field.image)){
            image=newsParseObject.getString(News.field.image);
        }else{
            image="";
        }

    }
    public NewsItem(JSONObject jsonData) {
        try{
            content=jsonData.getString("content");
            title = jsonData.getString("title");
            peak = jsonData.getString("content");
            newsDate = new Date(Integer.parseInt(jsonData.getString("date").substring(0, 4))-1900,Integer.parseInt(jsonData.getString("date").substring(4, 6))-1
                    , Integer.parseInt(jsonData.getString("date").substring(6)));
            if(true){
                image="";
            }else{
                image="";
            }
        }catch (Exception e){

        }

    }


}
