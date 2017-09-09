package edu.nccu.cs.nccustock.linechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.nccu.cs.nccustock.R;
import java.util.List;

/**
 * Created by Rabbit徐 on 2015/7/22.
 */
public class MessageAdapter extends BaseAdapter {
    private Context mContext;
    private List<XMPPMessage> mData;

    public MessageAdapter(Context context, List<XMPPMessage> data)
    {
        this.mContext=context;
        this.mData=data;
    }

    public void Refresh()
    {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int Index)
    {
        return mData.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
        switch(mData.get(Index).getType())
        {
            case XMPPMessage.MessageType_Time:

                break;
            case XMPPMessage.MessageType_To:
                mView= LayoutInflater.from(mContext).inflate(R.layout.message_out, null);
                Content=(TextView)mView.findViewById(R.id.out);
                Content.setText(ChatRoom.MyName+": "+mData.get(Index).getContent());
                break;
            case XMPPMessage.MessageType_From:
                mView= LayoutInflater.from(mContext).inflate(R.layout.message_in, null);
                Content=(TextView)mView.findViewById(R.id.in);
                Content.setText(mData.get(Index).getName()+": "+mData.get(Index).getContent());
                break;
            case XMPPMessage.MessageType_Broadcast:
                mView= LayoutInflater.from(mContext).inflate(R.layout.message_out, null);
                Content=(TextView)mView.findViewById(R.id.out);
                Content.setText("Broadcast: "+mData.get(Index).getContent());
                break;
        }
        return mView;
    }

}
