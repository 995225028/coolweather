package com.example.jinkai.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.jinkai.coolweather.Base.BaseActivity;
import com.example.jinkai.coolweather.Service.AutoUpdateService;
import com.example.jinkai.coolweather.gson.Weather;
import com.example.jinkai.coolweather.utils.DbUtil;
import com.example.jinkai.coolweather.utils.HttpUtil;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends BaseActivity implements View.OnClickListener {

    protected TextView titleCity;
    protected TextView titleUpdateTime;
    protected TextView degreeText;
    protected TextView weatherInfo;
    protected LinearLayout forecastLayout;
    protected TextView aqiText;
    protected TextView pmText;
    protected TextView comfortText;
    protected TextView carWashText;
    protected TextView sportText;
    protected ScrollView weatherLayout;
    protected ImageView bgImage;
    public SwipeRefreshLayout swipeRefresh;
    protected Button navBtn;
    public DrawerLayout drawerlauout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_weather);
        initView();
        //设置下拉刷新进度条的颜色
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        //获取SharedPreferences实例
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        loadBgImage();
        final String weatherID;
        //如果缓存中有Weather的Json数据就直接解析
        String weatherresponse = defaultSharedPreferences.getString("weather", null);
        if (weatherresponse != null) {
            Weather weather = DbUtil.handleWeatherResponse(weatherresponse);
            weatherID = weather.getHeWeather().get(0).getBasic().getId();
            requestWeather(weatherID);
        } else {
            weatherID = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherID);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherID);
            }
        });

    }

    private void initView() {
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfo = (TextView) findViewById(R.id.weather_info);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pmText = (TextView) findViewById(R.id.pm_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        bgImage = (ImageView) findViewById(R.id.bg_Image);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        navBtn = (Button) findViewById(R.id.nav_btn);
        navBtn.setOnClickListener(WeatherActivity.this);
        drawerlauout = (DrawerLayout) findViewById(R.id.drawerlauout);
    }

    public void showWeatherInfo(Weather weather) {
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        Weather.HeWeatherBean heWeatherBean = weather.getHeWeather().get(0);
        titleCity.setText(heWeatherBean.getBasic().getCity());
        /**
         * loc : "2017-05-19 10:55"将loc中的数据使用空格分割，得到一个String[]取下标为1的值：10:55
         */
        titleUpdateTime.setText(heWeatherBean.getBasic().getUpdate().getLoc().split(" ")[1]);
        degreeText.setText(heWeatherBean.getNow().getTmp() + "℃");
        weatherInfo.setText(heWeatherBean.getNow().getCond().getTxt());
        forecastLayout.removeAllViews();
        List<Weather.HeWeatherBean.DailyForecastBean> daily_forecast = weather.getHeWeather().get(0).getDaily_forecast();
        for (int i = 0; i < daily_forecast.size(); i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView data_text = (TextView) inflate.findViewById(R.id.data_text);
            TextView info_text = (TextView) inflate.findViewById(R.id.info_text);
            TextView max_text = (TextView) inflate.findViewById(R.id.max_text);
            TextView min_text = (TextView) inflate.findViewById(R.id.min_text);
            data_text.setText(daily_forecast.get(i).getDate());
            info_text.setText(daily_forecast.get(i).getCond().getTxt_d());
            max_text.setText(daily_forecast.get(i).getTmp().getMax());
            min_text.setText(daily_forecast.get(i).getTmp().getMin());
            forecastLayout.addView(inflate);
        }
        if (weather.getHeWeather().get(0).getAqi() != null) {
            aqiText.setText(heWeatherBean.getAqi().getCity().getAqi());
            pmText.setText(heWeatherBean.getAqi().getCity().getPm25());
        }
        comfortText.setText("舒适度：" + heWeatherBean.getSuggestion().getComf().getTxt());
        carWashText.setText("洗车指数：" + heWeatherBean.getSuggestion().getCw().getTxt());
        sportText.setText("运动建议：" + heWeatherBean.getSuggestion().getSport().getTxt());
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public void loadBgImage() {
        String returnedImageUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(returnedImageUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                final String imageUrl = defaultSharedPreferences.getString("imageUrl", null);
                if (imageUrl!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(imageUrl).into(bgImage);
                        }
                    });
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imageUrl = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("imageUrl",imageUrl);
                edit.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(imageUrl).into(bgImage);
                    }
                });
            }
        });
    }

    public void requestWeather(String weather_ID) {
        String url = "http://guolin.tech/api/weather?cityid=" + weather_ID + "&key=3aaf649f0e364044b4ed91bb4fa554fa";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                        String weather = defaultSharedPreferences.getString("weather", null);
                        if (weather!=null){
                            Weather weather1 = DbUtil.handleWeatherResponse(weather);
                            showWeatherInfo(weather1);
                        }
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherResponse = response.body().string();
                final Weather weather = DbUtil.handleWeatherResponse(weatherResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.getHeWeather().get(0).getStatus().equals("ok")) {
                            SharedPreferences.Editor edit =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", weatherResponse);
                            edit.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nav_btn) {
            drawerlauout.openDrawer(GravityCompat.START);
        }
    }
}
