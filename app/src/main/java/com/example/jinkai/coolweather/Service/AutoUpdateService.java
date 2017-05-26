package com.example.jinkai.coolweather.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.example.jinkai.coolweather.gson.Weather;
import com.example.jinkai.coolweather.utils.DbUtil;
import com.example.jinkai.coolweather.utils.HttpUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timedTask();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息：请求最新的json字符串
     */
    public void updateWeather(){
        //获取SharedPreferences实例
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        //根据字段“weather”获取存储的weather信息，类型为Json字符串
        String weatherResponse = defaultSharedPreferences.getString("weather", null);
        if (weatherResponse!=null){
            //解析Json数据获得Weather实体类对象
            Weather weather = DbUtil.handleWeatherResponse(weatherResponse);
            //获取Weather_ID用于请求最新的weather信息
            String weather_ID = weather.getHeWeather().get(0).getBasic().getId();
            //获取最新天气信息的url
            String url = "http://guolin.tech/api/weather?cityid=" + weather_ID + "&key=3aaf649f0e364044b4ed91bb4fa554fa";
            //使用HttpUtil类解析url并进行相关逻辑处理
            HttpUtil.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //获取到的最新weather信息，类型为Json字符串
                    String weatherResponse = response.body().string();
                    //获取Weather实体类
                    Weather weather = DbUtil.handleWeatherResponse(weatherResponse);
                    //如果实体类不为空且status状态为ok，则将最新的weather信息存储在SharedPreferences中
                    if (weather!=null && weather.getHeWeather().get(0).getStatus().equals("ok")){
                        SharedPreferences.Editor edit =
                                PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        edit.putString("weather",weatherResponse);
                        edit.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新壁纸：请求最新的壁纸图片
     */
    public void updateBgimage(){
        //使用该url请求会获得一个image图片的url
        String requestUrl="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将请求下来的image图片url存储在SharedPreferences中
                String imageUrl = response.body().string();
                SharedPreferences.Editor edit =
                        PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("imageUrl",imageUrl);
                edit.apply();

            }
        });
    }

    /**
     * 每隔一个小时更新一次天气信息
     */
    public void timedTask(){
        //更新背景图片信息
        updateBgimage();
        //更新天气信息
        updateWeather();
        //获得闹钟管理器实例
        AlarmManager alermManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        //一小时
        int time=1000*60*60;
        //开机后到目前的时间（SystemClock.elapsedRealtime）加上一个小时等于触发时间
        long triggertime = SystemClock.elapsedRealtime()+time;
        Intent intent1 = new Intent(this, AutoUpdateService.class);
        //获取一个PendingIntent对象
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, 0);
        //取消触发事件
        alermManager.cancel(pendingIntent);
        //设置触发事件
        alermManager.set(AlarmManager.ELAPSED_REALTIME,triggertime,pendingIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
