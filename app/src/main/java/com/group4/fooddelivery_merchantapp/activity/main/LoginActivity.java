package com.group4.fooddelivery_merchantapp.activity.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.model.Product;
import com.group4.fooddelivery_merchantapp.model.ProductImage;
import com.group4.fooddelivery_merchantapp.model.Regex;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore root;
    FirebaseAuth mAuth;

    ProgressBar bar;
    TextView et_login;
    EditText et_email;
    EditText et_pass;

    String MerchantID = "EOPPrOWpbfp2XCcjCQkT";

    interface mCallBacks {
        void getProductCallBack(Product p);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
    }

    private void Init() {
        et_email = findViewById(R.id.lg_et_email);
        et_pass = findViewById(R.id.lg_et_password);
        et_login = findViewById(R.id.lg_et_login);
        bar = findViewById(R.id.lg_pb_bar);
        root = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        et_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (checkRequirements()) {
//                    bar.setVisibility(View.VISIBLE);
//                    bar.setIndeterminate(true);
//                    //signInWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString());
//                    initProductData();
//                }
                bar.setVisibility(View.VISIBLE);
                bar.setIndeterminate(true);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                initProductData();
            }
        });
    }

    private void initProductData() {
        root.collection("Product/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document == null) break;
                            Product product = new Product();
                            product.setID((String) document.getId());
                            product.setName((String) document.get("Name"));
                            product.setName_En(document.get("Name_En").toString());
                            product.setStatus((String) document.get("Status"));
                            product.setPrice((ArrayList<String>) document.get("Price"));
                            product.setSize((ArrayList<String>) document.get("Size"));
                            product.setMerchant(MerchantID);
                            product.setRating((String) document.get("Rating"));
                            product.setCreate(document.get("Create").toString());
                            getImageList(product, new mCallBacks() {
                                @Override
                                public void getProductCallBack(Product p) {
                                    WelcomeActivity.firebase.productList.add(product);
                                }
                            });
                        }
                        finishGetData();
                    }
                });
    }

    private void getImageList(Product product, final mCallBacks callBacks) {
        ArrayList<ProductImage> images = new ArrayList<ProductImage>();
        root.collection("Product/" + product.getID() + "/Photos/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductImage img = new ProductImage();
                            Log.e("Firebasse", "img" + document.getId());
                            if (document == null)
                                break;
                            img.setUri(document.get("Image_Link").toString());
                            img.setId(document.getId());
                            images.add(img);
                        }
                        product.setImage(images);
                        WelcomeActivity.firebase.productList.add(product);
                        callBacks.getProductCallBack(product);
                    }
                });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            initProductData();
                        } else {
                            bar.setVisibility(View.INVISIBLE);;
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_pass_not_correct),
                                    Toast.LENGTH_LONG).show();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });
    }

    private void finishGetData() {
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