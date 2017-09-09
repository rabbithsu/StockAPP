package edu.nccu.cs.nccustock.common;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mrjedi on 2015/6/12.
 */
public class DateCompareHelper {
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return  cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

    }
}
