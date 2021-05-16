package com.example.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.backend.Param;
import com.example.player.mvp.country.CountryActivity;
import com.example.player.mvp.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Param param = new Param(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (param.getInt(Constant.PARAM_COUNTRY) == 0) {
                    intent = new Intent(getApplicationContext(), CountryActivity.class);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_down);
                }
                finish();
            }
        },2000);
    }
}
