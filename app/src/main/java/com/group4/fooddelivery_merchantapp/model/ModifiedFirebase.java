package com.group4.fooddelivery_merchantapp.model;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifiedFirebase {
    FirebaseFirestore root;
    StorageReference storage;

    public Merchant merchant = new Merchant();
    public ArrayList<Product> productList;
    public ArrayList<ArrayList<Order>> orderList = new ArrayList<>();


    public ModifiedFirebase() {
        productList = new ArrayList<Product>();
        root = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void getMerchantInfo(final OnDataListener listener) {
        listener.onStart();
        root.collection("Merchant/")
                .document(merchant.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            merchant.setName(documentSnapshot.get("Name").toString());

                            ArrayList<Uri> merchantImages = new ArrayList<Uri>();
                            root.collection("Merchant/" + merchant.getId() + "/Photos/")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                if (document == null)
                                                    break;
                                                if (((String) document.get("Image_Link")) != null) {
                                                    merchantImages.add(Uri.parse((String) document.get("Image_Link")));
                                                    break;
                                                }
                                            }
                                            merchant.setImage(merchantImages);
                                            listener.onSuccess();
                                        }
                                    });
                        }
                    }
                });
    }

    public void initProductData(final OnDataListener listener) {
        listener.onStart();
        root.collection("Product/")
                .whereEqualTo("Merchant", "Merchant/" + merchant.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.e("Firebasse", document.getId());
                            if (document == null) break;
                            Product product = new Product();
                            product.setID((String) document.getId());
                            product.setName((String) document.get("Name"));
                            product.setName_En(document.get("Name_En").toString());
                            product.setStatus((String) document.get("Status"));
                            product.setPrice((ArrayList<String>) document.get("Price"));
                            product.setSize((ArrayList<String>) document.get("Size"));
                            product.setMerchant(merchant.getId());
                            product.setRating((String) document.get("Rating"));
                            product.setCreate(document.get("Create").toString());
                            getImageList(product);
                        }
                        listener.onSuccess();
                    }
                });
    }

    public void getImageList(Product product) {
        ArrayList<ProductImage> images = new ArrayList<ProductImage>();
        root.collection("Product/" + product.getID() + "/Photos/")
                .orderBy("Type", Query.Direction.ASCENDING)
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
                            img.setType(document.get("Type").toString());
                            images.add(img);
                        }
                        product.setImage(images);
                        productList.add(product);
                    }
                });
    }

    public void getOrderCollection(final OnDataListener listener) {
        listener.onStart();
        orderList = new ArrayList<>();
        root.collection("Merchant/" + merchant.getId() + "/Order/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Order> pendingList = new ArrayList<>();
                        ArrayList<Order> confirmedList = new ArrayList<>();
                        ArrayList<Order> deliveringList = new ArrayList<>();
                        ArrayList<Order> succeededList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = new Order();
                            order.setUserID(document.get("user_id").toString());
                            order.setUserName(document.get("user_name").toString());
                            order.setUserPhoneNumber(document.get("user_phone_number").toString());
                            order.setUserAddress(document.get("user_address").toString());
                            order.setStatus(document.get("status").toString());
                            order.setTime(document.get("time").toString());
                            order.setOrderID(document.getId());
                            getListOrderedItems(order);
                            order.setMethod(document.get("payment_method").toString());
                            order.setDiscount(Integer.parseInt(document.get("discount").toString()));
                            order.setFreightCost(Integer.parseInt(document.get("freight_cost").toString()));
                            order.setTotalAmount(Integer.parseInt(document.get("total_amount").toString()));
                            if (order.getStatus().equals("Pending"))
                                pendingList.add(order);
                            if (order.getStatus().equals("Confirmed"))
                                confirmedList.add(order);
                            if (order.getStatus().equals("Delivering"))
                                deliveringList.add(order);
                            if (order.getStatus().equals("Succeeded") || order.getStatus().equals("Canceled"))
                                succeededList.add(order);
                        }
                        orderList.add(pendingList);
                        orderList.add(confirmedList);
                        orderList.add(deliveringList);
                        orderList.add(succeededList);
                        listener.onSuccess();
                    }
                });
    }

    public void onNewOrderChangeListener(final OnDataListener listener) {
        listener.onStart();
        root.collection("Merchant/" + merchant.getId() + "/Order/")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("REALTIME", error.getMessage());
                        }

                        if (value != null) {
                            List<DocumentChange> documentChangeList = value.getDocumentChanges();
                            for (DocumentChange document : documentChangeList) {
                                Log.e("REALTIME--", document.getDocument().getId());
                                Order order = new Order();
                                order.setUserID(document.getDocument().get("user_id").toString());
                                order.setUserName(document.getDocument().get("user_name").toString());
                                order.setUserPhoneNumber(document.getDocument().get("user_phone_number").toString());
                                order.setUserAddress(document.getDocument().get("user_address").toString());
                                order.setStatus(document.getDocument().get("status").toString());
                                order.setTime(document.getDocument().get("time").toString());
                                order.setOrderID(document.getDocument().getId());
                                getListOrderedItems(order);
                                order.setMethod(document.getDocument().get("payment_method").toString());
                                order.setDiscount(Integer.parseInt(document.getDocument().get("discount").toString()));
                                order.setFreightCost(Integer.parseInt(document.getDocument().get("freight_cost").toString()));
                                order.setTotalAmount(Integer.parseInt(document.getDocument().get("total_amount").toString()));

                                for (ArrayList<Order> arrayList : orderList) {
                                    for (Order o : arrayList) {
                                        if (o.getOrderID().equals(order.getOrderID())) {
                                            arrayList.remove(o);
                                            break;
                                        }
                                    }
                                }

                                if (order.getStatus().equals("Pending"))
                                    orderList.get(0).add(order);
                                if (order.getStatus().equals("Confirmed"))
                                    orderList.get(1).add(order);
                                if (order.getStatus().equals("Delivering"))
                                    orderList.get(2).add(order);
                                if (order.getStatus().equals("Succeeded") || order.getStatus().equals("Canceled"))
                                    orderList.get(3).add(order);
                            }

                            listener.onSuccess();
                        }
                    }
                });

    }

    public void getListOrderedItems(Order order) {
        ArrayList<OrderItem> orderItemArrayList = new ArrayList<>();

        root.collection("Merchant/" + merchant.getId() + "/Order/").document(order.getOrderID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            ArrayList<Map<String, String>> listMap = new ArrayList<>();
                            listMap = (ArrayList<Map<String, String>>) documentSnapshot.get("listItems");

                            for (Map item : listMap) {
//                                Log.e(ORDER_TAG, "get order products: " + documentSnapshot.getId() + " " + item.get("product"));
                                Product product = new Product();
                                product = getProductById(item.get("product").toString());

                                OrderItem orderItem = new OrderItem(
                                        product,
                                        Integer.parseInt(item.get("quantity").toString()),
                                        Integer.parseInt(item.get("price").toString()),
                                        item.get("size").toString()
                                );
                                orderItemArrayList.add(orderItem);
                            }
                            order.setListOrderItems(orderItemArrayList);
                        }
                    }
                });
    }

    public void setOrderStatus(Order order, String status, final OnDataListener listener) {
        listener.onStart();
        root.document("Merchant/" + merchant.getId() + "/Order/" + order.getOrderID())
                .update("status", status);
        root.collection("User/" + order.getUserID() + "/Order/")
                .document(order.orderID)
                .update("status", status);
        listener.onSuccess();
    }

    public void addOrderUpdateNotification(String userId, String orderId, String status) {
        MyNotification notification = new MyNotification();
        //Set vietnamese content
        notification.setTitle_vn("Cập nhật đơn hàng");
        notification.setTitle_en("Update order");
        if (status.equals("Confirmed")) {
            notification.setDesc_en("Order "+ orderId + " has been confirmed. The merchant will deliver to you soon!");
            notification.setDesc_vn("Đơn hàng " + orderId + " của bạn đã được xác nhận. Người bán sẽ sớm giao ngay cho bạn!");
        }
        if (status.equals("Delivering")) {
            notification.setDesc_en("Order "+ orderId + " is being delivered. See you soon!");
            notification.setDesc_vn("Đơn hàng " + orderId + " của bạn đang được vận chuyển. Chờ một chút nhé!");
        }
        if (status.equals("Succeeded")) {
            notification.setDesc_en("Order "+ orderId + " has been successfully delivered. Remember to rate the products!");
            notification.setDesc_vn("Đơn hàng " + orderId + " đã giao thành công. Đừng quên đánh giá sản phẩm nhé!");
        }
        if (status.equals("Canceled")) {
            notification.setDesc_en("Order "+ orderId + " has been canceled or failed to deliver");
            notification.setDesc_vn("Đơn hàng " + orderId + " đã bị hủy hoặc giao thất bại!");
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String time = format.format(Calendar.getInstance().getTime()).toString();

        notification.setTime(time);
        Map<String, String> noti = new HashMap<>();
        noti.put("Title_Vn", notification.getTitle_vn());
        noti.put("Detail_Vn", notification.getDesc_vn());
        noti.put("Title_En", notification.getTitle_en());
        noti.put("Detail_En", notification.getDesc_en());
        noti.put("Date", notification.getTime());
        noti.put("Status", "false");
        root.collection("User/" + userId + "/Notification/")
                .document()
                .set(noti)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    private Product getProductById(String productId) {
        Product product = new Product();
        root.document("Product/" + productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        product.setID(productId);
                        product.setName(documentSnapshot.get("Name").toString());
                        root.collection("Product/" + productId + "/Photos/")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ProductImage productImage = new ProductImage();
                                        productImage.setUri(queryDocumentSnapshots.getDocuments().get(0).get("Image_Link").toString());
                                        product.getImage().add(productImage);
                                    }
                                });
                    }
                });

        return product;
    }

    public String getMerchantId() {
        return merchant.getId();
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }
}
