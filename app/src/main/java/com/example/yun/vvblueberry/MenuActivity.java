package com.example.yun.vvblueberry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Yun on 2017-09-17.
 */

public class MenuActivity extends Activity {

    private ImageButton btnWeatherInfo, btnDeviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnWeatherInfo = (ImageButton)findViewById(R.id.btnWeatherInfo);
        btnDeviceInfo = (ImageButton)findViewById(R.id.btnDeviceInfo);

        btnWeatherInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, FarmActivity.class);
                startActivity(intent);
            }
        });
    }

}
