package com.example.yun.vvblueberry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yun on 2017-10-15.
 */

public class ListViewAdapterOption extends BaseAdapter {
    private ArrayList<ListViewItemOption> listViewItemList = new ArrayList<ListViewItemOption>();

    public ListViewAdapterOption(){

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
            convertView = inflater.inflate(R.layout.listitem_option, parent, false);
        }

        ImageView optIcon = (ImageView) convertView.findViewById(R.id.optIcon);
        TextView optTitle = (TextView) convertView.findViewById(R.id.optTitle);

        ListViewItemOption listViewItem = listViewItemList.get(position);

        optIcon.setImageResource(listViewItem.getIcon());
        optTitle.setText(listViewItem.getTitle());

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

    public void addItem(int icon, String title){
        ListViewItemOption item = new ListViewItemOption();

        item.setIcon(icon);
        item.setTitle(title);

        listViewItemList.add(item);
    }
}
