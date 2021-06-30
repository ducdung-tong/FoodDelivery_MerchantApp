package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;
import com.group4.fooddelivery_merchantapp.model.ModifiedFirebase;

import es.dmoral.toasty.Toasty;

public class WelcomeActivity extends AppCompatActivity {

    public static ModifiedFirebase firebase;
    FirebaseAuth mAuth;
    FirebaseFirestore root;
    ProgressBar loading;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            checkMerchant(user.getEmail());
        } else {
            navigatetoLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Init();
    }

    private void Init() {
        loading = findViewById(R.id.loading);
        firebase = new ModifiedFirebase();
        root = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    private void getData() {
        firebase.getMerchantInfo(new OnDataListener() {
            @Override
            public void onStart() {
                loading.setVisibility(View.VISIBLE);
                loading.setIndeterminate(true);
            }

            @Override
            public void onSuccess() {
                firebase.initProductData(new OnDataListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess() {
                        navigateToMain();
                    }
                });
            }
        });

    }

    private void checkMerchant(String email) {
        root.collection("Merchant")
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            WelcomeActivity.firebase.merchant.setId(queryDocumentSnapshots.getDocuments().get(0).getId());;
                            getData();
                        } else {
                            Toasty.error(WelcomeActivity.this, getString(R.string.account_invalid)).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(WelcomeActivity.this, getString(R.string.account_invalid)).show();
                        Log.e("LOGIN", e.getMessage());
                    }
                });
    }

    private void navigateToMain() {
        Intent mainActivity = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    private void navigatetoLogin() {
        Intent loginActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(loginActivity);
    }

}