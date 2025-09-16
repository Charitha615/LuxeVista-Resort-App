package com.example.luxevistaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.LinearProgressIndicator;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 seconds
    private static final int ANIMATION_DURATION = 1000; // 1 second

    private ImageView logo;
    private TextView appName, tagline;
    private LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.appName);
        tagline = findViewById(R.id.tagline);
        progressIndicator = findViewById(R.id.progressIndicator);

        // Start animations
        startSplashAnimations();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, SPLASH_DELAY);
    }

    private void startSplashAnimations() {
        // Logo animation - scale and fade in
        AnimationSet logoAnimation = new AnimationSet(true);
        ScaleAnimation scaleLogo = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        AlphaAnimation fadeInLogo = new AlphaAnimation(0, 1);
        logoAnimation.addAnimation(scaleLogo);
        logoAnimation.addAnimation(fadeInLogo);
        logoAnimation.setDuration(ANIMATION_DURATION);
        logoAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        logoAnimation.setStartOffset(200);
        logo.startAnimation(logoAnimation);

        // App name animation - fade in and slide up
        AnimationSet appNameAnimation = new AnimationSet(true);
        AlphaAnimation fadeInAppName = new AlphaAnimation(0, 1);
        TranslateAnimation slideUpAppName = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        appNameAnimation.addAnimation(fadeInAppName);
        appNameAnimation.addAnimation(slideUpAppName);
        appNameAnimation.setDuration(ANIMATION_DURATION);
        appNameAnimation.setStartOffset(400);
        appName.startAnimation(appNameAnimation);

        // Tagline animation - fade in
        AlphaAnimation fadeInTagline = new AlphaAnimation(0, 1);
        fadeInTagline.setDuration(ANIMATION_DURATION);
        fadeInTagline.setStartOffset(800);
        tagline.startAnimation(fadeInTagline);

        // Progress indicator animation - fade in
        AlphaAnimation fadeInProgress = new AlphaAnimation(0, 1);
        fadeInProgress.setDuration(500);
        fadeInProgress.setStartOffset(1000);
        progressIndicator.startAnimation(fadeInProgress);
    }

    private void navigateToLogin() {
        // Fade out animation for all elements
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        logo.startAnimation(fadeOut);
        appName.startAnimation(fadeOut);
        tagline.startAnimation(fadeOut);
        progressIndicator.startAnimation(fadeOut);
    }

//    @Override
//    public void onBackPressed() {
//        // Disable back button during splash screen
//    }
}