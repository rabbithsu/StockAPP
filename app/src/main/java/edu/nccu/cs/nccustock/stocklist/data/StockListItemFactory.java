package edu.nccu.cs.nccustock.stocklist.data;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.entity.FeaturedStock;
import edu.nccu.cs.nccustock.parse.eneity.Stock;
import edu.nccu.cs.nccustock.stocklist.model.StockItem;

/**
 * Created by mrjedi on 2015/6/11.
 */
public class StockListItemFactory {
    public static void getStockItems(final OnGetStockItemCompleted onGetStockItemCompleted) {
        new Thread() {
            public void run() {
                try {
                    // demo date
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date target_date = new Date();

                    ArrayList<StockItem> stockItems = new ArrayList<>();
                    // use featured stocks firet
                    List<FeaturedStock> featuredStocks = FeaturedStock.listAll(FeaturedStock.class);
                    for (int i = 0; i != featuredStocks.size(); i++) {
                        StockItem stockItem = new StockItem();
                        stockItem.datdate = target_date;
                        stockItem.stockname = featuredStocks.get(i).stockname;
                        stockItem.stockid = featuredStocks.get(i).stockid;
                        stockItem.stockPriceEntities = StockPriceFactory.getStockPricePast(target_date, stockItem.stockid, 14);
                        if (stockItem.stockPriceEntities.size() != 0)
                            stockItem.setClose(stockItem.stockPriceEntities.get(stockItem.stockPriceEntities.size() - 1).close);
                        /*ParseQuery parseQuery=new ParseQuery(Stock.table_name);
                        parseQuery.whereEqualTo(Stock.field.trade_date,target_date);
                        parseQuery.whereEqualTo(Stock.field.stock_id,featuredStocks.get(i).stockid);
                        List<ParseObject> parseObjects=parseQuery.find();
                        if(parseObjects.size()!=0){
                            stockItem.setClose(parseObjects.get(0).getDouble(Stock.field.close_price));
                        }*/
                        stockItems.add(stockItem);
                    }
                    onGetStockItemCompleted.done(stockItems, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    ;
                    onGetStockItemCompleted.done(null, null);
                }
            }
        }.start();

    }
}
