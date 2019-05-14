package com.swufe.wy.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity {
    public final String TAG="ConfigActivity";
    EditText dollarText;
    EditText euroText;
    EditText wonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent=getIntent();
        float dollar2=intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2=intent.getFloatExtra("euro_rate_key",0.0f);
        float won2=intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG,"onCreate：dollarRate="+dollar2);
        Log.i(TAG,"onCreate：euroRate="+euro2);
        Log.i(TAG,"onCreate：wonRate="+won2);

        dollarText=findViewById(R.id.dollar_rate);
        euroText=findViewById(R.id.euro_rate);
        wonText=findViewById(R.id.won_rate);

        //显示数据到控件
        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
    }
    public void save(View btn){

        //获取新的值
        float newDollar=Float.parseFloat(dollarText.getText().toString());
        float newEuro=Float.parseFloat(euroText.getText().toString());
        float newWon=Float.parseFloat(wonText.getText().toString());

        Log.i(TAG,"save:获取到的值");
        Log.i(TAG,"save:newDollar="+newDollar);
        Log.i(TAG,"save:newEuro="+newEuro);
        Log.i(TAG,"save:newWon="+newWon);
        //保存到bundle或者放入Extra中
        Intent intent=getIntent();
        Bundle bd1=new Bundle();
            //数据打包
        bd1.putFloat("key_euro",newEuro);//这里的key是带回去的，可以和之前的相同也可以不同
        bd1.putFloat("key_dollar",newDollar);
        bd1.putFloat("key_won",newWon);
           //数据放到bundle中
        intent.putExtras(bd1);
        setResult(2,intent);//code:响应代码

        //返回到调用页面
        finish();

    }
}
