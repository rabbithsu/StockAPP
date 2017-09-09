package edu.nccu.cs.nccustock.data;

import android.os.Environment;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.nccu.cs.nccustock.common.DebugLog;
import edu.nccu.cs.nccustock.parse.eneity.Stock;
import edu.nccu.cs.nccustock.parse.eneity.StockInfo;


public class StockPriceGen {

    public static void genStockData() {
        new Thread(){
            public void run(){
                DebugLog.log("StockPriceGen","start");
                String UTF8 = "utf8";
                File f = new File(Environment.getExternalStorageDirectory() + "/" + "0_a" + "/" + "stock0615.csv");
                ArrayList<String> ids=new ArrayList<>();
                String[] choose_stock={"鴻海","台積電","元大金","兆豐金","台塑","台達電","宏達電"};
                ArrayList<String> stockarray=new ArrayList<>();
                for(int i=0;i!=choose_stock.length;i++){
                    stockarray.add(choose_stock[i]);
                }
                try {
                    FileInputStream fstream = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream,UTF8));
                    String strLine;
                    // 20140609
                    DateFormat df = new SimpleDateFormat("yyyyMMdd");
                    Date target=df.parse("20150526");
                    boolean if_start=true;
                    while ((strLine = br.readLine()) != null) {
                        if(if_start){
                            if_start=false;
                            continue;
                        }

                        // DebugLog.log("stockpricegen",strLine);
                        String [] dat=strLine.split(",");
                        Date currdate=df.parse(dat[2].replaceAll("\\s+",""));
                        if(currdate.before(target)){
                            continue;
                        }

                        String stockName=dat[1].replaceAll("\\s+","");

                        if(!stockarray.contains(stockName)){
                            // continue;
                        }

                        // DebugLog.log("stockpricegen",stockName);
                        String stockID=dat[0].replaceAll("\\s+","");
             /*   if(!ids.contains(stockID)){
                    DebugLog.log("stockgen",dat[1]);
                    DebugLog.log("datdate",dat[2]);
                    ids.add(stockID);
                    ParseObject parseObject=new ParseObject(StockInfo.table_name);
                    parseObject.put(StockInfo.field.stock_id,stockID);
                    parseObject.put(StockInfo.field.stock_name,stockName);
                    parseObject.saveEventually();
                }*/

                        ParseObject stockParseObject=new ParseObject(Stock.table_name);
                        stockParseObject.put(Stock.field.stock_id,dat[0].replaceAll("\\s+",""));
                        stockParseObject.put(Stock.field.stock_name,dat[1].replaceAll("\\s+",""));
                        stockParseObject.put(Stock.field.trade_date,df.parse(dat[2].replaceAll("\\s+","")));
                        stockParseObject.put(Stock.field.close_price,Double.valueOf(dat[3].replaceAll("\\s+","")));
                        DebugLog.log("stockpricegen",df.parse(dat[2].replaceAll("\\s+","")).toString());
                      //  stockParseObject.save();;
                        DebugLog.log("stockpricegen",strLine);

                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();;
                }
            }
        }.start();;

    }
}
