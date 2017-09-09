package edu.nccu.cs.nccustock.stocklist.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.nccu.cs.nccustock.entity.StockPriceEntity;

/**
 * Created by mrjedi on 2015/6/11.
 */
public class StockItem {
    static DecimalFormat df = new DecimalFormat("0.00");
    public String stockname;
    public String yesterday_close;
    public String stockid;
    public int pos=-1;
    public int neg=-1;
    public Date datdate;
    public ArrayList<StockPriceEntity> stockPriceEntities = new ArrayList<>();

    public void setClose(double close) {
        yesterday_close = df.format(close);
    }

    public StockItem() {

    }
}
