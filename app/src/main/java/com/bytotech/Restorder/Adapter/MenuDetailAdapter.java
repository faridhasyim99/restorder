package com.bytotech.Restorder.Adapter;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.Activity.MenuDetailItemActivity;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseMenuDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class MenuDetailAdapter extends RecyclerView.Adapter {
	
	private List<ResponseMenuDetail.MenuListCat> menuListCats;
	private Context mContext;
	
	public MenuDetailAdapter(Context mContext, List<ResponseMenuDetail.MenuListCat> menuListCats) {
		this.mContext = mContext;
		this.menuListCats = menuListCats;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			            .inflate(R.layout.item_menu_detail, parent, false);
		return new MenuDetailAdapter.MenuViewHolder(v);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("CheckResult")
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
		final MenuDetailAdapter.MenuViewHolder holder = (MenuDetailAdapter.MenuViewHolder) viewHolder;
		holder.tvMenuTitle.setText(menuListCats.get(position).menu_name);
		holder.tvMenucount.setText(mContext.getString(R.string.currency)+" "+menuListCats.get(position).menu_price);
		holder.tvMenuWeight.setText(menuListCats.get(position).menu_weight);
		
		if (!menuListCats.get(position).rate_avg.equalsIgnoreCase("")) {
			holder.rating.setRating(Float.parseFloat(menuListCats.get(position).rate_avg));
		} else {
			holder.rating.setRating(0);
		}
		Glide.with(mContext)
			   .load(menuListCats.get(position).menu_image)
			   .into(holder.ivMenu);
		holder.ivMenuDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Bitmap image = ((BitmapDrawable) holder.ivMenu.getDrawable()).getBitmap();
				File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
				try {
					FileOutputStream outputStream = new FileOutputStream(file);
					image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				final Uri uri = Uri.fromFile(file);
				String url = uri.toString();
				Intent intent = new Intent(mContext, MenuDetailItemActivity.class);
				intent.putExtra("mid", menuListCats.get(position).mid);
				intent.putExtra("title", menuListCats.get(position).menu_name);
				intent.putExtra("detail", menuListCats.get(position).menu_info);
				intent.putExtra("menu_price", menuListCats.get(position).menu_price);
				intent.putExtra("menu_weight", menuListCats.get(position).menu_weight);
				intent.putExtra("uri", url);
				if (menuListCats.get(position).itemCount != 0) {
					intent.putExtra("itemCount", menuListCats.get(position).itemCount);
				} else {
					intent.putExtra("itemCount", 0);
				}
				ActivityOptions options = ActivityOptions.makeCustomAnimation(mContext, R.anim.slide_in, R.anim.slide_out);
				mContext.startActivity(intent, options.toBundle());
				
			}
		});
		
	}
	
	@Override
	public int getItemCount() {
		return menuListCats.size();
	}
	
	private class MenuViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvMenuTitle, tvMenucount, tvItemCount, tvMenuWeight;
		CircleImageView ivMenuDetail, ivMenuPlus, ivMenuminus, ivMenu;
		RatingBar rating;
		CardView detailCard, cardAddItems;
		
		MenuViewHolder(@NonNull View itemView) {
			super(itemView);
			tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
			tvMenucount = itemView.findViewById(R.id.tvMenucount);
			tvItemCount = itemView.findViewById(R.id.tvItemCount);
			tvMenuWeight = itemView.findViewById(R.id.tvMenuWeight);
			ivMenu = itemView.findViewById(R.id.ivMenu);
			ivMenuDetail = itemView.findViewById(R.id.ivMenuDetail);
			ivMenuPlus = itemView.findViewById(R.id.ivMenuPlus);
			ivMenuminus = itemView.findViewById(R.id.ivMenuminus);
			rating = itemView.findViewById(R.id.rating);
			detailCard = itemView.findViewById(R.id.detailCard);
			cardAddItems = itemView.findViewById(R.id.cardAddItems);
			
		}
	}
}