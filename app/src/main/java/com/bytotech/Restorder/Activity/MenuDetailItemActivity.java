package com.bytotech.Restorder.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.Adapter.CommentAdapter;
import com.bytotech.Restorder.Adapter.MenuViewPagerAdapter;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseAddCart;
import com.bytotech.Restorder.WS.Response.ResponseComment;
import com.bytotech.Restorder.WS.Response.ResponseGetLike;
import com.bytotech.Restorder.WS.Response.ResponseLike;
import com.bytotech.Restorder.WS.Response.ResponseMenuItemDetail;
import com.bytotech.Restorder.WS.Response.ResponseMenuSlider;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rd.PageIndicatorView;

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
public class MenuDetailItemActivity extends AppCompatActivity implements View.OnClickListener {
	
	MenuViewPagerAdapter menuViewPagerAdapter;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private RelativeLayout progressBar;
	private TextView tvItemCount;
	private TextView tvComment;
	private String strTitle;
	private String strMId;
	private String strMenuPrice;
	private String strItemDetail;
	private String uri;
	private int count;
	private ImageView ivLike;
	private ViewPager viewPager;
	private float price;
	private List<ResponseMenuSlider.menuMultipleImage> sliderList;
	private RecyclerView rvComment;
	private boolean isClicked = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_detail_item);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
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
		strTitle = getIntent().getStringExtra("title");
		strItemDetail = getIntent().getStringExtra("detail");
		strMId = getIntent().getStringExtra("mid");
		strMenuPrice = getIntent().getStringExtra("menu_price");
		uri = getIntent().getStringExtra("uri");
		String strMenuWeight = getIntent().getStringExtra("menu_weight");
		
		progressBar = findViewById(R.id.progressbar);
		
		TextView tvItemTitle = findViewById(R.id.tvItemTitle);
		tvItemCount = findViewById(R.id.tvItemCount);
		TextView tvItemPrice = findViewById(R.id.tvItemPrice);
		TextView tvItemDetail = findViewById(R.id.tvItemDetail);
		tvComment = findViewById(R.id.tvComment);
		tvItemTitle.setText(strTitle);
		tvItemDetail.setText(strItemDetail);
		tvItemPrice.setText(getString(R.string.currency)+" "+strMenuPrice);
		
		LinearLayout llComment = findViewById(R.id.llComment);
		LinearLayout llShare = findViewById(R.id.llShare);
		
		rvComment = findViewById(R.id.rvComment);
		rvComment.setNestedScrollingEnabled(false);
		rvComment.setHasFixedSize(false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		rvComment.setLayoutManager(layoutManager);
		
		((TextView) findViewById(R.id.tvItemWeight)).setText(strMenuWeight);
		assert preferenceUtils.getCartCount() != null;
		if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
			findViewById(R.id.tvCartCount).setVisibility(View.GONE);
		} else {
			findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
		}
		((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
		
		viewPager = findViewById(R.id.viewPager);
		
		String strItemCount = tvItemCount.getText().toString().trim();
		count = Integer.parseInt(strItemCount);
		
		ImageView ivPlus = findViewById(R.id.ivPlus);
		ImageView ivMinus = findViewById(R.id.ivMinus);
		ivLike = findViewById(R.id.ivLike);
		ImageView ivBack = findViewById(R.id.ivBack);
		ImageView ivCart = findViewById(R.id.ivCart);
		
		ivPlus.setOnClickListener(this);
		ivMinus.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		ivCart.setOnClickListener(this);
		
		if (cc.isOnline()) {
			getMenuDetail();
			getLikes();
		} else {
			cc.showAlert(getString(R.string.msg_no_internet));
		}
		
		if (cc.isOnline()) {
			getSlider();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		
		ivLike.setActivated(true);
		ivLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ivLike.isActivated()) {
					addUnLikes();
				} else {
					addLikes();
				}
			}
		});
		
		llComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentDialog();
			}
		});
		
		llShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri bmpUri = Uri.parse(uri);
				Log.e("uri==>", "" + uri);
				String strShare = strTitle + "\n" +
					                     "\n" + strItemDetail + "\n" +
					                     "\n" +"price : " +getString(R.string.currency)+" "+strMenuPrice + "\n" +
					                     "\n" + "For order food online please Download the App " + getString(R.string.app_name) + "\n" +
					                     "play.google.com/store/apps/details?id=" + getPackageName();
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("text/plain");
				share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				share.putExtra(Intent.EXTRA_SUBJECT, "Share with friends");
				share.putExtra(Intent.EXTRA_TEXT, strShare);
				share.putExtra(Intent.EXTRA_STREAM, bmpUri);
				share.setType("image/*");
				startActivity(Intent.createChooser(share, "Share with friends"));
			}
		});
	}
	
	@SuppressLint("SetTextI18n")
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ivBack:
				onBackPressed();
				break;
			case R.id.ivPlus:
				if (isClicked) {
					return;
				}
				count++;
				if (count == 1) {
					price = Float.parseFloat(strMenuPrice);
				} else {
					price = price + Float.parseFloat(strMenuPrice);
				}
				tvItemCount.setText("" + count);
				if (cc.isOnline()) {
					Addcart(count, price);
				} else {
					cc.showToast(getString(R.string.msg_no_internet));
				}
				break;
			
			case R.id.ivMinus:
				if (isClicked) {
					return;
				}
				if (count != 0) {
					count--;
					price = price - Float.parseFloat(strMenuPrice);
					tvItemCount.setText("" + count);
					if (cc.isOnline()) {
						Addcart(count, price);
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
				} else {
					cc.showAlert("Please enter item first");
				}
				break;
			case R.id.ivCart:
				cc.startCartActivity();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}
	
	public void getSlider() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseMenuSlider> call = dawaAPI.getMenuSlider(strMId);
		call.enqueue(new Callback<ResponseMenuSlider>() {
			@Override
			public void onResponse(Call<ResponseMenuSlider> call, Response<ResponseMenuSlider> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						sliderList = new ArrayList<>();
						sliderList = response.body().menu_multiple_image;
						menuViewPagerAdapter = new MenuViewPagerAdapter(MenuDetailItemActivity.this, sliderList);
						viewPager.setAdapter(menuViewPagerAdapter);
						PageIndicatorView indicator = findViewById(R.id.indicator);
						indicator.setViewPager(viewPager);
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseMenuSlider> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void getLikes() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseGetLike> call = dawaAPI.getLikes(strMId, preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseGetLike>() {
			@Override
			public void onResponse(Call<ResponseGetLike> call, Response<ResponseGetLike> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						((TextView) findViewById(R.id.tvLike)).setText(String.valueOf(response.body().getlike_list_count));
						if (response.body().isLiked) {
							ivLike.setActivated(true);
						} else {
							ivLike.setActivated(false);
						}
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseGetLike> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void addLikes() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseLike> call = dawaAPI.addLike(strMId, preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseLike>() {
			@Override
			public void onResponse(Call<ResponseLike> call, Response<ResponseLike> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						getLikes();
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseLike> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void addUnLikes() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseLike> call = dawaAPI.addunlike(strMId, preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseLike>() {
			@Override
			public void onResponse(Call<ResponseLike> call, Response<ResponseLike> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						getLikes();
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseLike> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void addComment(String strComment) {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseComment> call = dawaAPI.addComment(strMId, preferenceUtils.getUserId(), strComment);
		call.enqueue(new Callback<ResponseComment>() {
			@Override
			public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						getMenuDetail();
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseComment> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	//==========  WS ADD CART ==========//
	public void Addcart(int menu_qty, float newPrice) {
		String item = String.valueOf(menu_qty);
		String priceNew = String.valueOf(newPrice);
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseAddCart> call = dawaAPI.AddCart(preferenceUtils.getUserId(), strMId, strTitle, priceNew, item);
		call.enqueue(new Callback<ResponseAddCart>() {
			@Override
			public void onResponse(Call<ResponseAddCart> call, Response<ResponseAddCart> response) {
				if (response.isSuccessful()) {
					isClicked = false;
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						if (cc.isOnline()) {
							preferenceUtils.setCartCount(response.body().add_cart.cart_items);
							assert preferenceUtils.getCartCount() != null;
							if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
								findViewById(R.id.tvCartCount).setVisibility(View.GONE);
							} else {
								findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
							}
							((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
							getMenuDetail();
						} else {
							finish();
							cc.showAlert(getString(R.string.msg_no_internet));
						}
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseAddCart> call, Throwable t) {
				isClicked = false;
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	//==========  WS Menu Detail ==========//
	public void getMenuDetail() {
		progressBar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseMenuItemDetail> call = dawaAPI.getMenuDetailItem(preferenceUtils.getUserId(), strMId);
		call.enqueue(new Callback<ResponseMenuItemDetail>() {
			@SuppressLint("SetTextI18n")
			@Override
			public void onResponse(Call<ResponseMenuItemDetail> call, Response<ResponseMenuItemDetail> response) {
				if (response.isSuccessful()) {
					progressBar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						if (response.body().menu_list_cat != null) {
							tvItemCount.setText(response.body().menu_list_cat.menu_qty);
							price = Float.parseFloat(response.body().menu_list_cat.menu_price);
							count = Integer.parseInt(response.body().menu_list_cat.menu_qty);
							if (response.body().menu_comment.size() != 0) {
								tvComment.setText(String.valueOf(response.body().menu_comment.size()));
							} else {
								tvComment.setText("0");
							}
						} else {
							Addcart(1, Float.parseFloat(strMenuPrice));
						}
						rvComment.setAdapter(new CommentAdapter(MenuDetailItemActivity.this, response.body().menu_comment));
					} else {
						tvItemCount.setText("" + count);
						if (response.body().menu_comment.size() != 0) {
							tvComment.setText(String.valueOf(response.body().menu_comment.size()));
						} else {
							tvComment.setText("0");
						}
						rvComment.setAdapter(new CommentAdapter(MenuDetailItemActivity.this, response.body().menu_comment));
					}
				} else {
					progressBar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseMenuItemDetail> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	@Override
	protected void onResume() {
		preferenceUtils = new PreferenceUtils(this);
		assert preferenceUtils.getCartCount() != null;
		if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
			findViewById(R.id.tvCartCount).setVisibility(View.GONE);
		} else {
			findViewById(R.id.tvCartCount).setVisibility(View.VISIBLE);
		}
		((TextView) findViewById(R.id.tvCartCount)).setText(preferenceUtils.getCartCount());
		if (cc.isOnline()) {
			getMenuDetail();
		} else {
			cc.showAlert(getString(R.string.msg_no_internet));
		}
		super.onResume();
	}
	
	public void commentDialog() {
		final Dialog dialog = new Dialog(this, R.style.CustomDialog);
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.custom_comment_dialog);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(lp);
		
		final EditText etCommentText = dialog.findViewById(R.id.etCommentText);
		Button btnCancel = dialog.findViewById(R.id.btnCancel);
		Button btnComment = dialog.findViewById(R.id.btnComment);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		btnComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strComment = etCommentText.getText().toString().trim();
				if (Validate.isNotNull(strComment)) {
					if (cc.isOnline()) {
						addComment(strComment);
						dialog.dismiss();
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
				}
			}
		});
		dialog.show();
	}
}
