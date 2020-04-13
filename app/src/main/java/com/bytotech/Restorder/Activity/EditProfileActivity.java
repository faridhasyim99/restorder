package com.bytotech.Restorder.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.DateTimeUtils;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseProfile;
import com.bytotech.Restorder.WS.Response.ResponseUpdateProfile;
import com.bytotech.Restorder.WS.RestApi;
import com.bytotech.Restorder.WS.eRestroAPI;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
	public PreferenceUtils preferenceUtils;
	RequestBody requestBody;
	private CommonClass cc;
	private TextView edtName, edtEmail, edtContactNo,edtAddress1, edtAddress2,
		   edtCountry, edtState, edtCity, edtCode;
	private String strName, strEmail, strPassword, strContactNo,strAddress1, strAddress2,
		   strCountry, strState, strCity, strCode;
	private ImageView ivBack, ivRegister;
	private CircleImageView ivProfileImage;
	private RelativeLayout progressbar;
	private String strFileName;
	private Uri mImageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		
		cc = new CommonClass(this);
		preferenceUtils = new PreferenceUtils(this);
		
		progressbar = findViewById(R.id.progressbar);
		edtName = findViewById(R.id.edtName);
		edtEmail = findViewById(R.id.edtEmail);
		edtContactNo = findViewById(R.id.edtContactNo);
		edtAddress1 = findViewById(R.id.edtAddress1);
		edtAddress2 = findViewById(R.id.edtAddress2);
		edtCountry = findViewById(R.id.edtCountry);
		edtState = findViewById(R.id.edtState);
		edtCity = findViewById(R.id.edtCity);
		edtCode = findViewById(R.id.edtCode);
		ivProfileImage = findViewById(R.id.ivProfileImage);
		ivBack = findViewById(R.id.ivBack);
		ivRegister = findViewById(R.id.ivRegister);
		
		edtName.setClickable(false);
		edtEmail.setClickable(false);
		edtContactNo.setClickable(false);
		
		findViewById(R.id.flCart).setVisibility(View.GONE);
		
		if (cc.isOnline()) {
			getProfile();
		} else {
			cc.showToast(getString(R.string.msg_no_internet));
		}
		
		ivBack.setOnClickListener(this);
		ivProfileImage.setOnClickListener(this);
		ivRegister.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ivBack:
				onBackPressed();
				break;
			case R.id.ivProfileImage:
				chooseFile();
				break;
			case R.id.ivRegister:
				UpdateProfile();
				break;
		}
	}
	
	public void getProfile() {
		progressbar.setVisibility(View.VISIBLE);
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseProfile> call = dawaAPI.getProfile(preferenceUtils.getUserId());
		call.enqueue(new Callback<ResponseProfile>() {
			@SuppressLint("CheckResult")
			@Override
			public void onResponse(Call<ResponseProfile> call, Response<ResponseProfile> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().code == Constant.Response_OK) {
						edtName.setText(response.body().get_user_profile.name);
						edtEmail.setText(response.body().get_user_profile.email);
						edtContactNo.setText(response.body().get_user_profile.phone);
						edtAddress1.setText(response.body().get_user_profile.address_line_1);
						edtAddress2.setText(response.body().get_user_profile.address_line_2);
						edtCity.setText(response.body().get_user_profile.city);
						edtState.setText(response.body().get_user_profile.state);
						edtCountry.setText(response.body().get_user_profile.country);
						edtCode.setText(response.body().get_user_profile.zipcode);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().get_user_profile.address_line_1)) {
							sb.append(response.body().get_user_profile.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.address_line_2)) {
							sb.append(response.body().get_user_profile.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.city)) {
							sb.append(response.body().get_user_profile.city + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.state)) {
							sb.append(response.body().get_user_profile.state + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.country)) {
							sb.append(response.body().get_user_profile.country + " , ");
						}
						if (Validate.isNotNull(response.body().get_user_profile.zipcode)) {
							sb.append(response.body().get_user_profile.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().get_user_profile.phone);
						Log.e("Address==>", "" + sb.toString());
						
						RequestOptions requestOptions = new RequestOptions();
						requestOptions.placeholder(R.drawable.user);
						requestOptions.error(R.drawable.user);
						
						Glide.with(EditProfileActivity.this)
							   .setDefaultRequestOptions(requestOptions)
							   .load(response.body().get_user_profile.user_image)
							   .into(ivProfileImage);
						
					} else {
						cc.showToast(response.body().message);
					}
				} else {
					progressbar.setVisibility(View.GONE);
					cc.showToast(getString(R.string.msg_something_went_wrong));
				}
			}
			
			@Override
			public void onFailure(Call<ResponseProfile> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	public void UpdateProfile() {
		progressbar.setVisibility(View.VISIBLE);
		
		RequestBody requestuserId = RequestBody.create(MediaType.parse("text/plain"), preferenceUtils.getUserId());
		RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), edtName.getText().toString().trim());
		RequestBody requestEmail = RequestBody.create(MediaType.parse("text/plain"), edtEmail.getText().toString().trim());
		RequestBody requestPassword = RequestBody.create(MediaType.parse("text/plain"), "123456");
		RequestBody requestPhone = RequestBody.create(MediaType.parse("text/plain"), edtContactNo.getText().toString().trim());
		RequestBody requestAddress1 = RequestBody.create(MediaType.parse("text/plain"), edtAddress1.getText().toString().trim());
		RequestBody requestAddress2 = RequestBody.create(MediaType.parse("text/plain"), edtAddress2.getText().toString().trim());
		RequestBody requestCity = RequestBody.create(MediaType.parse("text/plain"), edtCity.getText().toString().trim());
		RequestBody requestState = RequestBody.create(MediaType.parse("text/plain"), edtState.getText().toString().trim());
		RequestBody requestCountry = RequestBody.create(MediaType.parse("text/plain"), edtCountry.getText().toString().trim());
		RequestBody requestCode = RequestBody.create(MediaType.parse("text/plain"), edtCode.getText().toString().trim());
		MultipartBody.Part part = null;
		if (requestBody != null) {
			part = MultipartBody.Part.createFormData("user_image", strFileName, requestBody);
		}
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseUpdateProfile> call = dawaAPI.UpdateProfile(requestuserId, requestName, requestEmail, requestPassword, requestPhone, requestAddress1,requestAddress2,requestCity,requestState,requestCountry,requestCode, part);
		call.enqueue(new Callback<ResponseUpdateProfile>() {
			@SuppressLint("CheckResult")
			@Override
			public void onResponse(Call<ResponseUpdateProfile> call, Response<ResponseUpdateProfile> response) {
				if (response.isSuccessful()) {
					progressbar.setVisibility(View.GONE);
					if (response.body().response.code == Constant.Response_OK) {
						edtName.setText(response.body().update_user_profile.name);
						edtEmail.setText(response.body().update_user_profile.email);
						edtContactNo.setText(response.body().update_user_profile.phone);
						edtAddress1.setText(response.body().update_user_profile.address_line_1);
						edtAddress2.setText(response.body().update_user_profile.address_line_2);
						edtCity.setText(response.body().update_user_profile.city);
						edtState.setText(response.body().update_user_profile.state);
						edtCountry.setText(response.body().update_user_profile.country);
						edtCode.setText(response.body().update_user_profile.zipcode);
						preferenceUtils.setUserImage(response.body().update_user_profile.user_image);
						
						StringBuilder sb = new StringBuilder();
						if (Validate.isNotNull(response.body().update_user_profile.address_line_1)) {
							sb.append(response.body().update_user_profile.address_line_1 + " , ");
						}
						if (Validate.isNotNull(response.body().update_user_profile.address_line_2)) {
							sb.append(response.body().update_user_profile.address_line_2 + " , ");
						}
						if (Validate.isNotNull(response.body().update_user_profile.city)) {
							sb.append(response.body().update_user_profile.city + " , ");
						}
						if (Validate.isNotNull(response.body().update_user_profile.state)) {
							sb.append(response.body().update_user_profile.state + " , ");
						}
						if (Validate.isNotNull(response.body().update_user_profile.country)) {
							sb.append(response.body().update_user_profile.country + " , ");
						}
						if (Validate.isNotNull(response.body().update_user_profile.zipcode)) {
							sb.append(response.body().update_user_profile.zipcode + " , ");
						}
						preferenceUtils.setAddress(sb.toString());
						preferenceUtils.setPhoneno(response.body().update_user_profile.phone);
						Log.e("Address==>", "" + sb.toString());
						
						RequestOptions requestOptions = new RequestOptions();
						requestOptions.placeholder(R.drawable.user);
						requestOptions.error(R.drawable.user);
						
						Glide.with(EditProfileActivity.this)
							   .setDefaultRequestOptions(requestOptions)
							   .load(response.body().update_user_profile.user_image)
							   .into(ivProfileImage);
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
			public void onFailure(Call<ResponseUpdateProfile> call, Throwable t) {
				progressbar.setVisibility(View.GONE);
				cc.showToast(getString(R.string.msg_something_went_wrong));
				t.printStackTrace();
			}
		});
	}
	
	private void chooseFile() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.profile_pic_upload_title);
		builder.setItems(R.array.profile_pic_upload_option,
			   new DialogInterface.OnClickListener() {
				   
				   @RequiresApi(api = Build.VERSION_CODES.M)
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					   switch (which) {
						   case 0:
							   // GET IMAGE FROM GALLERY
							   if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
									 Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
								   requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
										 Constant.RequestPermissions.READ_EXTERNAL_STORAGE);
							   } else {
								   openGallery();
							   }
							   break;
						   
						   case 1:
							   // GET IMAGE FROM CAMERA
							   if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
									 Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
									     ContextCompat.checkSelfPermission(EditProfileActivity.this,
										        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
									     ContextCompat.checkSelfPermission(EditProfileActivity.this,
										        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
								   requestPermissions(new String[]{
											    Manifest.permission.CAMERA,
											    Manifest.permission.READ_EXTERNAL_STORAGE,
											    Manifest.permission.WRITE_EXTERNAL_STORAGE},
										 Constant.RequestPermissions.CAMERA);
							   } else {
								   openCamera();
							   }
							   break;
						   
						   default:
							   break;
					   }
				   }
			   });
		
		builder.show();
		
	}
	
	private void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, this.getString(R.string.profile_pic_open_gallary_title)), Constant.ActivityForResult.GALLERY);
	}
	
	private void openCamera() {
		String fileName = getString(R.string.app_name) + DateTimeUtils.getCurrentDateTimeMix() + ".jpg";
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured by Camera on" + getString(R.string.app_name));
		mImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			   values);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		startActivityForResult(intent, Constant.ActivityForResult.CAMERA);
		
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == Constant.RequestPermissions.READ_EXTERNAL_STORAGE
			       && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			openGallery();
		} else if (requestCode == Constant.RequestPermissions.CAMERA
			              && grantResults[0] == PackageManager.PERMISSION_GRANTED
			              && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
			openCamera();
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case Constant.ActivityForResult.CAMERA:
					if (cc.isOnline()) {
						Glide.with(this)
							   .load(mImageUri)
							   .into(ivProfileImage);
						File file = new File(getRealPathFromURI(mImageUri));
						strFileName = file.getName();
						requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(mImageUri)), file);
					} else {
						cc.showToast(R.string.msg_no_internet);
					}
					break;
				case Constant.ActivityForResult.GALLERY:
					if (cc.isOnline()) {
						if (data != null) {
							mImageUri = data.getData();
							assert mImageUri != null;
							Glide.with(this)
								   .load(mImageUri)
								   .into(ivProfileImage);
							File file = new File(getRealPathFromURI(mImageUri));
							strFileName = file.getName();
							requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(mImageUri)), file);
						}
					} else {
						cc.showToast(R.string.msg_no_internet);
					}
					break;
			}
		}
		
	}
	
	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
		if (cursor == null) {
			return contentURI.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}
	
}
