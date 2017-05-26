package com.example.jinkai.coolweather.Base;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.example.jinkai.coolweather.R;

public class BaseActivity extends AppCompatActivity {
    private long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            long l = System.currentTimeMillis();
            if ((l-time)>2000){
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                time=l;
                return false;
            }else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
