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
import android.widget.TextView;

import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseMenuItemDetail;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class CommentAdapter extends RecyclerView.Adapter {
	
	private List<ResponseMenuItemDetail.menuComment> menuComments;
	private Context mContext;
	
	public CommentAdapter(Context mContext, List<ResponseMenuItemDetail.menuComment> menuComments) {
		this.mContext = mContext;
		this.menuComments = menuComments;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			            .inflate(R.layout.item_comment_list, parent, false);
		return new CommentAdapter.MenuViewHolder(v);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("CheckResult")
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
		CommentAdapter.MenuViewHolder holder = (CommentAdapter.MenuViewHolder) viewHolder;
		holder.tvTitle.setText(menuComments.get(position).name);
		holder.tvComment.setText(menuComments.get(position).comment);
	}
	
	@Override
	public int getItemCount() {
		return menuComments.size();
	}
	
	private class MenuViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvTitle, tvComment;
		
		MenuViewHolder(@NonNull View itemView) {
			super(itemView);
			
			tvTitle = itemView.findViewById(R.id.tvTitle);
			tvComment = itemView.findViewById(R.id.tvComment);
		}
	}
}