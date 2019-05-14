package com.swufe.wy.test;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {

    Handler handler;
    private List<HashMap<String,String>> listItems; //存放文字、图片信息
    private SimpleAdapter listItemAdapter; //适配器
    private  final String  TAG="list2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2); 不使用默认布局
        initListView();

        //MyAdapter myAdapter=new MyAdapter(this,R.layout.list_item,listItems);  //创建MyAdapter对象
        //this.setListAdapter(myAdapter); //使用自定义的myAdapter
        this.setListAdapter(listItemAdapter); //应用adapter

        Thread t=new Thread(this);
        t.start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    listItems=(List<HashMap<String, String>>)msg.obj;
                    listItemAdapter=new SimpleAdapter(MyList2Activity.this,listItems, //listItem数据源
                            R.layout.list_item, //ListItem的XML布局实现
                            new String[]{"ItemTitle","ItemDetail"}, //数据的key
                            new int[]{R.id.itemTitle,R.id.itemDetail}//布局里的id，k与id一一匹配
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };//注：有分号

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
        }


    private void initListView(){
        int i;
        listItems=new ArrayList<HashMap<String, String>>();
        for(i=0;i<10;i++);{
            HashMap<String, String> map =new HashMap<String, String>();
            map.put("ItemTitle","Rate:"+ i); //标题文字
            map.put("ItemDetail","detail:" + i); //详情描述
            listItems.add(map); //往列表里添加数据
        }
        //生成适配器Item和动态数组对应元素
        listItemAdapter=new SimpleAdapter(this,listItems, //listItem数据源
                R.layout.list_item, //ListItem的XML布局实现
                new String[]{"ItemTitle","ItemDetail"}, //数据的key
                new int[]{R.id.itemTitle,R.id.itemDetail} //布局里的id，k与id一一匹配
                );
    }

    @Override
    public void run() {
        List<HashMap<String, String>> retList=new ArrayList<HashMap<String, String>>();

        Document doc = null;
        try {
            Thread.sleep(1000);
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run:"+doc.title()); //获得title
            Elements tables=doc.getElementsByTag("table");
           int i=1;
            for (Element table : tables) {
                Log.i(TAG,"run: table["+i+"]"+table);
                i++;
            } //查找出所需数据在哪个table里
            Element table1=tables.get(0);//通过日志看出在table1，但是索引是从0开始
            Log.i(TAG,"run:table1"+table1 );
            //获取td中的数据
            Elements tds =table1.getElementsByTag("td");

            for(int j=0;j<tds.size();j+=6) {
                Element td1=tds.get(j); //td1:汇率名；td2：数值
                Element td2=tds.get(j+5);
                String str1=td1.text();
                String val=td2.text();
                Log.i(TAG,"run:"+str1+"==>"+val);
                HashMap<String, String> map =new HashMap<String, String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                retList.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message msg=handler.obtainMessage(7);
        //msg.what=5;
        msg.obj=retList;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent:"+parent);
        Log.i(TAG, "onItemClick: view"+view);
        Log.i(TAG, "onItemClick: position"+position);
        Log.i(TAG, "onItemClick: id"+id);

        //通过map获取数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr=map.get("ItemTitle");
        String detailStr=map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr"+titleStr);
        Log.i(TAG, "onItemClick: detailStr"+detailStr);

        //通过view获取数据
        TextView title=view.findViewById(R.id.itemTitle);
        TextView detail=view.findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String Detail2=String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2"+title2);
        Log.i(TAG, "onItemClick: Detail2"+Detail2);

        //打开新的界面，传递参数
        Intent rateCalc=new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr)); //valueOf:Float；parseFloat：float
        startActivity(rateCalc);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {  //为了让下层的方法接收到final
        Log.i(TAG, "onItemLongClick: 长按列表项position"+position);
        //注：长按也可激活点击事件。解决方法：return：true
        //删除操作
        //listItems.remove(position);
        //listItemAdapter.notifyDataSetChanged();
        //Log.i(TAG, "onItemLongClick: size"+listItems.size());
        //构造对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除").setPositiveButton("是", new DialogInterface.OnClickListener() { //匿名对象
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        })
        .setNegativeButton("否",null);
        builder.create().show();
        return true;
    }
}
