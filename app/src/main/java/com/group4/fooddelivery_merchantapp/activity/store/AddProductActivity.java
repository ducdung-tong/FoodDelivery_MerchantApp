package com.group4.fooddelivery_merchantapp.activity.store;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.fooddelivery_merchantapp.R;
import com.group4.fooddelivery_merchantapp.activity.main.WelcomeActivity;
import com.group4.fooddelivery_merchantapp.adapter.ImageAdapter;
import com.group4.fooddelivery_merchantapp.model.OnDataListener;
import com.group4.fooddelivery_merchantapp.model.ProductImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MULTIPLE_IMAGE_REQUEST = 2;
    Uri mImageUri;

    EditText et_name, et_name_en, et_size, et_price;
    ImageView iv_image;
    ProgressBar bar;
    Button bt_finish, bt_addmore;

    FirebaseFirestore root;
    StorageReference storage;
    String productRef;
    String collectionPath = "Product/";
    String MerchantID = "EOPPrOWpbfp2XCcjCQkT";

    String[] types;
    ArrayAdapter<String> typeAdapter;
    AutoCompleteTextView temp;
    String choosenType;
    ArrayList<ProductImage> images = new ArrayList<ProductImage>();
    RecyclerView list_Image;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Init();
    }

    private void Init() {
        et_name = findViewById(R.id.add_et_name);
        et_name_en = findViewById(R.id.add_et_name_en);
        et_size = findViewById(R.id.add_et_size);
        et_price = findViewById(R.id.add_et_price);
        iv_image = findViewById(R.id.add_iv_image);
        bt_finish = findViewById(R.id.add_bt_finish);
        bar = findViewById(R.id.add_pb_bar);
        bt_addmore = findViewById(R.id.add_bt_choosemore);
        list_Image = findViewById(R.id.imageRecycler);
        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(AddProductActivity.this);

        choosenType = getResources().getString(R.string.food);

        temp = findViewById(R.id.add_auto_type);
        types = getResources().getStringArray(R.array.type);
        typeAdapter = new ArrayAdapter<>(AddProductActivity.this, R.layout.dropdown_item, types);
        temp.setAdapter(typeAdapter);

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        bt_addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMultipleFile();
            }
        });

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRequirement();
                insertProduct();
            }
        });

        temp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choosenType = parent.getItemAtPosition(position).toString();
            }
        });
    }

    private void openMultipleFile() {
        Intent getMultipleImage = new Intent(Intent.ACTION_GET_CONTENT);
        getMultipleImage.setType("*/*");
        getMultipleImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(getMultipleImage, PICK_MULTIPLE_IMAGE_REQUEST);
    }

    private void openFile() {
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.setType("*/*");
        startActivityForResult(getImage, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            images.add(new ProductImage(
                    "main", "", mImageUri.toString()
            ));
            Glide.with(AddProductActivity.this).load(mImageUri).into(iv_image);
        } else if (requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData().getItemCount() != 0) {
                int count = data.getClipData().getItemCount();
                if (images.size() == 0) {
                    Toasty.warning(AddProductActivity.this, getString(R.string.pls_choose_main_image)).show();
                } else {
                    for (int i = 0; i < count; i++) {
                        images.add(new ProductImage(
                                "subsidiary", "", data.getClipData().getItemAt(i).getUri().toString()
                        ));
                    }
                    loadMultipleImage();
                }
            }
        }
    }

    private void loadMultipleImage() {
        ImageAdapter imageAdapter = new ImageAdapter(AddProductActivity.this, images);
        LinearLayoutManager imageManager = new LinearLayoutManager(AddProductActivity.this, RecyclerView.HORIZONTAL, false);
        list_Image.setLayoutManager(imageManager);
        list_Image.setAdapter(imageAdapter);
    }

    private void insertProduct() {
        progressDialog.setMessage(getString(R.string.adding_info));
        progressDialog.show();
        HashMap<String, Object> info = new HashMap<>();
        info.put("Name", et_name.getText().toString());
        info.put("Name_En", et_name_en.getText().toString());
        info.put("Rating", "0");
        info.put("Sales", "0");
        info.put("Status", "In_Stock");
        info.put("Merchant", "Merchant/" + WelcomeActivity.firebase.getMerchantId());
        info.put("Create", new Date().toString());
        String[] prices = et_price.getText().toString().split("\\s*,\\s*");
        String[] sizes = et_size.getText().toString().split("\\s*,\\s*");
        ArrayList<String> price = new ArrayList<>(Arrays.asList(prices));
        ArrayList<String> size = new ArrayList<>(Arrays.asList(sizes));
        info.put("Size", size);
        info.put("Price", price);
        info.put("Type", choosenType.equals(getString(R.string.food)) ? "Food" : "Drink");

        root.collection(collectionPath)
                .add(info)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        productRef = documentReference.getId();
                        addTemp();
                    }
                });
    }

    private void addTemp() {
        Map<String, String> type = new HashMap<>();
        type.put("Type", "main");
        root.collection(collectionPath + productRef + "/Photos").add(type)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uploadImage(mImageUri, documentReference.getId(), "main");
                    }
                });
    }

    private void addMultipleImage() {
        if (images.size() == 1) {
            refreshProducData();
        } else {
            for (ProductImage image : images) {
                if (image.getType().equals("main"))
                    continue;
                Map<String, String> type = new HashMap<>();
                type.put("Type", "subsidiary");
                root.collection(collectionPath + productRef + "/Photos").add(type)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                image.setId(documentReference.getId());
                                uploadImage(Uri.parse(image.getUri()), documentReference.getId(), "subsidiary");
                            }
                        });
            }
        }
    }

        private void uploadImage (Uri uri, String id, String type){
            StorageReference fileRef = storage.child("ProductImage/" + MerchantID + "/" + productRef + "/" + id + "." + getExtension(uri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, String> image = new HashMap<>();
                            image.put("Type", type);
                            image.put("Image_Link", uri.toString());
                            image.put("Upload_Time", new Timestamp(new Date()).toString());
                            root.collection(collectionPath + productRef + "/Photos/").document(id).set(image)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e("Add", id + "/" + images.get(images.size() - 1).getId());
                                            if (type.equals("main")) {
                                                addMultipleImage();
                                            }
                                            if (id.equals(images.get(images.size() - 1).getId())) {
                                                refreshProducData();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }

        private void refreshProducData () {
            WelcomeActivity.firebase.productList.clear();
            WelcomeActivity.firebase.initProductData(new OnDataListener() {
                @Override
                public void onStart() {
                    progressDialog.setMessage(getString(R.string.loading));
                }

                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Toasty.success(AddProductActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    goBack();
                }
            });
        }

        private void goBack () {
            AddProductActivity.super.onBackPressed();
            finish();
        }

        private String getExtension (Uri mImageUri){
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(contentResolver.getType(mImageUri));
        }

        private void checkRequirement () {
            if (et_name.getText().toString().isEmpty()) {
                et_name.setError(getString(R.string.pls_enter_product_name));
                et_name.requestFocus();
            } else if (et_size.getText().toString().isEmpty()) {
                et_size.setError(getString(R.string.pls_enter_product_size));
            }
        }
    }