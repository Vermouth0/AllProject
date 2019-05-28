package com.swufe.wy.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FrameActivity extends FragmentActivity {

    private Fragment mFragment[]; //该数组用于管理所有的Fragment
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction; //事务管理
    private RadioButton rbtHome,rbtFunc,rbtSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        //初始化对象
        mFragment=new Fragment[3];
        fragmentManager=getSupportFragmentManager();
        mFragment[0]=fragmentManager.findFragmentById(R.id.fragment_main);
        mFragment[1]=fragmentManager.findFragmentById(R.id.fragment_func);
        mFragment[2]=fragmentManager.findFragmentById(R.id.fragment_setting);
        fragmentTransaction=fragmentManager.beginTransaction().hide(mFragment[0]).hide(mFragment[1]).hide(mFragment[2]);
        fragmentTransaction.show(mFragment[0]).commit();

       //获取三个元素
        rbtHome= findViewById(R.id.radioHome);
        rbtFunc = (RadioButton)findViewById(R.id.radioFunc);
        rbtSetting = (RadioButton)findViewById(R.id.radioSetting);
        rbtHome.setBackgroundResource(R.drawable.shape3);

        //通过按钮切换
        radioGroup=findViewById(R.id.bottomGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i("radioGroup", "checkId=" + checkedId);
                fragmentTransaction = fragmentManager.beginTransaction().hide(mFragment[0]).hide(mFragment[1]).hide(mFragment[2]);

                //修改背景图片的颜色
                rbtHome.setBackgroundResource(R.drawable.shape2);
                rbtFunc.setBackgroundResource(R.drawable.shape2);
                rbtSetting.setBackgroundResource(R.drawable.shape2);

                switch (checkedId) {
                    case R.id.radioHome:
                        fragmentTransaction.show(mFragment[0]).commit();
                        rbtHome.setBackgroundResource(R.drawable.shape3);
                        break;
                    case R.id.radioFunc:
                        fragmentTransaction.show(mFragment[1]).commit();
                        rbtFunc.setBackgroundResource(R.drawable.shape3);
                        break;
                    case R.id.radioSetting:
                        fragmentTransaction.show(mFragment[2]).commit();
                        rbtSetting.setBackgroundResource(R.drawable.shape3);
                        break;
                    default:
                        break;
                }
            }
        });

     }
}
