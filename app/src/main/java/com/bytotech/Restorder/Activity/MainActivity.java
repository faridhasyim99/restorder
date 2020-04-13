package com.bytotech.Restorder.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytotech.Restorder.CommonClass.CommonClass;
import com.bytotech.Restorder.CommonClass.PreferenceUtils;
import com.bytotech.Restorder.Fragment.AboutUsFragment;
import com.bytotech.Restorder.Fragment.BasketFragment;
import com.bytotech.Restorder.Fragment.MenuFragment;
import com.bytotech.Restorder.Fragment.OrderFragment;
import com.bytotech.Restorder.Fragment.PromoFragment;
import com.bytotech.Restorder.Fragment.SearchFragment;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.View.AdvanceDrawerLayout;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    CircleImageView ivEventPic;
    private CommonClass cc;
    private PreferenceUtils preferenceUtils;
    private AdvanceDrawerLayout drawer;
    private TextView tvCartCount;
    private ImageView ivCart;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        cc = new CommonClass(this);
        preferenceUtils = new PreferenceUtils(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivCart = findViewById(R.id.ivCart);
        tvCartCount = findViewById(R.id.tvCartCount);
        ivBack.setImageResource(R.drawable.ic_menu);
        ivBack.setOnClickListener(this);
        ivCart.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        assert preferenceUtils.getCartCount() != null;
        if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
            tvCartCount.setVisibility(View.GONE);
        } else {
            tvCartCount.setVisibility(View.VISIBLE);
        }
        tvCartCount.setText(preferenceUtils.getCartCount());

        replaceFragment(new MenuFragment());

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.tvName)).setText(preferenceUtils.getUserName());
        ((TextView) header.findViewById(R.id.tvEmail)).setText(preferenceUtils.getUserEmail());
        ivEventPic = header.findViewById(R.id.ivEventPic);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.user);
        requestOptions.error(R.drawable.user);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(preferenceUtils.getUserImage())
                .into(ivEventPic);

        LinearLayout llNavHeader = header.findViewById(R.id.llNavHeader);
        llNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(1);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            findViewById(R.id.flCart).setVisibility(View.VISIBLE);
            replaceFragment(new MenuFragment());
        } else if (id == R.id.nav_basket) {
            findViewById(R.id.flCart).setVisibility(View.GONE);
            replaceFragment(new BasketFragment());
        } else if (id == R.id.nav_promo) {
            findViewById(R.id.flCart).setVisibility(View.VISIBLE);
            replaceFragment(new PromoFragment());
        } else if (id == R.id.nav_order) {
            findViewById(R.id.flCart).setVisibility(View.VISIBLE);
            replaceFragment(new OrderFragment());
        } else if (id == R.id.nav_search) {
            findViewById(R.id.flCart).setVisibility(View.VISIBLE);
            replaceFragment(new SearchFragment());
        } else if (id == R.id.nav_rate_us) {
            cc.showToast("Rate Us");
        } else if (id == R.id.nav_about_us) {
            findViewById(R.id.flCart).setVisibility(View.VISIBLE);
            replaceFragment(new AboutUsFragment());
        } else if (id == R.id.nav_share) {
            cc.showToast("Share");
        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (preferenceUtils.getIsFbLogin()) {
                                LoginManager.getInstance().logOut();
                                cc.logout();
                                finish();
                            } else if (preferenceUtils.getIsGpLogin()) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                preferenceUtils.setIsGpLogin(false);
                                            }
                                        });
                            } else {
                                cc.logout();
                            }
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options =
                                    ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.slide_in, R.anim.slide_out);
                            startActivity(intent, options.toBundle());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.ivBack:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(Gravity.LEFT);
                } else {
                    drawer.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.ivCart:
                cc.startCartActivity();
                break;
        }

    }

    public void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    protected void onResume() {
        preferenceUtils = new PreferenceUtils(this);
        assert preferenceUtils.getCartCount() != null;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.user);
        requestOptions.error(R.drawable.user);

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(preferenceUtils.getUserImage())
                .into(ivEventPic);
        if (preferenceUtils.getCartCount().equals("") || preferenceUtils.getCartCount().equals("0")) {
            tvCartCount.setVisibility(View.GONE);
        } else {
            tvCartCount.setVisibility(View.VISIBLE);
        }
        tvCartCount.setText(preferenceUtils.getCartCount());
        super.onResume();
    }
}
