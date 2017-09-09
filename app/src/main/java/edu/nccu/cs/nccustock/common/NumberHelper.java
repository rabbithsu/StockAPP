package edu.nccu.cs.nccustock.common;

/**
 * Created by mrjedi on 2015/6/11.
 */
public class NumberHelper {
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
