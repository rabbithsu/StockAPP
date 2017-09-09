package edu.nccu.cs.nccustock.stocklist.data;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.common.DateStringHelper;
import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.entity.StockInfoHelper;
import edu.nccu.cs.nccustock.entity.StockPriceEntity;
import edu.nccu.cs.nccustock.parse.eneity.Stock;

/**
 * Created by mrjedi on 2015/6/11.
 */
public class StockPriceFactory {
    public static final String tag="StockPriceFactory";
    public static ArrayList<StockPriceEntity> getStockPricePast(Date currDate,String stockid,int apart) throws ParseException {
        ArrayList<StockPriceEntity> stockPriceEntities=new ArrayList<>();
        Calendar start = Calendar.getInstance();
        start.setTime(currDate);
        start.add(Calendar.DATE, -1*apart);
        Calendar end = Calendar.getInstance();
        end.setTime(currDate);
        //DebugLog.log(tag,"getting data:"+start.getTime().toString()+" "+end.getTime().toString());
        while( !start.after(end)){
            Date targetDay = start.getTime();
            StockPriceEntity stockPriceEntity= StockInfoHelper.getStockPriceEntityByDateID(stockid, DateStringHelper.dateToString(targetDay));
            if(stockPriceEntity==null){
                // query
                /*ParseQuery parseQuery=new ParseQuery(Stock.table_name);
                parseQuery.whereEqualTo(Stock.field.trade_date,targetDay);
                parseQuery.whereEqualTo(Stock.field.stock_id,stockid);
                List<ParseObject> parseObjectList=parseQuery.find();*/
                /*if(parseObjectList.size()!=0){
                    ParseObject stock=parseObjectList.get(0);
                     stockPriceEntity=new StockPriceEntity();
                    stockPriceEntity.close=stock.getDouble(Stock.field.close_price);
                    stockPriceEntity.datdate=targetDay;
                    stockPriceEntity.stockid=stockid;
                    stockPriceEntity.dateindex=DateStringHelper.dateToString(targetDay);
                    stockPriceEntity.save();
                    DebugLog.log(tag,"saved stock price:"+stockid+" "+targetDay.toString()+" "+String.valueOf(stockPriceEntity.close));
                }else{*/
                    stockPriceEntity=new StockPriceEntity();
                    stockPriceEntity.close=-1; // no data
                    stockPriceEntity.datdate=targetDay;
                    stockPriceEntity.stockid=stockid;
                    stockPriceEntity.dateindex=DateStringHelper.dateToString(targetDay);
                    stockPriceEntity.save();
                //}
            }else{
                //DebugLog.log(tag,"alreay have:"+stockid+" "+targetDay.toString()+" "+String.valueOf(stockPriceEntity.close));
            }
            if(stockPriceEntity.close>0) {
                stockPriceEntities.add(stockPriceEntity);
            }
            DebugLog.log(tag,targetDay.toString());
            start.add(Calendar.DATE, 1);

        }

        return stockPriceEntities;
    }
}
