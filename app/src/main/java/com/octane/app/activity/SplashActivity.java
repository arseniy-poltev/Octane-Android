package com.octane.app.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.LinearLayout;

import com.octane.app.R;

public class SplashActivity extends Activity {

    LinearLayout layout;
    private final Handler handler = new Handler();

    private final Runnable startActivityRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        layout = findViewById(R.id.splashLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnimationSet set = new AnimationSet(true);

        Animation fadeIn = FadeIn(1000);
        fadeIn.setBackgroundColor(0);
        fadeIn.setStartOffset(500);
        set.addAnimation(fadeIn);


        layout.startAnimation(set);

        handler.postDelayed(startActivityRunnable, 2000);
    }
    private Animation FadeIn(int t)
    {
        Animation fade;
        fade = new AlphaAnimation(0.0f,1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }
    private Animation FadeOut(int t)
    {
        Animation fade;
        fade = new AlphaAnimation(1.0f,0.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }
    public void onPause()
    {
        super.onPause();
        handler.removeCallbacks(startActivityRunnable);
    }
}
