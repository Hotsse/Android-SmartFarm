package com.example.yun.vvblueberry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private static final String baseURL = "http://210.117.183.125:60101";

    private ListView listview1;
    private ListViewAdapterWeather adapter = null;
    private LinearLayout layoutWeather, layoutLoading;

    private ImageView imgCurrentWeaIcon, imgWeatherLoadingWheel;
    private TextView textCurrentTemp, textCurrentTime;

    private ImageButton selMenuButton1, selMenuButton2, selMenuButton3, selMenuButton4;

    //Push Function Variables---
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCurrentTemp = (TextView)findViewById(R.id.textCurrentTemp);
        textCurrentTime = (TextView)findViewById(R.id.textCurrentTime);
        imgCurrentWeaIcon = (ImageView)findViewById(R.id.imgCurrentWeaIcon);

        imgWeatherLoadingWheel = (ImageView)findViewById(R.id.imgWeatherLoadingWheel);
        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        imgWeatherLoadingWheel.startAnimation(rotate);

        layoutLoading = (LinearLayout)findViewById(R.id.layoutLoading);
        layoutWeather = (LinearLayout)findViewById(R.id.layoutWeather);
        listview1 = (ListView)findViewById(R.id.listview1);

        adapter = new ListViewAdapterWeather();
        listview1.setAdapter(adapter);
        AddItem(R.drawable.sun, "온도", "시간구분", "강수");

        // 시스템으로부터 현재시간(ms) 가져오기
        long now = System.currentTimeMillis();
        // Data 객체에 시간을 저장한다.
        Date date = new Date(now);
        // 각자 사용할 포맷을 정하고 문자열로 만든다.
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        String strNow = sdfNow.format(date);
        textCurrentTime.setText(strNow);

        selMenuButton1 = (ImageButton)findViewById(R.id.selMenuButton1);
        selMenuButton2 = (ImageButton)findViewById(R.id.selMenuButton2);
        selMenuButton3 = (ImageButton)findViewById(R.id.selMenuButton3);
        selMenuButton4 = (ImageButton)findViewById(R.id.selMenuButton4);

        selMenuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FarmActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OptionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //start push service
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        ConnectToServer();
    }

    private void AddItem(int icon, String temp, String time, String rain){
        listview1.setAdapter(adapter);
        adapter.addItem(icon, temp, time, rain);
    }

    private void ConnectToServer(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id","35QZ9QxAuRcyFA");
        client.post(baseURL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                layoutLoading.setVisibility(View.GONE);
                imgWeatherLoadingWheel.setVisibility(View.GONE);

                String result = "["+new String(bytes)+"]";

                try {
                    JSONArray jarray = new JSONArray(result);   // JSONArray 생성

                    for(int x = 0; x < jarray.length(); x++){
                        JSONObject jObject = jarray.getJSONObject(x);  // JSONObject 추출

                        String tmp;
                        //tmp = jObject.getString("success");
                        //tmp = jObject.getString("devices");
                        tmp = jObject.getString("weather");
                        tmp="["+tmp+"]";

                        JSONArray jarray2 = new JSONArray(tmp);   // JSONArray 생성
                        for(int j = 0; j < jarray2.length(); j++) {
                            JSONObject jObject2 = jarray2.getJSONObject(j);  // JSONObject 추출

                            String tmp2, str2 = "";
                            //tmp2 = jObject2.getString("title");
                            //tmp2 = jObject2.getString("category");
                            //tmp2 = jObject2.getString("author");
                            //tmp2 = jObject2.getString("lastUpdated");
                            tmp2 = jObject2.getString("info");

                            JSONArray jarray3 = new JSONArray(tmp2);   // JSONArray 생성

                            String tmp3, str3 = "";

                            for(int k = 0; k < jarray3.length(); k++) {
                                JSONObject jObject3 = jarray3.getJSONObject(k);  // JSONObject 추출

                                tmp3 = jObject3.getString("time");
                                String[] split = tmp3.split("T");
                                String[] split2 = split[1].split(":");
                                int time = Integer.parseInt(split2[0]);

                                String timestr = "오후 12시";
                                if(time == 12)timestr = "오후 12시";
                                else if(time == 0)timestr = "오전 12시";
                                else if(time < 12) timestr = "오전 " + time + "시";
                                else timestr = "오후 " + (time-12) + "시";

                                tmp3 = jObject3.getString("temperature");
                                String[] tempsplit = tmp3.split(":");
                                String[] tempsplit2 = tempsplit[1].split(",");

                                tmp3 = jObject3.getString("sky");
                                String[] skysplit = tmp3.split(":");
                                String[] skysplit2 = skysplit[1].split(",");
                                int skycode = Integer.parseInt(skysplit2[0]);

                                tmp3 = jObject3.getString("rain");
                                String[] rainsplit = tmp3.split(":");
                                String[] rainsplit2 = rainsplit[3].split(",");
                                String[] rainsplit3 = rainsplit[1].split(",");
                                int raincode = Integer.parseInt(rainsplit3[0]);

                                int weaIcon = R.drawable.sun;

                                if(raincode != 0){
                                    if(raincode == 1 || raincode == 2)weaIcon =  R.drawable.rain_cloud;
                                    else weaIcon = R.drawable.snow_cloud;
                                }
                                else{
                                    if(skycode == 1){
                                        if(time >= 6 && time <= 18)weaIcon =  R.drawable.sun;
                                        else weaIcon = R.drawable.moon;
                                    }
                                    else if(skycode == 2){
                                        if(time >= 6 && time <= 18)weaIcon =  R.drawable.sun_cloud;
                                        else weaIcon = R.drawable.moon_cloud;
                                    }
                                    else if(skycode == 3 || skycode == 4)weaIcon = R.drawable.cloud;
                                }

                                if(k == 0){
                                    //그라디언트 설정
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                                    String currentDateandTime = sdf.format(new Date());
                                    int tmptime = Integer.parseInt(currentDateandTime);
                                    if(tmptime < 0)tmptime += 24;
                                    if(tmptime >= 6 && tmptime < 18)layoutWeather.setBackgroundResource(R.drawable.gradient_background_day);
                                    else layoutWeather.setBackgroundResource(R.drawable.gradient_background_night);

                                    //현재 날씨,온도 설정
                                    imgCurrentWeaIcon.setImageResource(weaIcon);
                                    textCurrentTemp.setText(tempsplit2[0] + "℃");

                                }

                                Log.d("testHose", timestr + " / " + tempsplit2[0] + " / " + skysplit2[0] + " / " + rainsplit2[0]);
                                if(time == 0){
                                    AddItem(weaIcon, "-", "다음 날", "-");
                                }
                                AddItem(weaIcon, tempsplit2[0] + "℃", timestr, rainsplit2[0] + "%");
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, "연결 실패. 다시 시도합니다.", Toast.LENGTH_LONG).show();
                ConnectToServer();
            }
        });
    }

    //Push checker
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }
}
