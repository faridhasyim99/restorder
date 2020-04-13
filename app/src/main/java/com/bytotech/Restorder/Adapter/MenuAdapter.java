package com.bytotech.Restorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseMenu;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class MenuAdapter extends RecyclerView.Adapter {
	
	private List<ResponseMenu.MenucategoryList> memberLists;
	private Context mContext;
	private OnDetailsClick onDetailClick;
	
	public MenuAdapter(Context mContext, List<ResponseMenu.MenucategoryList> memberLists) {
		this.mContext = mContext;
		this.memberLists = memberLists;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			            .inflate(R.layout.item_menu_list, parent, false);
		return new MenuAdapter.MenuViewHolder(v);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("CheckResult")
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
		MenuAdapter.MenuViewHolder holder = (MenuAdapter.MenuViewHolder) viewHolder;
		holder.tvMenuTitle.setText(memberLists.get(position).category_name);
		holder.tvMenuItems.setText(memberLists.get(position).count + " Items");
		if (!memberLists.get(position).rate_avg.equalsIgnoreCase("")) {
			holder.rating.setRating(Float.parseFloat(memberLists.get(position).rate_avg));
		} else {
			holder.rating.setRating(0);
		}
		Glide.with(mContext)
			   .load(memberLists.get(position).category_image)
			   .into(holder.ivMenu);
		
		holder.ivMenuDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onDetailClick.onDetailClick(position);
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return memberLists.size();
	}
	
	public void setOnDetailClick(OnDetailsClick onDetailClick) {
		this.onDetailClick = onDetailClick;
		
	}
	
	public interface OnDetailsClick {
		void onDetailClick(int position);
	}
	
	private class MenuViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvMenuTitle, tvMenuItems;
		RatingBar rating;
		CircleImageView ivMenu, ivMenuDetail;
		
		MenuViewHolder(@NonNull View itemView) {
			super(itemView);
			tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
			tvMenuItems = itemView.findViewById(R.id.tvMenuItems);
			rating = itemView.findViewById(R.id.rating);
			ivMenu = itemView.findViewById(R.id.ivMenu);
			ivMenuDetail = itemView.findViewById(R.id.ivMenuDetail);
			
		}
	}
}