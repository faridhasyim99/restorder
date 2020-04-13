package com.bytotech.Restorder.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytotech.Restorder.Adapter.CheckoutAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Utility;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseAddCart;
import com.bytotech.Restorder.WS.Response.ResponseApplyCode;
import com.bytotech.Restorder.WS.Response.ResponseCartList;
import com.bytotech.Restorder.WS.Response.ResponsePayment;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {

    float totalPrice = 0;
    String total;
    private CommonClass cc;
    private PreferenceUtils preferenceUtils;
    private RelativeLayout progressbar;
    private TextView tvNoData;
    private TextView tvSubTotal;
    private EditText edtPromoCode;
    private ImageView ivBack;
    private RecyclerView rvCheckout;
    private List<ResponseCartList.cartList> cartLists;
    private CheckoutAdapter checkoutAdapter;
    private Button btnApply, btnCheckout;
    private LinearLayout llDiscount;
    private boolean isClicked = false;
    String payment_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        cc = new CommonClass(this);
        preferenceUtils = new PreferenceUtils(this);
        Checkout.preload(getApplicationContext());
        findViewById(R.id.flCart).setVisibility(View.GONE);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        if (preferenceUtils.adCount() == 5) {
            preferenceUtils.setadCount(1);
            cc.showFullAd();
        } else {
            int count = preferenceUtils.adCount() + 1;
            preferenceUtils.setadCount(count);
        }

        progressbar = findViewById(R.id.progressbar);
        tvNoData = findViewById(R.id.tvNoData);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        llDiscount = findViewById(R.id.llDiscount);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        btnApply = findViewById(R.id.btnApply);
        btnCheckout = findViewById(R.id.btnCheckout);

        ((TextView) findViewById(R.id.tvLabelSubTotal)).setText(getString(R.string.subtotal) + "(" + getString(R.string.currency) + ")");
        ((TextView) findViewById(R.id.tvLabelDiscount)).setText(getString(R.string.discount) + "(" + getString(R.string.currency) + ")");
        ((TextView) findViewById(R.id.tvLabelTotal)).setText(getString(R.string.total) + "(" + getString(R.string.currency) + ")");

        ivBack = findViewById(R.id.ivBack);

        rvCheckout = findViewById(R.id.rvCheckout);
        rvCheckout.setNestedScrollingEnabled(false);
        rvCheckout.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCheckout.setLayoutManager(layoutManager);
        rvCheckout.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (cc.isOnline()) {
            getCartList(0);
        } else {
            cc.showAlert(getString(R.string.msg_no_internet));
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate.isNotNull(edtPromoCode.getText().toString().trim())) {
                    if (cc.isOnline()) {
                        ApplyCode();
                        Utility.hideKeyboard(CheckoutActivity.this);
                    } else {
                        cc.showToast(getString(R.string.msg_no_internet));
                    }
                } else {
                    cc.showAlert("Please enter promo code first.");
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate.isNotNull(preferenceUtils.getAddress())) {
                    if (Validate.isNotNull(preferenceUtils.getPhoneno())) {
                        paymentMode();
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this);
                        alertDialog.setMessage(getString(R.string.msg_no_contact));
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(CheckoutActivity.this, EditProfileActivity.class);
                                startActivity(intent);
                            }
                        });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this);
                    alertDialog.setMessage(getString(R.string.msg_no_address));
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CheckoutActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }
            }
        });
    }

    public void paymentMode() {
        final Dialog dialog = new Dialog(this);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_payment_mode);

        final RadioButton rbCOD = dialog.findViewById(R.id.rbCOD);
        final RadioButton rbPay = dialog.findViewById(R.id.rbPay);

        rbCOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_mode = "COD";
                total = ((TextView) findViewById(R.id.tvTotal)).getText().toString().trim();
                JsonObject request = new JsonObject();
                JsonArray cart_id = new JsonArray();
                JsonArray menu_id = new JsonArray();
                JsonArray menu_qty = new JsonArray();
                for (int i = 0; i < cartLists.size(); i++) {
                    cart_id.add(cartLists.get(i).cart_id);
                    menu_id.add(cartLists.get(i).menu_id);
                    menu_qty.add(cartLists.get(i).menu_qty);
                }

                request.add("cart_id", cart_id);
                request.add("menu_id", menu_id);
                request.add("menu_qty", menu_qty);
                request.addProperty("payment_id", "");
                request.addProperty("total_price", total);
                request.addProperty("payment_type", "COD");
                request.addProperty("user_id", preferenceUtils.getUserId());
                getPayment(request);
                dialog.dismiss();
            }
        });

        rbPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment_mode = "PAY";
                startPayment();
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void ApplyCode() {
        String code = edtPromoCode.getText().toString().trim();
        final String total = tvSubTotal.getText().toString().trim();
        progressbar.setVisibility(View.VISIBLE);
        eRestroAPI dawaAPI = RestApi.createAPI();
        Call<ResponseApplyCode> call = dawaAPI.ApplyCode(code, total);
        call.enqueue(new Callback<ResponseApplyCode>() {
            @Override
            public void onResponse(Call<ResponseApplyCode> call, Response<ResponseApplyCode> response) {
                if (response.isSuccessful()) {
                    progressbar.setVisibility(View.GONE);
                    if (response.body().code == Constant.Response_OK) {
                        llDiscount.setVisibility(View.VISIBLE);
                        float Taxtotal = Float.parseFloat(total);
                        String tax = ((TextView) findViewById(R.id.tvTax)).getText().toString().trim();
                        float taxPromo = (Taxtotal * Float.parseFloat(response.body().promo_code_apply.promo_percentage)) / 100;
                        float total = (totalPrice + Float.parseFloat(tax)) - taxPromo;
                        ((TextView) findViewById(R.id.tvDiscount)).setText("- " + String.valueOf(taxPromo));
                        ((TextView) findViewById(R.id.tvTotal)).setText(String.valueOf(total));
                    } else {
                        cc.showAlert(response.body().message);
                    }
                } else {
                    cc.showToast(getString(R.string.msg_something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<ResponseApplyCode> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                cc.showToast(getString(R.string.msg_something_went_wrong));
                t.printStackTrace();
            }
        });
    }

    // ==========  WSCartList ==========//
    public void getCartList(final int from) {
        progressbar.setVisibility(View.VISIBLE);
        eRestroAPI dawaAPI = RestApi.createAPI();
        Call<ResponseCartList> call = dawaAPI.getCartList(preferenceUtils.getUserId());
        call.enqueue(new Callback<ResponseCartList>() {
            @Override
            public void onResponse(Call<ResponseCartList> call, Response<ResponseCartList> response) {
                if (response.isSuccessful()) {
                    progressbar.setVisibility(View.GONE);
                    if (response.body().code == Constant.Response_OK) {
                        cartLists = new ArrayList<>();
                        cartLists = response.body().cart_list;
                        checkoutAdapter = new CheckoutAdapter(CheckoutActivity.this, cartLists);
                        rvCheckout.setAdapter(checkoutAdapter);
                        ((TextView) findViewById(R.id.tvLabelTax)).setText(getString(R.string.tax) + "(" + response.body().tax + "%)");
                        if (from == 0) {
                            cc.AnimationLeft(rvCheckout);
                        }
                        if (cartLists.size() != 0) {
                            for (int i = 0; i < cartLists.size(); i++) {
                                totalPrice = totalPrice + Float.parseFloat(cartLists.get(i).menu_price);
                            }
                            tvSubTotal.setText(String.valueOf(totalPrice));
                            float tax = (totalPrice * Float.parseFloat(response.body().tax)) / 100;
                            ((TextView) findViewById(R.id.tvTax)).setText(String.valueOf(tax));
                            float total = totalPrice + tax;
                            ((TextView) findViewById(R.id.tvTotal)).setText(String.valueOf(total));
                        }

                        // ==========  Add Item ==========//
                        checkoutAdapter.setOnPlusClick(new CheckoutAdapter.OnPlusClick() {
                            @Override
                            public void OnPlusClick(int position, int count, float price) {
                                if (isClicked) {
                                    return;
                                }
                                if (count == 1) {
                                    price = Float.parseFloat(cartLists.get(position).menu_price);
                                } else {
                                    price = price + Float.parseFloat(cartLists.get(position).base_price);
                                }
                                if (cc.isOnline()) {
                                    Addcart(count, price, cartLists.get(position).menu_id, cartLists.get(position).menu_name);
                                } else {
                                    cc.showToast(getString(R.string.msg_no_internet));
                                }
                            }
                        });

                        // ==========  Item Minus ==========//
                        checkoutAdapter.setOnMinusClick(new CheckoutAdapter.OnMinusClick() {
                            @Override
                            public void OnMinusClick(int position, int count, float price) {
                                if (isClicked) {
                                    return;
                                }
                                price = price - Float.parseFloat(cartLists.get(position).base_price);
                                if (cc.isOnline()) {
                                    Addcart(count, price, cartLists.get(position).menu_id, cartLists.get(position).menu_name);
                                } else {
                                    cc.showToast(getString(R.string.msg_no_internet));
                                }

                            }
                        });

                        if (cartLists.size() == 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                            rvCheckout.setVisibility(View.GONE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                            rvCheckout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        preferenceUtils.setCartCount("0");
                        tvNoData.setVisibility(View.VISIBLE);
                        rvCheckout.setVisibility(View.GONE);
                        cc.showToast(response.body().message);
                    }
                } else {
                    cc.showToast(getString(R.string.msg_something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<ResponseCartList> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                cc.showToast(getString(R.string.msg_something_went_wrong));
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    //==========  WS ADD CART ==========//
    public void Addcart(int menu_qty, float newPrice, String menu_id, String menu_name) {
        String item = String.valueOf(menu_qty);
        String priceNew = String.valueOf(newPrice);
        progressbar.setVisibility(View.VISIBLE);
        eRestroAPI dawaAPI = RestApi.createAPI();
        Call<ResponseAddCart> call = dawaAPI.AddCart(preferenceUtils.getUserId(), menu_id, menu_name, priceNew, item);
        call.enqueue(new Callback<ResponseAddCart>() {
            @Override
            public void onResponse(Call<ResponseAddCart> call, Response<ResponseAddCart> response) {
                if (response.isSuccessful()) {
                    isClicked = false;
                    progressbar.setVisibility(View.GONE);
                    if (response.body().response.code == Constant.Response_OK) {
                        llDiscount.setVisibility(View.GONE);
                        edtPromoCode.setText("");
                        totalPrice = 0;
                        if (cc.isOnline()) {
                            preferenceUtils.setCartCount(response.body().add_cart.cart_items);
                            assert preferenceUtils.getCartCount() != null;
                            if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
                                findViewById(R.id.tvCartCount).setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
                            }
                            ((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
                            getCartList(1);
                        } else {
                            finish();
                            cc.showAlert(getString(R.string.msg_no_internet));
                        }
                    } else {
                        cc.showToast(response.body().response.message);
                    }
                } else {
                    progressbar.setVisibility(View.GONE);
                    cc.showToast(getString(R.string.msg_something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<ResponseAddCart> call, Throwable t) {
                isClicked = false;
                progressbar.setVisibility(View.GONE);
                cc.showToast(getString(R.string.msg_something_went_wrong));
                t.printStackTrace();
            }
        });
    }

    public void startPayment() {
        total = ((TextView) findViewById(R.id.tvTotal)).getText().toString().trim();

        float price = Float.parseFloat(total) * 100;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Total payable amount");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
            preFill.put("email", preferenceUtils.getUserEmail());
            options.put("prefill", preFill);
            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            JsonObject request = new JsonObject();
            JsonArray cart_id = new JsonArray();
            JsonArray menu_id = new JsonArray();
            JsonArray menu_qty = new JsonArray();
            for (int i = 0; i < cartLists.size(); i++) {
                cart_id.add(cartLists.get(i).cart_id);
                menu_id.add(cartLists.get(i).menu_id);
                menu_qty.add(cartLists.get(i).menu_qty);
            }

            request.add("cart_id", cart_id);
            request.add("menu_id", menu_id);
            request.add("menu_qty", menu_qty);
            request.addProperty("payment_id", razorpayPaymentID);
            request.addProperty("total_price", total);
            request.addProperty("payment_type", "PAY");
            request.addProperty("user_id", preferenceUtils.getUserId());

            if (cc.isOnline()) {
                getPayment(request);
            } else {
                cc.showToast(getString(R.string.msg_no_internet));
            }
        } catch (Exception e) {
            Log.e("", "Exception in onPaymentSuccess", e);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("", "Exception in onPaymentError", e);
        }
    }

    public void getPayment(JsonObject jsonObject) {
        progressbar.setVisibility(View.VISIBLE);
        eRestroAPI dawaAPI = RestApi.createAPI();
        Call<ResponsePayment> call = dawaAPI.getPayment(jsonObject);
        call.enqueue(new Callback<ResponsePayment>() {
            @Override
            public void onResponse(Call<ResponsePayment> call, Response<ResponsePayment> response) {
                if (response.isSuccessful()) {
                    progressbar.setVisibility(View.GONE);
                    if (response.body().code == Constant.Response_OK) {
                        cc.showToast(response.body().message);
                        preferenceUtils.setCartCount("0");
                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        cc.showAlert(response.body().message);
                    }
                } else {
                    cc.showToast(getString(R.string.msg_something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<ResponsePayment> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                cc.showToast(getString(R.string.msg_something_went_wrong));
                t.printStackTrace();
            }
        });
    }
}
