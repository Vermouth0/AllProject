package com.swufe.wy.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends ArrayAdapter {

    private static final  String TAG="MyAdapter";

    public MyAdapter(Context context, int resource,ArrayList<HashMap<String,String>> list) {
        super(context, resource, list);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        //position:当前位置,根据不同的position返回不同的view；convertView：当前控件；parent：父类控件

        View itemView = convertView; //用于显示每一行的数据
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            //如果没有内容，向布局文件中填充对象；false：不覆盖根布局
        }
        Map<String,String> map = (Map<String,String>)getItem(position);
        TextView title=itemView.findViewById(R.id.itemTitle);
        TextView detail=itemView.findViewById(R.id.itemDetail);

        title.setText("Title:"+map.get("ItemTitle"));
        detail.setText("detail:"+map.get("ItemDetail"));
        return itemView;
    }
}
