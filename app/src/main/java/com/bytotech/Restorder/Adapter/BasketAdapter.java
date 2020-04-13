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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseCartList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class BasketAdapter extends RecyclerView.Adapter {
	
	private List<ResponseCartList.cartList> cartList;
	private Context mContext;
	private OnPlusClick onPlusClick;
	private OnMinusClick onMinusClick;
	private OnRemoveClick onRemoveClick;
	
	public BasketAdapter(Context mContext, List<ResponseCartList.cartList> cartList) {
		this.mContext = mContext;
		this.cartList = cartList;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			            .inflate(R.layout.item_basket_list, parent, false);
		return new BasketAdapter.MenuViewHolder(v);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("CheckResult")
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
		final BasketAdapter.MenuViewHolder holder = (BasketAdapter.MenuViewHolder) viewHolder;
		holder.tvItemTitle.setText(cartList.get(position).menu_name);
		holder.tvItemPrice.setText(mContext.getString(R.string.currency)+" "+cartList.get(position).menu_price);
		holder.tvItemCount.setText(cartList.get(position).menu_qty);
		
		holder.ivPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strItemCount = holder.tvItemCount.getText().toString().trim();
				float price = Float.parseFloat(cartList.get(position).menu_price);
				int count = Integer.parseInt(strItemCount);
				count++;
				onPlusClick.OnPlusClick(position, count, price);
			}
		});
		
		holder.ivMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strItemCount = holder.tvItemCount.getText().toString().trim();
				float price = Float.parseFloat(cartList.get(position).menu_price);
				int count = Integer.parseInt(strItemCount);
				if (count != 0) {
					count--;
				}
				onMinusClick.OnMinusClick(position, count, price);
				
			}
		});
		
		holder.tvRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onRemoveClick.OnRemoveClick(position);
			}
		});
		
		Glide.with(mContext)
			   .load(cartList.get(position).menu_image)
			   .into(holder.ivItem);
		
	}
	
	@Override
	public int getItemCount() {
		return cartList.size();
	}
	
	public void setOnPlusClick(OnPlusClick OnPlusClick) {
		this.onPlusClick = OnPlusClick;
		
	}
	
	public void setOnMinusClick(OnMinusClick OnMinusClick) {
		this.onMinusClick = OnMinusClick;
		
	}
	
	public void setRemoveClick(OnRemoveClick OnRemoveClick) {
		this.onRemoveClick = OnRemoveClick;
		
	}
	
	public interface OnPlusClick {
		void OnPlusClick(int position, int count, float price);
	}
	
	public interface OnMinusClick {
		void OnMinusClick(int position, int count, float price);
	}
	
	public interface OnRemoveClick {
		void OnRemoveClick(int position);
	}
	
	private class MenuViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvItemTitle, tvItemPrice, tvItemCount, tvRemove;
		CircleImageView ivItem;
		ImageView ivMinus, ivPlus;
		
		MenuViewHolder(@NonNull View itemView) {
			super(itemView);
			
			tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
			tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
			tvItemCount = itemView.findViewById(R.id.tvItemCount);
			tvRemove = itemView.findViewById(R.id.tvRemove);
			ivItem = itemView.findViewById(R.id.ivItem);
			ivMinus = itemView.findViewById(R.id.ivMinus);
			ivPlus = itemView.findViewById(R.id.ivPlus);
			
		}
	}
}
