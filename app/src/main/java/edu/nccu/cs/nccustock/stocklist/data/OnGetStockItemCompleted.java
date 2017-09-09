package edu.nccu.cs.nccustock.stocklist.data;

import java.util.ArrayList;

import edu.nccu.cs.nccustock.stocklist.model.StockItem;

/**
 * Created by mrjedi on 2015/6/11.
 */
public interface OnGetStockItemCompleted {
    public void done(ArrayList<StockItem> stockItems,Exception e);
}
