package com.example.jinkai.coolweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jinkai.coolweather.gson.Weather;

import java.util.List;

public class WeatherActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_weather);
        initView();

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
    }
    private void showWeatherInfo(Weather weather){
        Weather.HeWeatherBean heWeatherBean = weather.getHeWeather().get(0);
        titleCity.setText(heWeatherBean.getBasic().getCity());
        /**
         * loc : "2017-05-19 10:55"将loc中的数据使用空格分割，得到一个String[]取下标为1的值：10:55
         */
        titleUpdateTime.setText(heWeatherBean.getBasic().getUpdate().getLoc().split(" ")[1]);
        degreeText.setText(heWeatherBean.getNow().getTmp()+"℃");
        weatherInfo.setText(heWeatherBean.getNow().getCond().getTxt());
        forecastLayout.removeAllViews();
        List<Weather.HeWeatherBean.DailyForecastBean> daily_forecast = weather.getHeWeather().get(0).getDaily_forecast();
        for (int i = 0; i <daily_forecast.size() ; i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView data_text=(TextView) inflate.findViewById(R.id.data_text);
            TextView info_text=(TextView) inflate.findViewById(R.id.info_text);
            TextView max_text=(TextView) inflate.findViewById(R.id.max_text);
            TextView min_text=(TextView) inflate.findViewById(R.id.min_text);
            data_text.setText(daily_forecast.get(i).getDate());
            info_text.setText(daily_forecast.get(i).getCond().getTxt_d());
            max_text.setText(daily_forecast.get(i).getTmp().getMax());
            min_text.setText(daily_forecast.get(i).getTmp().getMin());
            forecastLayout.addView(inflate);
        }
        if (weather.getHeWeather().get(0).getAqi()!=null){
            aqiText.setText(heWeatherBean.getAqi().getCity().getAqi());
            pmText.setText(heWeatherBean.getAqi().getCity().getPm25());
        }
        comfortText.setText("舒适度"+heWeatherBean.getSuggestion().getComf().getTxt());
        carWashText.setText("洗车指数"+heWeatherBean.getSuggestion().getCw().getTxt());
        sportText.setText("运动建议"+heWeatherBean.getSuggestion().getSport().getTxt());
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
