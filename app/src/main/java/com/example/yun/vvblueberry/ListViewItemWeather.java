package com.example.yun.vvblueberry;

import android.graphics.drawable.Drawable;

/**
 * Created by Yun on 2017-08-24.
 */

public class ListViewItemWeather {
    private int iconDrawable;
    private String temp, time, rain;

    public void setIcon(int icon){
        this.iconDrawable = icon;
    }
    public void setTemp(String temp){
        this.temp = temp;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setRain(String rain){
        this.rain = rain;
    }

    public int getIcon(){
        return this.iconDrawable;
    }
    public String getTemp(){
        return this.temp;
    }
    public String getTime(){
        return this.time;
    }
    public String getRain(){
        return this.rain;
    }
}
