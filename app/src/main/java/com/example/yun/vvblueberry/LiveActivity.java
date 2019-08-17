package com.example.yun.vvblueberry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

/**
 * Created by Yun on 2017-10-16.
 */

public class LiveActivity extends Activity {

    private WebView webView;

    private ImageButton selMenuButton1, selMenuButton2, selMenuButton3, selMenuButton4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://210.117.183.125:60101/cam");

        selMenuButton1 = (ImageButton)findViewById(R.id.selMenuButton1);
        selMenuButton2 = (ImageButton)findViewById(R.id.selMenuButton2);
        selMenuButton3 = (ImageButton)findViewById(R.id.selMenuButton3);
        selMenuButton4 = (ImageButton)findViewById(R.id.selMenuButton4);

        selMenuButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveActivity.this, FarmActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selMenuButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveActivity.this, OptionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
