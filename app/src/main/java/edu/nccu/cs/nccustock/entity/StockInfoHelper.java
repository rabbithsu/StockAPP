package edu.nccu.cs.nccustock.entity;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Date;
import java.util.List;

import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.entity.StockInfoEntity;

/**
 * Created by mrjedi on 2015/6/11.
 */
public class StockInfoHelper {

    public static StockPredictEntity getStockPredictEntityByDateID(
            String stockid,String date) {
        List<StockPredictEntity > stockInfoEntities = Select.from(StockPredictEntity.class)
                .where(Condition.prop("stockid").eq(stockid),
                        Condition.prop("dateindex").eq(date))
                .list();
        DebugLog.log("getStockPriceEntityByDateID","size="+String.valueOf(stockInfoEntities.size()));
        if (stockInfoEntities.size() != 0) {
            return stockInfoEntities.get(0);
        } else {
            return null;
        }
    }

    public static StockPriceEntity getStockPriceEntityByDateID(
            String stockid,String date) {
        List<StockPriceEntity > stockInfoEntities = Select.from(StockPriceEntity.class)
                .where(Condition.prop("stockid").eq(stockid),
                        Condition.prop("dateindex").eq(date))
                .list();
        DebugLog.log("getStockPriceEntityByDateID","size="+String.valueOf(stockInfoEntities.size()));
        if (stockInfoEntities.size() != 0) {
            return stockInfoEntities.get(0);
        } else {
            return null;
        }
    }

    public static StockPosNegEntity  getStockPosNegEntityById(
            String stockid,String date) {
        List<StockPosNegEntity > stockInfoEntities = Select.from(StockPosNegEntity.class)
                .where(Condition.prop("stockid").eq(stockid),
                        Condition.prop("dateindex").eq(date))
                .list();
        if (stockInfoEntities.size() != 0) {
            return stockInfoEntities.get(0);
        } else {
            return null;
        }
    }


    public static StockInfoEntity  getStockInfoEntityById(
            String stockid) {
        List<StockInfoEntity > stockInfoEntities = StockInfoEntity.find(
                StockInfoEntity.class, "stockid=?", stockid);
        if (stockInfoEntities.size() != 0) {
            return stockInfoEntities.get(0);
        } else {
            return null;
        }
    }

    public static FeaturedStock  getFuturedFeaturedStockById(
            String remobjId) {
        List<FeaturedStock > stockInfoEntities = FeaturedStock.find(
                FeaturedStock.class, "stockid=?", remobjId);
        if (stockInfoEntities.size() != 0) {
            return stockInfoEntities.get(0);
        } else {
            return null;
        }
    }
}
