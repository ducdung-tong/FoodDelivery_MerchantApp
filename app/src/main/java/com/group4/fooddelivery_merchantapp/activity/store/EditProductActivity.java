package com.group4.fooddelivery_merchantapp.activity.store;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.group4.fooddelivery_merchantapp.model.Product;
import com.group4.fooddelivery_merchantapp.model.ProductImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MULTIPLE_IMAGE_REQUEST = 2;
    Uri mImageUri;

    EditText et_name, et_name_en, et_size, et_price;
    ImageView iv_image;
    ProgressBar bar;
    Button bt_finish, bt_choosemore;
    RecyclerView list_Image;
    ProgressDialog progressDialog;

    FirebaseFirestore root;
    StorageReference storage;
    FirebaseStorage mStorage;
    String docRef;
    String collectionPath = "Product/";
    Product product;
    int Position;
    ArrayList<String> price, size;
    ArrayList<ProductImage> tempImages;

    String[] types;
    ArrayAdapter<String> typeAdapter;
    AutoCompleteTextView temp;
    String choosenType;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Init();

        tempImages = new ArrayList<>();

        for (ProductImage image : product.getImage()) {
            tempImages.add(image.clone());
        }

        if (product.getImage().size() > 0)
            Glide.with(EditProductActivity.this).load(tempImages.get(0).getUri()).into(iv_image);
        ImageAdapter imageAdapter = new ImageAdapter(EditProductActivity.this, tempImages);
        LinearLayoutManager imageManager = new LinearLayoutManager(EditProductActivity.this, RecyclerView.HORIZONTAL, false);
        list_Image.setLayoutManager(imageManager);
        list_Image.setAdapter(imageAdapter);
    }

    private void Init() {
        et_name = findViewById(R.id.edit_et_name);
        et_name_en = findViewById(R.id.edit_et_name_en);
        et_size = findViewById(R.id.edit_et_size);
        et_price = findViewById(R.id.edit_et_price);
        iv_image = findViewById(R.id.edit_iv_image);
        bt_finish = findViewById(R.id.edit_bt_finish);
        bt_choosemore = findViewById(R.id.edit_bt_choosemore);
        bar = findViewById(R.id.edit_pb_bar);
        list_Image = findViewById(R.id.imageEditRecycler);
        progressDialog = new ProgressDialog(EditProductActivity.this);

        Position = Integer.parseInt(getIntent().getStringExtra("Product"));
        product = WelcomeActivity.firebase.productList.get(Position);

        et_name.setText(product.getName());
        et_name_en.setText(product.getName_En());
        String price = "";
        for (int i = 0; i < product.getPrice().size(); i++) {
            if (i == product.getPrice().size() - 1) {
                price += product.getPrice().get(i);
                break;
            }
            price += product.getPrice().get(i) + ",";
        }
        et_price.setText(price);

        String size = "";
        for (int i = 0; i < product.getSize().size(); i++) {
            if (i == product.getSize().size() - 1) {
                size += product.getSize().get(i);
                break;
            }
            size += product.getSize().get(i) + ",";
        }
        et_size.setText(size);

        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();

        choosenType = getResources().getString(R.string.food);

        temp = findViewById(R.id.edit_auto_type);
        types = getResources().getStringArray(R.array.type);
        typeAdapter = new ArrayAdapter<>(EditProductActivity.this, R.layout.dropdown_item, types);
        temp.setAdapter(typeAdapter);

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        bt_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRequirement()) {
                    openConfirmDialog();
                }
            }
        });

        bt_choosemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMultipleFile();
            }
        });

        temp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choosenType = parent.getItemAtPosition(position).toString();
            }
        });
    }

    private void openConfirmDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Window window = dialog.getWindow();

        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowsAttribute = window.getAttributes();
        windowsAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowsAttribute);

        dialog.setCancelable(false);


        Button bt_cancel = dialog.findViewById(R.id.dialog_cancel);
        Button bt_yes = dialog.findViewById(R.id.dialog_yes);
        TextView tv_title = dialog.findViewById(R.id.dialog_title);
        TextView tv_details = dialog.findViewById(R.id.dialog_details);

        tv_title.setText(R.string.update_question);
        tv_details.setText(R.string.update_details);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateProdcut();
            }
        });

        dialog.show();
    }

    private void openFile() {
        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
        getImage.setType("*/*");
        startActivityForResult(getImage, PICK_IMAGE_REQUEST);
    }

    private void openMultipleFile() {
        Intent getMultipleImage = new Intent(Intent.ACTION_GET_CONTENT);
        getMultipleImage.setType("*/*");
        getMultipleImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(getMultipleImage, PICK_MULTIPLE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            Glide.with(EditProductActivity.this).load(mImageUri).into(iv_image);
            tempImages.get(0).setUri(mImageUri.toString());
        } else if (requestCode == PICK_MULTIPLE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                if (tempImages.size() == 0) {
                    Toasty.warning(EditProductActivity.this, getString(R.string.pls_choose_main_image)).show();
                } else {
                    for (int i = 0; i < count; i++) {
                        tempImages.add(new ProductImage(
                                "subsidiary", "", data.getClipData().getItemAt(i).getUri().toString()
                        ));
                    }
                    loadMultipleImage();
                }
            }
        }
    }

    private void loadMultipleImage() {
        ImageAdapter imageAdapter = new ImageAdapter(EditProductActivity.this, tempImages);
        LinearLayoutManager imageManager = new LinearLayoutManager(EditProductActivity.this, RecyclerView.HORIZONTAL, false);
        list_Image.setLayoutManager(imageManager);
        list_Image.setAdapter(imageAdapter);
    }

    private void updateProdcut() {
        progressDialog.setMessage(getString(R.string.updating_info));
        progressDialog.show();
        HashMap<String, Object> info = new HashMap<>();
        info.put("Name", et_name.getText().toString());
        info.put("Name_En", et_name_en.getText().toString());
        info.put("Rating", product.getRating());
        info.put("Sales", product.getPrice());
        info.put("Status", "Còn hàng");
        info.put("Merchant", "Merchant/" + product.getMerchant());
        info.put("Create", new Date().toString());
        String[] prices = et_price.getText().toString().split("\\s*,\\s*");
        String[] sizes = et_size.getText().toString().split("\\s*,\\s*");
        price = new ArrayList<>(Arrays.asList(prices));
        size = new ArrayList<>(Arrays.asList(sizes));
        info.put("Size", size);
        info.put("Price", price);
        info.put("Type", choosenType.equals(getString(R.string.food)) ? "Food" : "Drink");
        root.collection(collectionPath)
                .document(product.getID())
                .update(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateImage();
                    }
                });
    }

    private void updateImage() {
        removeOldImage();
        addNewImage();
    }

    private void removeOldImage() {
        for (ProductImage image : product.getImage()) {
            boolean delete = true;
            for (ProductImage temp : tempImages) {
                if (temp.getUri().equals(image.getUri())) {
                    delete = false;
                    break;
                }
            }
            if (delete) {
                root.collection("Product/" + product.getID() + "/Photos/")
                        .document(image.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("IMG", "Remove from firestore: " + image.getId());
                                mStorage.getReferenceFromUrl(image.getUri())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e("IMG", "Remove from storage: " + image.getId());
                                            }
                                        });
                            }
                        });
            }
        }
    }

    private void addNewImage() {
        if (tempImages.get(0).getUri().contains("firebasestorage")) {
            addMultipleImage();
        } else {
            Map<String, String> type = new HashMap<>();
            type.put("Type", "main");
            root.collection("Product/" + product.getID() +
                    "/Photos/")
                    .add(type)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.e("IMG", "Add main");
                            tempImages.get(0).setId(documentReference.getId());
                            tempImages.get(0).setType("main");
                            uploadImage(tempImages.get(0));
                        }
                    });
        }
    }

    private void addMultipleImage() {
        if (tempImages.size() == 1) {
            Log.e("refresh", "3");
            refreshProduct();
        } else {
            for (ProductImage image : tempImages) {
                if (image.getType().equals("main"))
                    continue;
                if (image.getUri().contains("firebasestorage")) {
                    if (image == tempImages.get(tempImages.size() - 1)) {
                        Log.e("refresh", "2");
                        refreshProduct();
                    }
                } else {
                    Map<String, String> type = new HashMap<>();
                    type.put("Type", "subsidiary");

                    root.collection("Product/" + product.getID() + "/Photos")
                            .add(type)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.e("IMG", "subsidiary");
                                    image.setType("subsidiary");
                                    image.setId(documentReference.getId());
                                    uploadImage(image);
                                }
                            });
                }
            }
        }
    }

    private void refreshProduct() {
        WelcomeActivity.firebase.productList.clear();
        WelcomeActivity.firebase.initProductData(new OnDataListener() {
            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.loading));
            }

            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toasty.success(EditProductActivity.this, getString(R.string.update_success)).show();
                goBack();
            }
        });
    }

    private void uploadImage(ProductImage p) {
        StorageReference fileRef = storage.child("ProductImage/" + WelcomeActivity.firebase.merchantId
                + "/" + product.getID() + "/" + p.getId());
        fileRef.putFile(Uri.parse(p.getUri())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("IMG", "Add to storage");
                        Map<String, String> image = new HashMap<>();
                        image.put("Type", p.getType());
                        image.put("Image_Link", uri.toString());
                        image.put("Upload_Time", new Timestamp(new Date()).toString());
                        root.collection(collectionPath + product.getID() + "/Photos/")
                                .document(p.getId())
                                .set(image)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (p.getType().equals("main"))
                                            addMultipleImage();
                                        if (p == tempImages.get(tempImages.size() - 1)
                                                && !p.getType().equals("main")) {
                                            Log.e("refresh", "1");
                                            refreshProduct();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void goBack() {
        Intent storeActivity = new Intent(EditProductActivity.this, StoreActivity.class);
        startActivity(storeActivity);
        finish();
    }

    private boolean checkRequirement() {
        price = new ArrayList<>(Arrays.asList(et_price.getText().toString().split("\\s*,\\s*")));
        size = new ArrayList<>(Arrays.asList(et_size.getText().toString().split("\\s*,\\s*")));
        if (et_name.getText().toString().isEmpty()) {
            et_name.setError(getString(R.string.pls_enter_product_name));
            et_name.requestFocus();
            return false;
        } else if (et_size.getText().toString().isEmpty()) {
            et_size.setError(getString(R.string.pls_enter_product_size));
            et_size.requestFocus();
            return false;
        } else if (et_price.getText().toString().isEmpty()) {
            et_price.setError(getString(R.string.pls_enter_product_price));
            et_size.requestFocus();
            return false;
        } else if (price.size() != size.size()) {
            et_price.setError(getResources().getString(R.string.size_eqal_price));
            et_size.requestFocus();
            return false;
        }
        return true;
    }
}