package com.bytotech.Restorder.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.BasketAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseAddCart;
import com.bytotech.Restorder.WS.Response.ResponseCartList;
import com.bytotech.Restorder.WS.Response.ResponseDeleteItem;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class BasketActivity extends AppCompatActivity {
	
	float totalPrice = 0;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressbar;
	private RecyclerView rvBasket;
	private TextView tvNoData;
	private ImageView ivBack;
	private BasketAdapter basketAdapter;
	private List<ResponseCartList.cartList> cartLists;
	private LinearLayout llBottom;
	private Button btnShopping, btnCheckout;
	private boolean isClicked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basket);
		
		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
		
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
		llBottom = findViewById(R.id.llBottom);
		tvNoData = findViewById(R.id.tvNoData);
		
		ivBack = findViewById(R.id.ivBack);
		
		btnShopping = findViewById(R.id.btnShopping);
		btnCheckout = findViewById(R.id.btnCheckout);
		
		rvBasket = findViewById(R.id.rvBasket);
		rvBasket.setNestedScrollingEnabled(false);
		rvBasket.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		rvBasket.setLayoutManager(layoutManager);
		
		ivBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		btnShopping.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BasketActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		btnCheckout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BasketActivity.this, CheckoutActivity.class);
				ActivityOptions options = ActivityOptions.makeCustomAnimation(BasketActivity.this, R.anim.slide_in, R.anim.slide_out);
				startActivity(intent, options.toBundle());
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
						basketAdapter = new BasketAdapter(BasketActivity.this, cartLists);
						rvBasket.setAdapter(basketAdapter);
						if (from == 0) {
							cc.AnimationLeft(rvBasket);
						}
						if (cartLists.size() != 0) {
							for (int i = 0; i < cartLists.size(); i++) {
								totalPrice = totalPrice + Float.parseFloat(cartLists.get(i).menu_price);
							}
							((TextView) findViewById(R.id.tvTotalPrice)).setText(getString(R.string.currency)+" " + totalPrice);
						}
						
						// ==========  Add Item ==========//
						basketAdapter.setOnPlusClick(new BasketAdapter.OnPlusClick() {
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
						
						// ==========  Item minus ==========//
						basketAdapter.setOnMinusClick(new BasketAdapter.OnMinusClick() {
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
						
						// ==========  Item Remove ==========//
						basketAdapter.setRemoveClick(new BasketAdapter.OnRemoveClick() {
							@Override
							public void OnRemoveClick(int position) {
								DeleteItem(cartLists.get(position).cart_id);
							}
						});
						
						if (cartLists.size() == 0) {
							tvNoData.setVisibility(View.VISIBLE);
							rvBasket.setVisibility(View.GONE);
							llBottom.setVisibility(View.GONE);
						} else {
							tvNoData.setVisibility(View.GONE);
							rvBasket.setVisibility(View.VISIBLE);
							llBottom.setVisibility(View.VISIBLE);
						}
					} else {
						preferenceUtils.setCartCount("0");
						tvNoData.setVisibility(View.VISIBLE);
						rvBasket.setVisibility(View.GONE);
						llBottom.setVisibility(View.GONE);
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
						totalPrice = 0;
						if (cc.isOnline()) {
							preferenceUtils.setCartCount(response.body().add_cart.cart_items);
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
	
	//==========  WS REMOVE ITEM ==========//
	public void DeleteItem(String cartId) {
		
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseDeleteItem> call = dawaAPI.deleteItem(cartId, preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseDeleteItem>() {
			@Override
			public void onResponse(Call<ResponseDeleteItem> call, Response<ResponseDeleteItem> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						cc.showToast(response.body().response.message);
						totalPrice = 0;
						if (cc.isOnline()) {
							preferenceUtils.setCartCount(response.body().response.count);
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
			public void onFailure(Call<ResponseDeleteItem> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (cc.isOnline()) {
			totalPrice = 0;
			getCartList(0);
		} else {
			cc.showAlert(getString(R.string.msg_no_internet));
		}
	}
}
