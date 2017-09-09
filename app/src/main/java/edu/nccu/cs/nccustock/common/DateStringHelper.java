package edu.nccu.cs.nccustock.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrjedi on 2015/6/10.
 */
public class DateStringHelper {
    static DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
    public static String dateToString(Date date){
        return df.format(date);
    }
}
