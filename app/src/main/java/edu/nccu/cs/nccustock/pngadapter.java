package edu.nccu.cs.nccustock;

import android.widget.BaseAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by rabbithsu on 2016/5/3.
 */
public class pngadapter extends BaseAdapter {
    private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;
    private int[] png;


    public pngadapter(Activity a, int[] p, String[] d) {
        data = d;
        png = p;
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.drawer_list, null);

        TextView text=(TextView)vi.findViewById(R.id.title);
        ImageView image=(ImageView)vi.findViewById(R.id.img);
        text.setText(data[position]);
        image.setImageResource(png[position]);
        return vi;
    }
}

