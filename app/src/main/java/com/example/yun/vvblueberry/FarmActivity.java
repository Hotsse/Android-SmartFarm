package com.example.yun.vvblueberry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yun on 2017-09-17.
 */

public class FarmActivity extends Activity {

    private static final String baseURL = "http://210.117.183.125:60101";
    private String valveString="";

    final String serverUri = "tcp://210.117.183.125:10518";

    String clientId = "very2BlueBerry";
    final String temp = "arduino1/sensor/temperature";
    final String humi = "arduino1/sensor/humidity";
    final String motor = "arduino1/solenoid/state";
    final String publishTopic = "arduino1/solenoid/control";
    final String publishMessage = "ON";


    private LinearLayout layoutFarm;

    private TextView textCurrentTime, sensorTempText, sensorHumiText, valveStateText;
    private ImageButton valveIcon;

    private ImageView imgTempLoadingWheel, imgHumiLoadingWheel, imgValveLoadingWheel;

    private ImageButton selMenuButton1, selMenuButton2, selMenuButton3, selMenuButton4;

    private Boolean valveState = false, valveComm = false;


    MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //MQTT 초기화
        try{
            mqttAndroidClient.unsubscribe(temp);
            mqttAndroidClient.unsubscribe(humi);
            mqttAndroidClient.unsubscribe(motor);
        }
        catch(MqttException e){
            e.printStackTrace();
        }

        mqttAndroidClient.close();
        mqttAndroidClient = null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm);

        //뷰(View) 매핑
        layoutFarm = (LinearLayout)findViewById(R.id.layoutFarm);

        textCurrentTime = (TextView)findViewById(R.id.textCurrentTime);
        sensorTempText = (TextView)findViewById(R.id.sensorTempText);
        sensorHumiText = (TextView)findViewById(R.id.sensorHumiText);
        valveStateText = (TextView)findViewById(R.id.valveStateText);

        valveIcon = (ImageButton) findViewById(R.id.valveIcon);


        //로딩 애니메이션
        imgTempLoadingWheel = (ImageView)findViewById(R.id.imgTempLoadingWheel);
        imgHumiLoadingWheel = (ImageView)findViewById(R.id.imgHumiLoadingWheel);
        imgValveLoadingWheel = (ImageView)findViewById(R.id.imgValveLoadingWheel);

        /*
        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        imgTempLoadingWheel.startAnimation(rotate);
        imgHumiLoadingWheel.startAnimation(rotate);
        Animation rotate2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        imgValveLoadingWheel.startAnimation(rotate2);
        */

        //배경 그라디언트 설정
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String currentDateandTime = sdf.format(new Date());

        int tmptime = Integer.parseInt(currentDateandTime);
        if(tmptime < 0)tmptime += 24;
        if(tmptime >= 6 && tmptime < 18)layoutFarm.setBackgroundResource(R.drawable.gradient_background_day);
        else layoutFarm.setBackgroundResource(R.drawable.gradient_background_night);

        //현재 시간 설정
        sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");
        currentDateandTime = sdf.format(new Date());
        textCurrentTime.setText(currentDateandTime);

        clientId = clientId + System.currentTimeMillis();

        //스레드로 구현되어 있는 객체로 보임
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    // serverURI에 재접속함
                    subscribeToTopic();
                } else {
                    // serverURI에 접속 성공
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                //접속 두절
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                if(topic.contains("temperature")){
                    sensorTempText.setText("온도 : " + new String(message.getPayload()) + "℃");
                    imgTempLoadingWheel.setVisibility(View.GONE);
                }
                else if(topic.contains("humidity")){
                    sensorHumiText.setText("습도 : " + new String(message.getPayload()) + "%");
                    imgHumiLoadingWheel.setVisibility(View.GONE);
                }
                else if(topic.contains("state")){
                    valveComm=true;
                    imgValveLoadingWheel.setVisibility(View.GONE);
                    String tmp = new String(message.getPayload());
                    if(tmp.equals("ON")){
                        valveState=true;
                        valveStateText.setText("급수");
                        Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                        valveIcon.startAnimation(rotate);
                    }
                    else if(tmp.equals("OFF")){
                        valveState=false;
                        valveStateText.setText("정지");
                        valveIcon.clearAnimation();
                    }
                }

                if(topic.contains("state"))Log.d("TopicArrived", topic.toString() + "   /  " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try{
                    Log.d("deliveryComplete", token.getMessage().toString());
                }
                catch(MqttException e){
                    e.printStackTrace();
                }

            }
        });

        //접속 옵션 설정
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName("Yuna");
        mqttConnectOptions.setPassword("12345".toCharArray());
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        //접속 시도
        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    //Toast.makeText(getApplicationContext(),"접속 성공",Toast.LENGTH_LONG).show();
                    //접속 성공
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //접속 실패
                    Toast.makeText(getApplicationContext(),"접속 실패",Toast.LENGTH_LONG).show();
                }
            });


        } catch (MqttException ex){
            //에러
            ex.printStackTrace();
        }

        //버튼 이벤트리스너 설정
        valveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!valveComm){
                    Toast.makeText(FarmActivity.this, "연결 중입니다. 잠시만 기다려 주십시오.", Toast.LENGTH_LONG).show();
                }
                else{
                    String publishMsg = "ON";
                    if(valveState)publishMsg="OFF";

                    WriteStatetoServer(publishMsg);

                    try {
                        MqttMessage message = new MqttMessage();
                        message.setPayload(publishMsg.getBytes());
                        mqttAndroidClient.publish(publishTopic, message);

                        if(publishMsg.equals("ON"))Toast.makeText(FarmActivity.this, "밸브 '열기'를 시도합니다", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(FarmActivity.this, "밸브 '닫기'를 시도합니다", Toast.LENGTH_SHORT).show();
                        //퍼블리싱 성공
                    } catch (MqttException e) {
                        //퍼블리싱 실패
                        Toast.makeText(FarmActivity.this, "신호 전달에 실패했습니다", Toast.LENGTH_LONG).show();
                        System.err.println("Error Publishing: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });

        selMenuButton1 = (ImageButton)findViewById(R.id.selMenuButton1);
        selMenuButton2 = (ImageButton)findViewById(R.id.selMenuButton2);
        selMenuButton3 = (ImageButton)findViewById(R.id.selMenuButton3);
        selMenuButton4 = (ImageButton)findViewById(R.id.selMenuButton4);

        selMenuButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmActivity.this, LiveActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FarmActivity.this, OptionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ConnectToServer();
    }

    //토픽 구독하기
    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(temp, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //구독 채널 추가 성공
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //구독 채널 추가 실패
                }
            });

            mqttAndroidClient.subscribe(humi, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //구독 채널 추가 성공
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //구독 채널 추가 실패
                }
            });

            mqttAndroidClient.subscribe(motor, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //구독 채널 추가 성공
                    /*
                    try {
                        MqttMessage message = new MqttMessage();
                        message.setPayload("VIEW".getBytes());
                        mqttAndroidClient.publish("arduino1/solenoid/control", message);
                        //Toast.makeText(FarmActivity.this, "퍼블리싱 성공", Toast.LENGTH_LONG).show();
                        //퍼블리싱 성공
                    } catch (MqttException e) {

                        //퍼블리싱 실패
                        //Toast.makeText(FarmActivity.this, "퍼블리싱 실패", Toast.LENGTH_LONG).show();
                        System.err.println("Error Publishing: " + e.getMessage());
                        e.printStackTrace();
                    }
                    */
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //구독 채널 추가 실패
                    Log.d("solenoid","구독실패");
                }
            });

        } catch (MqttException ex){
            //에러
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    //메시지 퍼블리싱하기
    public void publishMessage(){

        if(!mqttAndroidClient.isConnected()){
            //접속 상태가 아님
            Toast.makeText(FarmActivity.this, "접속 상태가 아닙니다.", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                MqttMessage message = new MqttMessage();
                message.setPayload(publishMessage.getBytes());
                mqttAndroidClient.publish(publishTopic, message);
                Toast.makeText(FarmActivity.this, "퍼블리싱 성공", Toast.LENGTH_LONG).show();
                //퍼블리싱 성공
            } catch (MqttException e) {
                //퍼블리싱 실패
                Toast.makeText(FarmActivity.this, "퍼블리싱 실패", Toast.LENGTH_LONG).show();
                System.err.println("Error Publishing: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    public void changeValveState(String state){
        valveComm=true;
        imgValveLoadingWheel.setVisibility(View.GONE);
        if(state.equals("ON")){
            valveState=true;
            valveStateText.setText("급수");
            Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            valveIcon.startAnimation(rotate);
        }
        else if(state.equals("OFF")){
            valveState=false;
            valveStateText.setText("정지");
            valveIcon.clearAnimation();
        }
    }

    private void WriteStatetoServer(String state){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id","35QZ9QxAuRcyFA");
        params.put("state", state);
        client.post(baseURL+"/valve/write", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                String result = "["+new String(bytes)+"]";
                System.out.println(result);

                try {
                    JSONArray jarray = new JSONArray(result);   // JSONArray 생성
                    for(int x = 0; x < jarray.length(); x++){
                        JSONObject jObject = jarray.getJSONObject(x);  // JSONObject 추출

                        String tmp;
                        tmp = jObject.getString("success");
                        tmp = jObject.getString("valve");

                        valveString = tmp;
                        changeValveState(valveString);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FarmActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(FarmActivity.this, "서버 연결에 이상이 발생 했습니다", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ConnectToServer(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id","35QZ9QxAuRcyFA");
        client.post(baseURL+"/valve", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                String result = "["+new String(bytes)+"]";
                System.out.println(result);

                try {
                    JSONArray jarray = new JSONArray(result);   // JSONArray 생성
                    for(int x = 0; x < jarray.length(); x++){
                        JSONObject jObject = jarray.getJSONObject(x);  // JSONObject 추출

                        String tmp;
                        tmp = jObject.getString("success");
                        tmp = jObject.getString("valve");

                        JSONArray jarray2 = new JSONArray(tmp);   // JSONArray 생성
                        JSONObject jObject2 = jarray2.getJSONObject(jarray2.length()-1);
                        valveString = jObject2.getString("state");
                        changeValveState(valveString);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FarmActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(FarmActivity.this, "서버 연결에 이상이 발생 했습니다", Toast.LENGTH_LONG).show();
            }
        });
    }


}
