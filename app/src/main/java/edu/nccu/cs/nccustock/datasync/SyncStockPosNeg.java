package edu.nccu.cs.nccustock.datasync;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.entity.FeaturedStock;
import edu.nccu.cs.nccustock.entity.StockInfoHelper;
import edu.nccu.cs.nccustock.entity.StockPosNegEntity;
import edu.nccu.cs.nccustock.parse.eneity.stock_news;

public class SyncStockPosNeg {
    public static void sync() {
        new Thread() {
            public void run() {
                try {
                    ArrayList<Date> dates=new ArrayList<Date>();
                    ParseQuery parseQuery=new ParseQuery(stock_news.table_name);
                    parseQuery.setLimit(1000);
                    parseQuery.orderByDescending(stock_news.field.news_date);
                    List<ParseObject> parseObjects=parseQuery.find();
                    for(int i=0;i!=parseObjects.size();i++){
                        Date date=parseObjects.get(i).getDate(stock_news.field.news_date);
                        if(!dates.contains(date)){
                            dates.add(date);
                        }
                    }
                   /* for(int i=0;i!=dates.size();i++) {
                        List<FeaturedStock> featuredStockList = FeaturedStock.listAll(FeaturedStock.class);
                        for (int i = 0; i != featuredStockList.size(); i++) {
                            String stockid = featuredStockList.get(i).stockid;
                            StockPosNegEntity stockPosNegEntity = StockInfoHelper.getStockPosNegEntityById(stockid);
                            if (stockPosNegEntity == null) {
                                stockPosNegEntity = new StockPosNegEntity();
                            }
                            ParseQuery parseQuery = new ParseQuery(stock_news.table_name);
                            parseQuery.whereEqualTo(stock_news.field.stock_name, featuredStockList.get(i).stockname);
                            int pos = parseQuery.count();
                            stockPosNegEntity.save();
                        }
                    }*/
                } catch (Exception e) {

                }

            }

        }.start();
    }
}
