package edu.nccu.cs.nccustock.linechat;

import java.util.ArrayList;

/**
 * Created by nccu_dct on 15/10/16.
 */
public class NoticeCenter {

    private static NoticeCenter mNoticeCenter;

    private ArrayList<OnDataChangedListener> mOnDataChangedListener;

    //singleton 確保只有一個實體
    private NoticeCenter(){}

    public static NoticeCenter getInstance(){
        if(null == mNoticeCenter){
            mNoticeCenter = new NoticeCenter();
            mNoticeCenter.init();
        }
        return mNoticeCenter;
    }

    private void init(){
        mOnDataChangedListener = new ArrayList<OnDataChangedListener>();
    }
    //observe pattern
    public interface OnDataChangedListener{
        public void onDataChanged(String msg);
    }

    public void addOnDataChangedListener(OnDataChangedListener listener){
        mOnDataChangedListener.add(listener);
    }

    public void removeOnDataChangedListener(OnDataChangedListener listener){
        mOnDataChangedListener.remove(listener);
    }

    public void notifyDataChanged(String msg){
        for(OnDataChangedListener listener : mOnDataChangedListener){
            if(listener != null){
                listener.onDataChanged(msg);
            }
        }
    }
}
