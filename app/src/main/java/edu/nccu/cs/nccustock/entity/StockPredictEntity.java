package edu.nccu.cs.nccustock.entity;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by hanjord on 15/9/5.
 */
public class StockPredictEntity extends SugarRecord<StockPredictEntity > {
    public String stockid;
    public double predict;
    public Date datdate;
    public String dateindex;
}
