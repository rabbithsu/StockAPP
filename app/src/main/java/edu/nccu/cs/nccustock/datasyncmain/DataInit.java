package edu.nccu.cs.nccustock.datasyncmain;

import android.app.Activity;
import android.content.SharedPreferences;

import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.common.NumberHelper;
import edu.nccu.cs.nccustock.entity.FeaturedStock;
import edu.nccu.cs.nccustock.entity.StockInfoEntity;
import edu.nccu.cs.nccustock.entity.StockInfoHelper;
import edu.nccu.cs.nccustock.entity.StockPredictEntity;
import edu.nccu.cs.nccustock.entity.StockPriceEntity;
import edu.nccu.cs.nccustock.parse.eneity.Stock;
import edu.nccu.cs.nccustock.parse.eneity.StockInfo;
import edu.nccu.cs.nccustock.parse.eneity.stock_predict;

/**
 * Created by hanjord on 15/6/14.
 */
public class DataInit {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static void init_data(Activity activity,OnDataInitComplete onDataInitComplete){
        onDataInitComplete.done(null);
        /*SharedPreferences settings = activity.getSharedPreferences("SYNC_STATE", 0);
        String LastDate = settings.getString("SYNC_DATE", "2010-01-01");
        Date curr=new Date();
        if(LastDate.equals(sdf.format(curr))){
            // already sync
            onDataInitComplete.done(null);
        }else {
            syncStockInfo(onDataInitComplete,settings);
        }*/
    }

    public static void syncStockInfo(final OnDataInitComplete onDataInitComplete, final SharedPreferences settings) {
        new Thread(){
            public void run(){
                try {
                    Calendar calendar = Calendar.getInstance(); // this would default to now
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, -20);

                   // Date lastDate=calendar.getTime();
                   // DebugLog.log("last date",lastDate.toString());
                    int skip = 0;
                    int limit = 1000;
                    ArrayList<String> syncstockids=new ArrayList<String>();
                    while (true) {
                        ParseQuery parseQuery = new ParseQuery(StockInfo.table_name);
                        parseQuery.whereEqualTo(StockInfo.field.recommend,true);
                        parseQuery.setLimit(1000);
                        parseQuery.setSkip(skip);
                        List<ParseObject> parseObjects = parseQuery.find();
                        for (int i = 0; i != parseObjects.size(); i++) {
                            String stockid = parseObjects.get(i).getString(StockInfo.field.stock_id);
                            String stockname = parseObjects.get(i).getString(StockInfo.field.stock_name);
                            if(NumberHelper.isNumeric(stockid)) {

                                if(parseObjects.get(i).containsKey(StockInfo.field.recommend)) {
                                    if (StockInfoHelper.getStockInfoEntityById(stockid) == null) {
                                        DebugLog.log("syncstock", stockname);
                                        StockInfoEntity stockInfoEntity = new StockInfoEntity();
                                        stockInfoEntity.stockid = stockid;
                                        stockInfoEntity.stockname = stockname;
                                        stockInfoEntity.save();
                                    }
                                    if(!syncstockids.contains(stockid)){
                                        syncstockids.add(stockid);
                                    }
                                    if(parseObjects.get(i).getBoolean(StockInfo.field.recommend)==true) {
                                        if (StockInfoHelper.getFuturedFeaturedStockById(stockid) == null) {
                                            DebugLog.log("sync recommend stock", stockname);
                                            FeaturedStock FeaturedStock = new FeaturedStock();
                                            FeaturedStock.stockid = stockid;
                                            FeaturedStock.stockname = stockname;
                                            FeaturedStock.save();
                                        }
                                    }
                                }
                            }
                        }
                        if (parseObjects.size() == limit) {
                            // query for more
                            skip += limit;
                        } else {
                            break;
                        }
                    }
                    for(int j=0;j!=syncstockids.size();j++){
                        String stockid=syncstockids.get(j);
                        skip = 0;
                        limit = 1000;
                        while (true) {
                            ParseQuery parseQuery=new ParseQuery(Stock.table_name);

                            parseQuery.whereEqualTo(Stock.field.stock_id,stockid);
                            parseQuery.setLimit(1000);
                            parseQuery.setSkip(skip);
                            List<ParseObject> parseObjectList = parseQuery.find();
                            for(int i=0;i!=parseObjectList.size();i++){
                                ParseObject stock=parseObjectList.get(i);
                                java.util.Date targetDay=stock.getDate(Stock.field.trade_date);

                                double close=stock.getDouble(Stock.field.close_price);
                                DebugLog.log("syncprice",stockid);
                                StockPriceEntity stockPriceEntity= StockInfoHelper.getStockPriceEntityByDateID(stockid,targetDay.toString());
                                if(stockPriceEntity==null){
                                    stockPriceEntity=new StockPriceEntity();
                                    stockPriceEntity.close=close; // no data
                                    stockPriceEntity.datdate=targetDay;
                                    stockPriceEntity.stockid=stockid;
                                    stockPriceEntity.dateindex= DateStringHelper.dateToString(targetDay);
                                    stockPriceEntity.save();
                                    DebugLog.log("sync price",stockPriceEntity.stockid+" "+stockPriceEntity.dateindex);
                                }
                            }

                            if ( parseObjectList.size() == limit) {
                                // query for more
                                skip += limit;
                            } else {
                                break;
                            }
                        }
                    }

                    for(int j=0;j!=syncstockids.size();j++){
                        String stockid=syncstockids.get(j);
                        skip = 0;
                        limit = 1000;
                        while (true) {
                            ParseQuery parseQuery=new ParseQuery(stock_predict.table_name);

                            parseQuery.whereEqualTo(stock_predict.field.stock_id,stockid);
                            parseQuery.setLimit(1000);
                            parseQuery.setSkip(skip);
                            List<ParseObject> parseObjectList = parseQuery.find();
                            for(int i=0;i!=parseObjectList.size();i++){
                                ParseObject stock=parseObjectList.get(i);
                                java.util.Date targetDay=stock.getDate(stock_predict.field.predict_date);
                                double predict=stock.getDouble(stock_predict.field.predict_price);
                                DebugLog.log("syncpredict",stockid);
                                StockPredictEntity stockPredictEntity= StockInfoHelper.getStockPredictEntityByDateID(stockid, targetDay.toString());
                                if(stockPredictEntity==null) {
                                    stockPredictEntity=new StockPredictEntity();
                                    stockPredictEntity.predict = predict;
                                    stockPredictEntity.datdate = targetDay;
                                    stockPredictEntity.stockid = stockid;
                                    stockPredictEntity.dateindex = DateStringHelper.dateToString(targetDay);
                                    stockPredictEntity.save();
                                }
                            }

                            if ( parseObjectList.size() == limit) {
                                // query for more
                                skip += limit;
                            } else {
                                break;
                            }
                        }
                    }

                    SharedPreferences.Editor PE = settings.edit();
                    PE.putString("SYNC_DATE",sdf.format(new Date()));
                    PE.commit();

                    onDataInitComplete.done(null);
                }catch (Exception e){
                    onDataInitComplete.done(e);
                }
            }
        }.start();

    }
}
