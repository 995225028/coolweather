package com.example.jinkai.coolweather.utils;

import android.text.TextUtils;

import com.example.jinkai.coolweather.db.City;
import com.example.jinkai.coolweather.db.County;
import com.example.jinkai.coolweather.db.Province;
import com.example.jinkai.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jinkai on 2017/5/16.
 */

public class DbUtil {
    /**
     * 解析和处理服务器返回的省级数据
     * @param response
     * @return
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i <allProvinces.length() ; i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param response
     * @param provinceID
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceID){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i <allCities.length() ; i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceID(provinceID);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param response
     * @param cityID
     * @return
     */
    public static boolean handleCountyResponse(String response,int cityID){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray counties = new JSONArray(response);
                for (int i = 0; i < counties.length(); i++) {
                    JSONObject countyObject = counties.getJSONObject(i);
                    County county = new County();
                    county.setCityID(cityID);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherID(countyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的Json数据解析成WeatherInfo实体类
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response){
        Gson gson = new Gson();
        Weather weather = gson.fromJson(response, Weather.class);
        return weather;
    }
}
