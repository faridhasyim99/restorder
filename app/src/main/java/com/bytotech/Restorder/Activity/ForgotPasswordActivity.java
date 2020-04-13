package com.bytotech.Restorder.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseForgotPassword;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
	
	private CommonClass cc;
	private EditText edtEmail;
	private String strEmail;
	private ImageView ivBack, ivReset;
	private TextView tvTitle;
	private RelativeLayout progressbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		cc = new CommonClass(this);
		
		progressbar = findViewById(R.id.progressbar);
		edtEmail = findViewById(R.id.edtEmail);
		ivBack = findViewById(R.id.ivBack);
		ivReset = findViewById(R.id.ivReset);
		tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.forgot_password));
		((FrameLayout) findViewById(R.id.flCart)).setVisibility(View.GONE);
		ivReset.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		
	}
	
	public boolean isValidate() {
		strEmail = edtEmail.getText().toString().trim();
		if (Validate.isNull(strEmail)) {
			edtEmail.setError("Please enter email.");
			edtEmail.requestFocus();
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
			edtEmail.setError("Please enter valid email.");
			edtEmail.requestFocus();
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ivBack:
				onBackPressed();
				break;
			case R.id.ivReset:
				if (isValidate()) {
					if (cc.isOnline()) {
						getPassword();
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
				}
				break;
		}
		
	}
	
	public void getPassword() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseForgotPassword> call = dawaAPI.getPassword(strEmail);
		call.enqueue(new Callback<ResponseForgotPassword>() {
			@Override
			public void onResponse(Call<ResponseForgotPassword> call, Response<ResponseForgotPassword> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						cc.showToast(response.body().response.message);
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
			public void onFailure(Call<ResponseForgotPassword> call, Throwable t) {
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
}
