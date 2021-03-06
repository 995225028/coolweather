package com.example.jinkai.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by jinkai on 2017/5/15.
 * County表实体类
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private  String weatherID;
    private int cityID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
