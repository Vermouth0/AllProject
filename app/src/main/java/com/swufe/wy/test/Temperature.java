package com.swufe.wy.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Temperature extends AppCompatActivity implements View.OnClickListener{
    TextView out;
    EditText in;
    String content;
    float S,H;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        out = (TextView)findViewById(R.id.textOut);
        in = (EditText)findViewById(R.id.textIn);
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.i("main","onClick");
        content = in.getText().toString();
        S = Float.parseFloat(content);
        H = (float) (32 + S * 1.8);
        out.setText("转换后的值为：" + H);
    }
}
