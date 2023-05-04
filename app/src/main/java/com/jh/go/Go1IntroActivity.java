package com.jh.go;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Go1IntroActivity extends AppCompatActivity {

    // github push test1
    // test2

    Animation ani1, ani2;
    LinearLayout layoutIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go1_intro);

        getSupportActionBar().hide() ;

        layoutIntro = (LinearLayout) findViewById(R.id.layoutIntro);

        ani1 = AnimationUtils.loadAnimation(Go1IntroActivity.this, R.anim.fade_in);
        ani2 = AnimationUtils.loadAnimation(Go1IntroActivity.this, R.anim.fade_out);

        layoutIntro.startAnimation(ani1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(Go1IntroActivity.this, Go2_0MainActivity.class);
                startActivity(intent);
                layoutIntro.startAnimation(ani2);
                finish();
            }
        },2000); // 2초 후 메인화면으로 이동
    }
}