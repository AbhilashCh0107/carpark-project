package com.service.parking.theparker.Controller.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.service.parking.theparker.Controller.Activity.Register.LoginActivity;
import com.service.parking.theparker.R;
import com.service.parking.theparker.Theparker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    @BindView(R.id.the_parker_icon)
    ImageView mParkerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        Theparker.animate(this);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                gotToLogin();
            } else {
                gotToStart();
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    void gotToLogin() {
        Intent toLogin = new Intent(this, LoginActivity.class);
        Pair pair = new Pair<View, String>(mParkerIcon, "parkerImage");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);
        startActivity(toLogin, options.toBundle());
        finish();
    }

    void gotToStart() {
        SharedPreferences sharedPreferences = getSharedPreferences("myinfo", MODE_PRIVATE);
        Intent toStart;
        if (sharedPreferences.getString(Theparker.Role, "user").equalsIgnoreCase("user")) {
             toStart = new Intent(this, StartActivity.class);
        } else {
             toStart = new Intent(this, StartAdminActivity.class);
        }
        Pair pair = new Pair<View, String>(mParkerIcon, "parkerImage");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);
        startActivity(toStart, options.toBundle());
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Theparker.animate(this);
    }
}
