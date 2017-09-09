package edu.nccu.cs.nccustock.entity;

import com.orm.SugarRecord;

public class StockPosNegEntity extends SugarRecord<StockPosNegEntity> {
    public String stockid;
    public int pos;
    public int neg;
    public String dateindex;
}
