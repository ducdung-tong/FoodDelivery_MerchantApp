package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;
import com.group4.fooddelivery_merchantapp.model.Product;
import com.group4.fooddelivery_merchantapp.model.ProductImage;
import com.group4.fooddelivery_merchantapp.model.Regex;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore root;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    TextView et_login;
    EditText et_email;
    EditText et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
    }

    private void Init() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        et_email = findViewById(R.id.lg_et_email);
        et_pass = findViewById(R.id.lg_et_password);
        et_login = findViewById(R.id.lg_et_login);
        root = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        et_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequirements()) {
                    progressDialog.setMessage(getString(R.string.login_in));
                    progressDialog.show();
                    signInWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString());
                }
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
                            initProductData();
                        } else {
                            Toasty.error(LoginActivity.this, getString(R.string.account_invalid)).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(LoginActivity.this, getString(R.string.account_invalid)).show();
                        Log.e("LOGIN", e.getMessage());
                    }
                });
    }

    private void initProductData() {
        WelcomeActivity.firebase.initProductData(new OnDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loading_data));
            }

            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                navigateToMain();
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkMerchant(authResult.getUser().getEmail());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(LoginActivity.this, getResources().getString(R.string.email_pass_not_correct),
                                Toast.LENGTH_LONG).show();
                        Log.e("LOGIN", e.getMessage());
                    }
                });
    }

    private void navigateToMain() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private boolean checkRequirements() {
        Regex regex = new Regex();
        String email = et_email.getText().toString();
        String pass = et_pass.getText().toString();
        if (email.isEmpty()) {
            et_email.setError(getString(R.string.pls_enter_email));
            et_email.requestFocus();
            return false;
        } else if (!regex.validateEmail(email)) {
            et_email.setError(getString(R.string.wrong_email_format));
            et_email.requestFocus();
            return false;
        } else if (pass.isEmpty()) {
            et_pass.setError(getString(R.string.please_enter_pass));
            et_pass.requestFocus();
            return false;
        }
        return true;
    }
}