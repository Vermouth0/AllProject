package com.swufe.wy.test;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Rate extends AppCompatActivity implements  Runnable{

    EditText rmb;
    TextView showOut;
    private float dollarRate=0.1f;
    private float euroRate=0.2f;
    private float wonRate=0.3f;
    private String updateDate="";
    private  final String  TAG="rate";
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb=findViewById(R.id.rmb);
        showOut=findViewById(R.id.showOut);

        //获取sp里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            //读取并改变
        dollarRate=sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate=sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate=sharedPreferences.getFloat("won_rate",0.0f);
        updateDate=sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today=Calendar.getInstance().getTime();
        //把时间格式转换为字符串格式
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//MM:月；mm:分钟
        final String todayStr=sdf.format(today);

        Log.i(TAG,"onCreate: sp dollarRate"+dollarRate);
        Log.i(TAG,"onCreate: sp euroRate"+euroRate);
        Log.i(TAG,"onCreate: sp wonRate"+wonRate);
        Log.i(TAG,"onCreate: sp updateDate"+updateDate);
        Log.i(TAG,"onCreate: sp todayStr"+todayStr);
        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG,"onCreate:需要更新");
            //开启子线程
            Thread t=new Thread(this);
            t.start();
        }else{
            Log.i(TAG,"onCreate:不需要更新");
        }

        //开启子线程
       // Thread t =new Thread(this);
        //t.start(); //开始调用run方法

        //处理消息
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==5){
                    //String str=(String)msg.obj; //强制转换，但必须是同类型
                    //Log.i(TAG,"handleMessage: getMessage msg="+str);
                    Bundle bundle=(Bundle) msg.obj;
                    dollarRate=bundle.getFloat("dollar_rate");
                    euroRate=bundle.getFloat("euro_rate");
                    wonRate=bundle.getFloat("won_rate");

                    //保存更新的日期和汇率信息，如果不保存的话，第二次会为0
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();

                    Log.i(TAG,"handMessage:dollarRate:"+dollarRate);
                    Log.i(TAG,"handMessage:euroRate:"+euroRate);
                    Log.i(TAG,"handMessage:wonRate:"+wonRate);

                    Toast.makeText(Rate.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
            }
        }; //使用分号的原因：这里是对Handler方法的复写，分号表示语句的结束

    }
    public void  onClick(View btn){
        String strR=rmb.getText().toString();
        float r=0;
        float result;
        String ipt;
        //用户有输入内容
        if(strR.length()>0){
            r=Float.parseFloat(strR);
        }else{
            //提醒用户输入信息
            Toast.makeText(this,"请输入金额",Toast.LENGTH_LONG).show();//创建一个消息对话框，.show（）表示显示消息，LENGTH_LONG表示长时间显示
        }
        if(btn.getId()==R.id.bDollar){
            result=r*dollarRate;
            ipt= String.format("%.2f",result);
            showOut.setText(ipt);//转化为字符型之后方可直接输出
            //①showOut.setText(String.valueOf(result));setText只能接收字符串类型，因此需进行字符转换
        }else if(btn.getId()==R.id.bEuro){
            //result=r*1/11; 整数相除，会自动取整
            result=r*euroRate;
            ipt= String.format("%.2f",result);
            showOut.setText(ipt);
            //showOut.setText(String.valueOf(result));
        }else{
            result=r*wonRate;
            ipt= String.format("%.2f",result);
            showOut.setText(ipt); //或通过""转换为字符串
        }
    }
    public void openOne(View btn) {
        Log.i("open", "openOne:");
        Intent config = new Intent(this, ConfigActivity.class);
        //Intent 方法：创建intent对象，表示从一个页面转向另外一个页面。
        // this表示当前页面，另一个类表示另一个页面。可直接返回。
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);

        Log.i(TAG, "openOne：dollarRate=" + dollarRate);
        Log.i(TAG, "openOne：euroRate=" + euroRate);
        Log.i(TAG, "openOne：wonRate=" + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);//code：请求参数
        //打开窗口是为了获得数据
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true; //返回真：有菜单项
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_set){
            Intent config = new Intent(this, ConfigActivity.class);
            config.putExtra("dollar_rate_key", dollarRate);
            config.putExtra("euro_rate_key", euroRate);
            config.putExtra("won_rate_key", wonRate);
            startActivityForResult(config, 1);
        }else if(item.getItemId()==R.id.open_list){
            Intent list = new Intent(this, MyList2Activity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
        protected void onActivityResult(int requestCode,int resultCode, Intent data){
        //三个参数：requestCode:区分是谁返回的数据；resultCode：区别返回的数据类型；intent Data
            if(requestCode==1 && resultCode==2){
                Bundle bundle=data.getExtras();
                dollarRate=bundle.getFloat("key_dollar",0.1f);
                euroRate=bundle.getFloat("key_euro",0.2f);
                wonRate=bundle.getFloat("key_won",0.3f);

                Log.i(TAG,"onActivityResult:dollarRate="+dollarRate);
                Log.i(TAG,"onActivityResult:euroRate="+euroRate);
                Log.i(TAG,"onActivityResult:wonRate="+wonRate);

                //新设置的汇率写到sp里
                //注：①默认情况下值为0.0f,因为开始的时候还没有myrate文件②写和读的文件名要一致
                SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                //SharedPreferences sp =PreferenceManager.getDefaultSharedPreferences(this);
                // 只能获得一个配置文件：提倡只有一个配置文件来存储关键信息。
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putFloat("dollar_rate",dollarRate);
                editor.putFloat("euro_rate",euroRate);
                editor.putFloat("won_rate",wonRate);
                editor.commit();

                Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");

            }
            super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run:run()开始执行");
        /*for(int i=1;i<6;i++){
            Log.i(TAG,"run: i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

      //用于保存获取的汇率
        Bundle bundle;
         /* //获取Msg对象，用于返回主线程
        Message msg=handler.obtainMessage(5);
        //msg.what=5;
        msg.obj="Hello from run()";
        handler.sendMessage(msg); */

        //获取网络数据
       /* URL url= null;
        try {
            url = new URL("http://www.usd-cny.com/bankofchina.htm"); //异常：网址不存在
            HttpURLConnection http =(HttpURLConnection)url.openConnection(); //异常：网络不通 转换类型：将URL转换成HttpURLConnection
            InputStream in=http.getInputStream(); //返回源代码 发生异常：权限不够Permission denied(missing INTERNET permission)解决方法：加上权限
            //在AndroidManifest.xml配置文件中<application前加上<uses-permission>

            String html =inputStream2String(in); //调用inputStream2String方法把输入流转换为字符串
            Log.i(TAG,"run: html: "+html);
            Document doc=Jsoup.parse(html); //把html翻译成源文件
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        bundle =getFromBOC();

        //bundle中保存所获取的汇率
        //获取Msg对象，用于返回主线程
        Message msg=handler.obtainMessage(5);
        //msg.what=5;
       // msg.obj="Hello from run()";
        msg.obj=bundle;
        handler.sendMessage(msg);
    }

    private Bundle getFromBOC() {
        Bundle bundle =new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run:"+doc.title()); //获得title
            Elements tables=doc.getElementsByTag("table");
            int i=1;
            for (Element table : tables) {
                //Log.i(TAG,"run: table["+i+"]"+table);
                i++;
            } //查找出所需数据在哪个table里
            Element table1=tables.get(0);//通过日志看出在table1，但是索引是从0开始
            //Log.i(TAG,"run:table1"+table1 );
            //获取td中的数据
            Elements tds =table1.getElementsByTag("td");

            for(int j=0;j<tds.size();j+=6) {
                Element td1=tds.get(j); //td1:汇率名；td2：数值
                Element td2=tds.get(j+5);
                //Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1=td1.text();
                String val=td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar_rate",Float.parseFloat(val)/100f);
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro_rate",Float.parseFloat(val)/100f);
                }else if("韩元".equals(str1)){
                    bundle.putFloat("won_rate",Float.parseFloat(val)*1000f);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    //把输入流转换成字符串输出
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
       // Reader in = new InputStreamReader(inputStream, "UTF-8"); 根据网页的编码设置编码类型以获取中文
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
