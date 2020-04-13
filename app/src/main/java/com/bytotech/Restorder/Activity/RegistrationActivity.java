package com.bytotech.Restorder.Activity;

import android.Manifest;
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
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.Constant;
import com.bytotech.Restorder.CommonClass.DateTimeUtils;
import com.bytotech.Restorder.CommonClass.Validate;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseRegister;
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
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
	
	RequestBody requestBody;
	private CommonClass cc;
	private EditText edtName, edtEmail, edtPassword, edtContactNo, edtAddress1, edtAddress2,
		   edtCountry, edtState, edtCity, edtCode;
	private String strName, strEmail, strPassword, strContactNo, strAddress1, strAddress2,
		   strCountry, strState, strCity, strCode;
	private ImageView ivBack, ivRegister;
	private RelativeLayout progressbar;
	private CircleImageView ivProfileImage;
	private String strFileName;
	private Uri mImageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		cc = new CommonClass(this);
		
		progressbar = findViewById(R.id.progressbar);
		edtName = findViewById(R.id.edtName);
		edtEmail = findViewById(R.id.edtEmail);
		edtPassword = findViewById(R.id.edtPassword);
		edtContactNo = findViewById(R.id.edtContactNo);
		edtAddress1 = findViewById(R.id.edtAddress1);
		edtAddress2 = findViewById(R.id.edtAddress2);
		edtCountry = findViewById(R.id.edtCountry);
		edtState = findViewById(R.id.edtState);
		edtCity = findViewById(R.id.edtCity);
		edtCode = findViewById(R.id.edtCode);
		ivBack = findViewById(R.id.ivBack);
		ivRegister = findViewById(R.id.ivRegister);
		ivProfileImage = findViewById(R.id.ivProfileImage);
		
		((FrameLayout) findViewById(R.id.flCart)).setVisibility(View.GONE);
		
		ivBack.setOnClickListener(this);
		ivRegister.setOnClickListener(this);
		ivProfileImage.setOnClickListener(this);
	}
	
	public boolean isValidate() {
		strName = edtName.getText().toString().trim();
		strEmail = edtEmail.getText().toString().trim();
		strPassword = edtPassword.getText().toString().trim();
		strContactNo = edtContactNo.getText().toString().trim();
		strAddress1 = edtAddress1.getText().toString().trim();
		strAddress2 = edtAddress2.getText().toString().trim();
		strCountry = edtCountry.getText().toString().trim();
		strState = edtState.getText().toString().trim();
		strCity = edtCity.getText().toString().trim();
		strCode = edtCode.getText().toString().trim();
		
		if (Validate.isNull(strName)) {
			edtName.setError("Please enter name.");
			edtName.requestFocus();
			return false;
		} else if (Validate.isNull(strEmail)) {
			edtName.setError("Please enter email.");
			edtName.requestFocus();
			return false;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
			edtName.setError("Please enter valid email.");
			edtName.requestFocus();
			return false;
		} else if (Validate.isNull(strPassword)) {
			edtName.setError("Please enter password.");
			edtName.requestFocus();
			return false;
		} else if (Validate.isNull(strContactNo)) {
			edtName.setError("Please enter mobile no.");
			edtName.requestFocus();
			return false;
		} else if (Validate.isNull(strAddress1)) {
			edtAddress1.setError("Please enter address line 1.");
			edtAddress1.requestFocus();
			return false;
		} else if (Validate.isNull(strAddress2)) {
			edtAddress2.setError("Please enter address line 2.");
			edtAddress2.requestFocus();
			return false;
		} else if (Validate.isNull(strCountry)) {
			edtCountry.setError("Please enter Country.");
			edtCountry.requestFocus();
			return false;
		} else if (Validate.isNull(strState)) {
			edtState.setError("Please enter State.");
			edtState.requestFocus();
			return false;
		} else if (Validate.isNull(strCity)) {
			edtCity.setError("Please enter City.");
			edtCity.requestFocus();
			return false;
		} else if (Validate.isNull(strCode)) {
			edtCode.setError("Please enter Code.");
			edtCode.requestFocus();
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
			case R.id.ivRegister:
				if (isValidate()) {
					if (cc.isOnline()) {
						getRegister();
					} else {
						cc.showToast(getString(R.string.msg_no_internet));
					}
				}
				break;
			case R.id.ivProfileImage:
				chooseFile();
				break;
		}
		
	}
	
	public void getRegister() {
		progressbar.setVisibility(View.VISIBLE);
		
		RequestBody requestName = RequestBody.create(MediaType.parse("text/plain"), strName);
		RequestBody requestEmail = RequestBody.create(MediaType.parse("text/plain"), strEmail);
		RequestBody requestPassword = RequestBody.create(MediaType.parse("text/plain"), strPassword);
		RequestBody requestContactNo = RequestBody.create(MediaType.parse("text/plain"), strContactNo);
		RequestBody requestAddress1 = RequestBody.create(MediaType.parse("text/plain"), strAddress1);
		RequestBody requestAddress2 = RequestBody.create(MediaType.parse("text/plain"), strAddress2);
		RequestBody requestCity = RequestBody.create(MediaType.parse("text/plain"), strCity);
		RequestBody requestState = RequestBody.create(MediaType.parse("text/plain"), strState);
		RequestBody requestCountry = RequestBody.create(MediaType.parse("text/plain"), strCountry);
		RequestBody requestCode = RequestBody.create(MediaType.parse("text/plain"), strCode);
		MultipartBody.Part part = null;
		if (requestBody != null) {
			part = MultipartBody.Part.createFormData("user_image", strFileName, requestBody);
		}
		
		eRestroAPI dawaAPI = RestApi.createAPI();
		Call<ResponseRegister> call = dawaAPI.getRegister(requestName, requestEmail, requestPassword, requestContactNo, requestAddress1,
			   requestAddress2, requestCity, requestState, requestCountry, requestCode, part);
		call.enqueue(new Callback<ResponseRegister>() {
			@Override
			public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
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
			public void onFailure(Call<ResponseRegister> call, Throwable t) {
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
							   if (ContextCompat.checkSelfPermission(RegistrationActivity.this,
									 Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
								   requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
										 Constant.RequestPermissions.READ_EXTERNAL_STORAGE);
							   } else {
								   openGallery();
							   }
							   break;
						   
						   case 1:
							   // GET IMAGE FROM CAMERA
							   if (ContextCompat.checkSelfPermission(RegistrationActivity.this,
									 Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
									     ContextCompat.checkSelfPermission(RegistrationActivity.this,
										        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
									     ContextCompat.checkSelfPermission(RegistrationActivity.this,
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
