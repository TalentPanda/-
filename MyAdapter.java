package com.demo.demos.FindU.SearchByWiFi.core.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.demo.demos.FindU.SearchByWiFi.core.ListItem;
import com.demo.demos.R;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {

    List<ListItem> maclist;
    LayoutInflater inflater;

    public MyAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(List<ListItem> list){
        this.maclist = list;
    }

    @Override
    public int getCount() {
        return maclist.size();
    }

    @Override
    public Object getItem(int position) {
        return maclist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (holder == null){
            convertView = inflater.inflate(R.layout.item,null);
            holder = new ViewHolder();
            holder.ip = convertView.findViewById(R.id.ip_textview);
            holder.mac = convertView.findViewById(R.id.mac_textview);
            holder.firm = convertView.findViewById(R.id.firm_textview);
            holder.result = convertView.findViewById(R.id.result_textview);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.d("position", String.valueOf(position));
        ListItem item = maclist.get(position);
        holder.ip.setText(item.getIp());
        holder.mac.setText(item.getMac());
        holder.firm.setText(item.getFirm());
        holder.result.setText(item.getResult());

        return convertView;
    }



    public static class ViewHolder {
        TextView ip;
        TextView mac;
        TextView firm;
        TextView result;
    }

}
