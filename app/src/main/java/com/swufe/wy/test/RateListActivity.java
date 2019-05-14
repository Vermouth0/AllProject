package com.swufe.wy.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements  Runnable {
    String data[]={"please waiting"}; //可以创建数组或列表对象（列表？）
    Handler handler;
    private  final String  TAG="rate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list); 类中已经有页面布局，因此不需要再填充
        final List<String> list1=new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }

        ListAdapter adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);//(当前对象，布局，数据)
        //  String：泛型，数据项是字符串的集合
        setListAdapter(adapter);
       //开启线程
        Thread t=new Thread(this);
        t.start();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    List<String> list2=(List<String>)msg.obj;
                    ListAdapter adapter= new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    //如果只用list表示handle
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };//注：有分号
    }

    @Override
    public void run() {
        //获取网络数据，带回主线程
        List<String> retList=new ArrayList<String>();

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
                retList.add(str1+"==>"+val);
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
}
