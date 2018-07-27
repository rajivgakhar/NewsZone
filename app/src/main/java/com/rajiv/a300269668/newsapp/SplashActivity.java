package com.rajiv.a300269668.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    TextView splashProgreeBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

      splashProgreeBar=(TextView) findViewById(R.id.txtProgress);


        new Thread(new Runnable() {

            public void run() {
                int i = 0;
                while (i < 100) {
                    SystemClock.sleep(10);
                    i++;
                    final int curCount = i;
                    if (curCount % 5 == 0) {
                        //update UI with progress every 5%
                        splashProgreeBar.post(new Runnable() {
                            public void run() {
                                splashProgreeBar.setText(curCount+"%");
                            }
                        });
                    }
                }
                splashProgreeBar.post(new Runnable() {
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                        finish();
                    }
                });
            }
        }).start();
    }
}
