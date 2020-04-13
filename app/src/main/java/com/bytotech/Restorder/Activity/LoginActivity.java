package com.bytotech.Restorder.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseFbLogin;
import com.bytotech.Restorder.WS.Response.ResponseGPLogin;
import com.bytotech.Restorder.WS.Response.ResponseLogin;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
	
	private static final int RC_SIGN_IN = 007;
	LoginButton mLoginButton;
	private CommonClass cc;
	private PreferenceUtils preferenceUtils;
	private EditText edtEmail, edtPassword;
	private String strEmail, strPassword;
	private ImageView ivLogin, ivRegister;
	private TextView tvForgotPassword;
	private RelativeLayout progressbar;
	private CallbackManager mCallbackManager;
	private ImageView ivFbLogin, ivGoogle;
	private GoogleApiClient mGoogleApiClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
		
		progressbar = findViewById(R.id.progressbar);
		edtEmail = findViewById(R.id.edtEmail);
		edtPassword = findViewById(R.id.edtPassword);
		ivLogin = findViewById(R.id.ivLogin);
		ivRegister = findViewById(R.id.ivRegister);
		tvForgotPassword = findViewById(R.id.tvForgotPassword);
		ivFbLogin = findViewById(R.id.ivFbLogin);
		ivGoogle = findViewById(R.id.ivGoogle);
		
		ivLogin.setOnClickListener(this);
		ivFbLogin.setOnClickListener(this);
		ivGoogle.setOnClickListener(this);
		ivRegister.setOnClickListener(this);
		tvForgotPassword.setOnClickListener(this);
		
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			                             .requestEmail()
			                             .build();
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			                      .enableAutoManage(this, this)
			                      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
			                      .build();
		
		mCallbackManager = CallbackManager.Factory.create();
		
		mLoginButton = findViewById(R.id.login_button);
		
		mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(final LoginResult loginResult) {
				getData();
			}
			
			@Override
			public void onCancel() {
			}
			
			@Override
			public void onError(FacebookException exception) {
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		switch (id) {
			case R.id.ivLogin:
				if (isValidate()) {
					if (cc.isOnline()) {
						getLogin();
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
				}
				break;
			case R.id.tvForgotPassword:
				Intent intForgotPassword = new Intent(this, ForgotPasswordActivity.class);
				ActivityOptions options =
					   ActivityOptions.makeCustomAnimation(this, R.anim.slide_in, R.anim.slide_out);
				startActivity(intForgotPassword, options.toBundle());
				break;
			case R.id.ivRegister:
				Intent intRegister = new Intent(this, RegistrationActivity.class);
				ActivityOptions options1 =
					   ActivityOptions.makeCustomAnimation(this, R.anim.slide_in, R.anim.slide_out);
				startActivity(intRegister, options1.toBundle());
				break;
			
			case R.id.ivFbLogin:
				mLoginButton.performClick();
				break;
			
			case R.id.ivGoogle:
				signIn();
				break;
		}
	}
	
	private void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	
	private void handleSignInResult(GoogleSignInResult result) {
		if (result.isSuccess()) {
			GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
			String name = googleSignInAccount.getDisplayName();
			String email = googleSignInAccount.getEmail();
			String id = googleSignInAccount.getId();
			String PhotoUrl = "";
			if (googleSignInAccount.getPhotoUrl() != null) {
				PhotoUrl = googleSignInAccount.getPhotoUrl().toString();
			}
			getRegisterGp(name, email, "", id, PhotoUrl);
			
		}else {
			
			Log.e("Status==>",""+result.getStatus());
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		} else {
			mCallbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.d("", "onConnectionFailed:" + connectionResult);
	}
	
	private void getData() {
		GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
			@Override
			public void onCompleted(JSONObject object, GraphResponse response) {
				
				try {
					String email = object.getString("email");
					String name = object.getString("name");
					String first_name = object.optString("first_name");
					String last_name = object.optString("last_name");
					preferenceUtils.setUserEmail(email);
					preferenceUtils.setUserImage("https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large");
					String utl = String.valueOf("https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large");
					preferenceUtils.setIsFbLogin(true);
					if (cc.isOnline()) {
						getRegisterFB(name, email, "", AccessToken.getCurrentAccessToken().getUserId(), utl);
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		
		Bundle parameters = new Bundle();
		parameters.putString("fields", "id,name,first_name,last_name,email");
		graphRequest.setParameters(parameters);
		graphRequest.executeAsync();
	}
	
	public boolean isValidate() {
		strEmail = edtEmail.getText().toString().trim();
		strPassword = edtPassword.getText().toString().trim();
		if (Validate.isNull(strEmail)) {
			edtEmail.setError("Please enter email.");
			edtEmail.requestFocus();
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
			edtEmail.setError("Please enter valid email.");
			edtEmail.requestFocus();
			return false;
		} else if (Validate.isNull(strPassword)) {
			edtPassword.setError("Please enter password.");
			edtPassword.requestFocus();
			return false;
		} else {
			return true;
		}
	}
	
	public void getLogin() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseLogin> call = dawaAPI.getLogin(strEmail, strPassword);
		call.enqueue(new Callback<ResponseLogin>() {
			@Override
			public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						cc.showToast(response.body().response.message);
						preferenceUtils.setUserId(response.body().login_detail.user_id);
						preferenceUtils.setUserName(response.body().login_detail.name);
						preferenceUtils.setUserEmail(response.body().login_detail.email);
						preferenceUtils.setUserImage(response.body().login_detail.user_image);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().login_detail.address_line_1)) {
							sb.append(response.body().login_detail.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().login_detail.address_line_2)) {
							sb.append(response.body().login_detail.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().login_detail.city)) {
							sb.append(response.body().login_detail.city + " , ");
						}
						if (Validate.isNotNull(response.body().login_detail.state)) {
							sb.append(response.body().login_detail.state + " , ");
						}
						if (Validate.isNotNull(response.body().login_detail.country)) {
							sb.append(response.body().login_detail.country + " , ");
						}
						if (Validate.isNotNull(response.body().login_detail.zipcode)) {
							sb.append(response.body().login_detail.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().login_detail.phone);
						Log.e("Address==>", "" + sb.toString());
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						ActivityOptions options1 =
							   ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.slide_in, R.anim.slide_out);
						startActivity(intent, options1.toBundle());
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseLogin> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void getRegisterFB(String name, String email, String phone, String fb_id, String user_image) {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseFbLogin> call = dawaAPI.getFbLogin(name, email, phone, user_image, fb_id);
		call.enqueue(new Callback<ResponseFbLogin>() {
			@Override
			public void onResponse(Call<ResponseFbLogin> call, Response<ResponseFbLogin> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						preferenceUtils.setUserId(response.body().register_detail.user_id);
						preferenceUtils.setUserEmail(response.body().register_detail.email);
						preferenceUtils.setUserName(response.body().register_detail.name);
						preferenceUtils.setUserImage(response.body().register_detail.user_image);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().register_detail.address_line_1)) {
							sb.append(response.body().register_detail.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().register_detail.address_line_2)) {
							sb.append(response.body().register_detail.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().register_detail.city)) {
							sb.append(response.body().register_detail.city + " , ");
						}
						if (Validate.isNotNull(response.body().register_detail.state)) {
							sb.append(response.body().register_detail.state + " , ");
						}
						if (Validate.isNotNull(response.body().register_detail.country)) {
							sb.append(response.body().register_detail.country + " , ");
						}
						if (Validate.isNotNull(response.body().register_detail.zipcode)) {
							sb.append(response.body().register_detail.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().register_detail.phone);
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						ActivityOptions options1 =
							   ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.slide_in, R.anim.slide_out);
						startActivity(intent, options1.toBundle());
						finish();
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseFbLogin> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void getRegisterGp(String name, String email, String phone, String fb_id, String user_image) {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseGPLogin> call = dawaAPI.getGpLogin(name, email, phone, user_image, fb_id);
		call.enqueue(new Callback<ResponseGPLogin>() {
			@Override
			public void onResponse(Call<ResponseGPLogin> call, Response<ResponseGPLogin> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						preferenceUtils.setUserId(response.body().gplus_register.user_id);
						preferenceUtils.setUserEmail(response.body().gplus_register.email);
						preferenceUtils.setUserName(response.body().gplus_register.name);
						preferenceUtils.setUserImage(response.body().gplus_register.user_image);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().gplus_register.address_line_1)) {
							sb.append(response.body().gplus_register.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().gplus_register.address_line_2)) {
							sb.append(response.body().gplus_register.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().gplus_register.city)) {
							sb.append(response.body().gplus_register.city + " , ");
						}
						if (Validate.isNotNull(response.body().gplus_register.state)) {
							sb.append(response.body().gplus_register.state + " , ");
						}
						if (Validate.isNotNull(response.body().gplus_register.country)) {
							sb.append(response.body().gplus_register.country + " , ");
						}
						if (Validate.isNotNull(response.body().gplus_register.zipcode)) {
							sb.append(response.body().gplus_register.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().gplus_register.phone);
						Log.e("Address==>", "" + sb.toString());
						
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						ActivityOptions options1 =
							   ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.slide_in, R.anim.slide_out);
						startActivity(intent, options1.toBundle());
						preferenceUtils.setIsGpLogin(true);
						finish();
					} else {
						cc.showToast(response.body().response.message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseGPLogin> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
}
