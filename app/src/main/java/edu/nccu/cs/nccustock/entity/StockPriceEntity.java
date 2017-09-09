package edu.nccu.cs.nccustock.entity;

import com.orm.SugarRecord;

import java.util.Date;

public class StockPriceEntity extends SugarRecord<StockPriceEntity> {
    public String stockid;
    public double close;
    public Date datdate;
    public String dateindex;
    public int pos=0;
    public int neg=0;
}
