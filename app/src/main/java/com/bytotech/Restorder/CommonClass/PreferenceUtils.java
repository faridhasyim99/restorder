package com.bytotech.Restorder.CommonClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class PreferenceUtils {

    private final String USER_ID = "pref_user_id";
    private final String USER_NAME = "pref_user_name";
    private final String USER_EMAIL = "pref_user_email";
    private final String USER_IMAGE = "pref_user_image";
    private final String CART_COUNT = "pref_cart_count";
    private final String IS_FB_LOGIN = "pref_is_fb_login";
    private final String IS_GP_LOGIN = "pref_is_gp_login";
    private final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private final String AD_COUNT = "ad_count";
    private final String ADDRESS = "address";
    private final String PHONENO = "phoneno";

    private SharedPreferences pref;
    @Nullable
    private String userid;
    private String username;
    private String useremail;
    private String userimege;
    private String cartCount;
    private String address;
    private String Phoneno;
    private boolean isFbLogin;
    private boolean isGpLogin;
    private boolean isFirstTime;
    private int adCount;

    public PreferenceUtils(Context context) {
        if (context == null) {
            context = getAppContext();
        }
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        refreshUserId();
        refreshUserName();
        refreshUserEmail();
        refreshUserImage();
        refreshCartCount();
        refreshIsFbLogin();
        refreshIsGpLogin();
        refreshisFirstTime();
        refreshadCount();
        refreshAddress();
        refreshPhoneno();
    }

    public static Context getAppContext() {
        return App.mContext;
    }

    private void refreshUserId() {
        userid = pref.getString(USER_ID, "");
    }

    @Nullable
    public String getUserId() {
        return userid;
    }

    public void setUserId(@NonNull String userid) {
        this.userid = userid;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ID, userid);
        editor.apply();
    }

    private void refreshUserName() {
        username = pref.getString(USER_NAME, "");
    }

    @Nullable
    public String getUserName() {
        return username;
    }

    public void setUserName(@NonNull String username) {
        this.username = username;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    private void refreshUserEmail() {
        useremail = pref.getString(USER_EMAIL, "");
    }

    @Nullable
    public String getUserEmail() {
        return useremail;
    }

    public void setUserEmail(@NonNull String useremail) {
        this.useremail = useremail;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_EMAIL, useremail);
        editor.apply();
    }

    private void refreshUserImage() {
        userimege = pref.getString(USER_IMAGE, "");
    }

    @Nullable
    public String getUserImage() {
        return userimege;
    }

    public void setUserImage(@NonNull String userimege) {
        this.userimege = userimege;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_IMAGE, userimege);
        editor.apply();
    }

    private void refreshCartCount() {
        cartCount = pref.getString(CART_COUNT, "");
    }

    @Nullable
    public String getCartCount() {
        return cartCount;
    }

    public void setCartCount(@NonNull String cartCount) {
        this.cartCount = cartCount;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CART_COUNT, cartCount);
        editor.apply();
    }

    private void refreshIsFbLogin() {
        isFbLogin = pref.getBoolean(IS_FB_LOGIN, false);
    }

    @Nullable
    public boolean getIsFbLogin() {
        return isFbLogin;
    }

    public void setIsFbLogin(@NonNull boolean isFbLogin) {
        this.isFbLogin = isFbLogin;
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_FB_LOGIN, isFbLogin);
        editor.apply();
    }

    private void refreshIsGpLogin() {
        isGpLogin = pref.getBoolean(IS_GP_LOGIN, true);
    }

    @Nullable
    public boolean getIsGpLogin() {
        return isGpLogin;
    }

    public void setIsGpLogin(@NonNull boolean isGpLogin) {
        this.isGpLogin = isGpLogin;
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_GP_LOGIN, isGpLogin);
        editor.apply();
    }

    private void refreshisFirstTime() {
        isFirstTime = pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    private void refreshadCount() {
        adCount = pref.getInt(AD_COUNT, 1);
    }

    public int adCount() {
        return pref.getInt(AD_COUNT, 1);
    }

    public void setadCount(int adCount) {
        this.adCount = adCount;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(AD_COUNT, adCount);
        editor.commit();
    }

    private void refreshAddress() {
        address = pref.getString(ADDRESS, "");
    }

    public String getAddress() {
        return pref.getString(ADDRESS, "");
    }

    public void setAddress(String address) {
        this.address = address;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ADDRESS, address);
        editor.commit();
    }

    private void refreshPhoneno() {
        Phoneno = pref.getString(PHONENO, "");
    }

    public String getPhoneno() {
        return pref.getString(PHONENO, "");
    }

    public void setPhoneno(String Phoneno) {
        this.Phoneno = Phoneno;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PHONENO, Phoneno);
        editor.commit();
    }
}
