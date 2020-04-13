package com.bytotech.Restorder.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseOfferList;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class PromoAdapter extends RecyclerView.Adapter {
	
	private List<ResponseOfferList.promoCode> promoCodes;
	private Context mContext;
	private OnTermClick onTermClick;
	
	public PromoAdapter(Context mContext, List<ResponseOfferList.promoCode> promoCodes) {
		this.mContext = mContext;
		this.promoCodes = promoCodes;
		
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
			            .inflate(R.layout.item_promo_list, parent, false);
		return new PromoAdapter.MenuViewHolder(v);
	}
	
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("CheckResult")
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
		final PromoAdapter.MenuViewHolder holder = (PromoAdapter.MenuViewHolder) viewHolder;
		holder.tvPromoTitle.setText(promoCodes.get(position).promo_title);
		holder.tvPromoDesc.setText(Html.fromHtml(promoCodes.get(position).promo_desc));
		holder.tvPromoCode.setText(promoCodes.get(position).promo_value);
		holder.tvPromoTerm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTermClick.OnTermClick(position);
			}
		});
		holder.tvPromoCode.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				String strCode = promoCodes.get(position).promo_value;
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("Code copied", strCode);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(mContext, "Code copied", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}
	
	@Override
	public int getItemCount() {
		return promoCodes.size();
	}
	
	public void setTermClick(OnTermClick OnTermClick) {
		this.onTermClick = OnTermClick;
		
	}
	
	public interface OnTermClick {
		void OnTermClick(int position);
	}
	
	private class MenuViewHolder extends RecyclerView.ViewHolder {
		
		TextView tvPromoTitle, tvPromoDesc, tvPromoTerm, tvPromoCode;
		
		MenuViewHolder(@NonNull View itemView) {
			super(itemView);
			tvPromoTitle = itemView.findViewById(R.id.tvPromoTitle);
			tvPromoDesc = itemView.findViewById(R.id.tvPromoDesc);
			tvPromoTerm = itemView.findViewById(R.id.tvPromoTerm);
			tvPromoCode = itemView.findViewById(R.id.tvPromoCode);
		}
	}
}