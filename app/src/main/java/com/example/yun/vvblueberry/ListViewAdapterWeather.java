package com.example.yun.vvblueberry;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yun on 2017-08-24.
 */

public class ListViewAdapterWeather extends BaseAdapter {
    private ArrayList<ListViewItemWeather> listViewItemList = new ArrayList<ListViewItemWeather>();

    public ListViewAdapterWeather(){

    }

    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_weather, parent, false);
        }

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.weaIcon);
        TextView tempTextView = (TextView) convertView.findViewById(R.id.weaTemp);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.weaTime);
        TextView rainTextView = (TextView) convertView.findViewById(R.id.rainCert);

        ListViewItemWeather listViewItem = listViewItemList.get(position);

        iconImageView.setImageResource(listViewItem.getIcon());
        tempTextView.setText(listViewItem.getTemp());
        timeTextView.setText(listViewItem.getTime());
        rainTextView.setText(listViewItem.getRain());

        return convertView;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public Object getItem(int position){
        return listViewItemList.get(position);
    }

    public void addItem(int icon, String temp, String time, String rain){
        ListViewItemWeather item = new ListViewItemWeather();

        item.setIcon(icon);
        item.setTemp(temp);
        item.setTime(time);
        item.setRain(rain);

        listViewItemList.add(item);
    }
}
