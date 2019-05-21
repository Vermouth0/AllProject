package com.swufe.wy.test;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements  Runnable {
    String data[]={"please waiting"}; //可以创建数组或列表对象（列表？）
    Handler handler;
    private  final String  TAG="rate";

    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        Log.i("List","lastRateDateStr=" + logDate);
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

        Log.i("List","run...");
        //获取网络数据，带回主线程
        List<String> retList=new ArrayList<String>();
        Message msg = handler.obtainMessage();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run","curDateStr:" + curDateStr + " logDate:" + logDate);
        if(curDateStr.equals(logDate)){
            //如果相等，则不从网络中获取数据
            Log.i("run","日期相等，从数据库中获取数据");
            RateManager dbManager = new RateManager(RateListActivity.this);
            for(RateItem rateItem : dbManager.listAll()){
                retList.add(rateItem.getCurName() + "=>" + rateItem.getCurRate());
            }
        }else {
            Log.i("run", "日期不相等，从网络中获取在线数据");
            Document doc = null;
            try {
                Thread.sleep(1000);
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                Log.i(TAG, "run:" + doc.title()); //获得title
                Elements tables = doc.getElementsByTag("table");
                int i = 1;
                for (Element table : tables) {
                    Log.i(TAG, "run: table[" + i + "]" + table);
                    i++;
                } //查找出所需数据在哪个table里
                Element table1 = tables.get(0);//通过日志看出在table1，但是索引是从0开始
                //Log.i(TAG, "run:table1" + table1);
                //获取td中的数据
                Elements tds = table1.getElementsByTag("td");
                //定义ratelist
                List<RateItem> rateList = new ArrayList<RateItem>();

                for (int j = 0; j < tds.size(); j += 6) {
                    Element td1 = tds.get(j); //td1:汇率名；td2：数值
                    Element td2 = tds.get(j + 5);
                    String str1 = td1.text();
                    String val = td2.text();
                    Log.i(TAG, "run:" + str1 + "==>" + val);
                    //带回界面进行显示
                    retList.add(str1 + "==>" + val);
                    //往rateList中添加数据，带回数据库
                    rateList.add(new RateItem(str1, val));

                    //把数据写入到数据库中
                    RateManager manager = new RateManager(this);
                    manager.deleteAll(); //增加前清空数据
                    manager.addAll(rateList);

                    //记录更新日期
                    SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(DATE_SP_KEY, curDateStr);
                    edit.commit();
                    Log.i("run","更新日期结束：" + curDateStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        msg=handler.obtainMessage(7);
        //msg.what=5;
        msg.obj=retList;
        handler.sendMessage(msg);
    }
}
