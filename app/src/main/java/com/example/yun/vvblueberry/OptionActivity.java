package com.example.yun.vvblueberry;

import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yun on 2017-10-15.
 */

public class OptionActivity extends Activity {

    private static final String baseURL = "http://210.117.183.125:60101";

    final static int LIST_ANNOUNCEMENT = 0;
    final static int LIST_CHECK_VERSION = 1;
    final static int LIST_HELP = 2;
    final static int LIST_VALVE = 3;

    private ListView listview2;
    private ListViewAdapterOption adapter = null;

    private int myMajor=1, myMinor=0, myMinor2=0;
    private int recentMajor=-1, recentMinor=-1, recentMinor2=-1;

    private String announceTitle="", announceContent="공지사항 정보를 로딩하지 못했습니다.";

    private ImageButton selMenuButton1, selMenuButton2, selMenuButton3, selMenuButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        listview2 = (ListView)findViewById(R.id.listview2);
        adapter = new ListViewAdapterOption();
        listview2.setAdapter(adapter);
        AddItem(R.drawable.announcement, "공지사항");
        AddItem(R.drawable.information, "버전정보");
        AddItem(R.drawable.question, "도움말");
        AddItem(R.drawable.remote_controll, "테스트_아이템1");
        AddItem(R.drawable.cctv, "테스트_아이템2");

        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == LIST_ANNOUNCEMENT){
                    //도움말 다이얼로그 얼럿 생성
                    final LinearLayout linear = (LinearLayout) View.inflate(getApplicationContext(), R.layout.dialog_announcement, null);
                    TextView announceTextView = (TextView)linear.findViewById(R.id.announceTextView);
                    announceTextView.setText(announceContent);

                    AlertDialog dlg = new AlertDialog.Builder(OptionActivity.this)
                            .setView(linear)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();

                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbtn.setTextColor(Color.BLACK);
                }
                else if(position == LIST_CHECK_VERSION){

                    if(recentMajor == -1 || recentMinor == -1 || recentMinor2 == -1){
                        Toast.makeText(OptionActivity.this, "버전정보를 로딩하지 못했습니다. 다시 시도합니다.", Toast.LENGTH_SHORT).show();
                        ConnectToServer("version");
                    }
                    else if(myMajor == recentMajor && myMinor == recentMinor && myMinor2 == recentMinor2){
                        Toast.makeText(OptionActivity.this, "최신버전 입니다", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(OptionActivity.this, "업데이트가 필요합니다", Toast.LENGTH_SHORT).show();
                    }

                    AlertDialog dlg = new AlertDialog.Builder(OptionActivity.this)
                            .setTitle("버전정보")

                            .setMessage("현재 버전 : v"+myMajor+"."+myMinor+"."+myMinor2+"\n최신 버전 : v"+recentMajor+"."+recentMinor+"."+recentMinor2)
                            .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            })
                            .show();

                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbtn.setTextColor(Color.BLACK);
                }
                else if(position == LIST_HELP){
                    //도움말 다이얼로그 얼럿 생성
                    final LinearLayout linear = (LinearLayout) View.inflate(getApplicationContext(), R.layout.dialog_help, null);
                    AlertDialog dlg = new AlertDialog.Builder(OptionActivity.this)
                            .setView(linear)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();

                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbtn.setTextColor(Color.BLACK);
                }
                else{
                    AlertDialog dlg = new AlertDialog.Builder(OptionActivity.this)
                            .setTitle("준비 중")
                            .setMessage("공개 임박")
                            .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            })
                            .show();

                    Button pbtn = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbtn.setTextColor(Color.BLACK);
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
                Intent intent = new Intent(OptionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, FarmActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionActivity.this, LiveActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ConnectToServer("version");
        ConnectToServer("announcement");
    }

    private void AddItem(int icon, String title){
        listview2.setAdapter(adapter);
        adapter.addItem(icon, title);
    }

    private void ConnectToServer(String word){

        AsyncHttpClient client = new AsyncHttpClient();

        if(word.equals("version")){
            RequestParams params = new RequestParams();
            params.put("id","35QZ9QxAuRcyFA");
            client.post(baseURL+"/version", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                    String result = "["+new String(bytes)+"]";

                    try {
                        JSONArray jarray = new JSONArray(result);   // JSONArray 생성

                        for(int x = 0; x < jarray.length(); x++){
                            JSONObject jObject = jarray.getJSONObject(x);  // JSONObject 추출

                            String tmp;
                            tmp = jObject.getString("success");
                            tmp = jObject.getString("version");

                            tmp = "[" + tmp + "]";

                            JSONArray jarray2 = new JSONArray(tmp);   // JSONArray 생성
                            for(int j = 0; j < jarray2.length(); j++) {
                                JSONObject jObject2 = jarray2.getJSONObject(j);  // JSONObject 추출

                                String tmp2, str2 = "";
                                tmp2 = jObject2.getString("major");
                                recentMajor = Integer.parseInt(tmp2);
                                tmp2 = jObject2.getString("minor");
                                recentMinor = Integer.parseInt(tmp2);
                                tmp2 = jObject2.getString("minor2");
                                recentMinor2 = Integer.parseInt(tmp2);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OptionActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(OptionActivity.this, "서버 연결에 이상이 발생 했습니다", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(word.equals("announcement")){
            RequestParams params = new RequestParams();
            params.put("id","35QZ9QxAuRcyFA");
            client.post(baseURL+"/notice", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                    String result = "["+new String(bytes)+"]";

                    try {
                        JSONArray jarray = new JSONArray(result);   // JSONArray 생성
                        for(int x = 0; x < jarray.length(); x++){
                            JSONObject jObject = jarray.getJSONObject(x);  // JSONObject 추출

                            String tmp;
                            tmp = jObject.getString("success");
                            tmp = jObject.getString("notice");

                            JSONArray jarray2 = new JSONArray(tmp);   // JSONArray 생성
                            JSONObject jObject2 = jarray2.getJSONObject(jarray2.length()-1);
                            announceTitle = jObject2.getString("title");
                            announceContent = jObject2.getString("content");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(OptionActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(OptionActivity.this, "서버 연결에 이상이 발생 했습니다", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}


