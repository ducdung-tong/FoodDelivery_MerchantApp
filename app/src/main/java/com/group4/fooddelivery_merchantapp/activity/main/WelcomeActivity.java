package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;
import com.group4.fooddelivery_merchantapp.model.ModifiedFirebase;

public class WelcomeActivity extends AppCompatActivity {

    public static ModifiedFirebase firebase;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Init();
    }

    private void Init() {
        loading = findViewById(R.id.loading);
        firebase = new ModifiedFirebase();
        getData();
    }

    private void getData() {
        firebase.initProductData(new OnDataListener() {
            @Override
            public void onStart() {
                loading.setVisibility(View.VISIBLE);
                loading.setIndeterminate(true);
            }

            @Override
            public void onSuccess() {
                navigateToMain();
            }
        });
    }

    private void navigateToMain() {
        Intent mainActivity = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }


}