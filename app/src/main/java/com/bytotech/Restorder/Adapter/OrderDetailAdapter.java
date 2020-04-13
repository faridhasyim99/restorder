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

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseOrderDetails;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderDetailAdapter extends RecyclerView.Adapter {

    private List<ResponseOrderDetails.orderList> cartList;
    private Context mContext;
    public OrderDetailAdapter.OnRateUsClick onRateUsClick;

    public OrderDetailAdapter(Context mContext, List<ResponseOrderDetails.orderList> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order__details, parent, false);
        return new OrderDetailAdapter.MenuViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final OrderDetailAdapter.MenuViewHolder holder = (OrderDetailAdapter.MenuViewHolder) viewHolder;
        if (cartList != null) {
            holder.tvItemTitle.setText(cartList.get(position).menu_name);
            holder.tvItemPrice.setText(mContext.getString(R.string.currency) + " " + cartList.get(position).menu_price);
            holder.tvItemstatus.setText("Your order : " + cartList.get(position).status);
            holder.tvItemQTY.setText(cartList.get(position).menu_qty);
            holder.tvLabelTax.setText("Tax (" + cartList.get(position).tax + "%)");
            String totalPrice = "" + Float.parseFloat(cartList.get(position).menu_price) * Float.parseFloat(cartList.get(position).menu_qty);
            holder.tvSubTotal.setText(totalPrice);
            float tax = (Float.parseFloat(totalPrice) * Float.parseFloat(cartList.get(position).tax)) / 100;
            holder.tvTax.setText("" + tax);
            float total = ((Float.parseFloat(cartList.get(position).menu_price) * Float.parseFloat(cartList.get(position).menu_qty)) + tax);

            holder.tvTotal.setText("" + total);
            Glide.with(mContext)
                    .load(cartList.get(position).menu_image)
                    .into(holder.ivMenu);

            if (cartList.get(position).status.equals("Delivered")) {
                holder.ivRateUs.setVisibility(View.VISIBLE);
            } else {
                holder.ivRateUs.setVisibility(View.GONE);
            }
            holder.ivRateUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRateUsClick.OnRateUsClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle, tvItemPrice, tvItemstatus, tvItemQTY, tvSubTotal,
                tvLabelTax, tvTax, tvTotal;
        CircleImageView ivMenu, ivRateUs;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemstatus = itemView.findViewById(R.id.tvItemstatus);
            tvItemQTY = itemView.findViewById(R.id.tvItemQTY);
            tvSubTotal = itemView.findViewById(R.id.tvSubTotal);
            tvLabelTax = itemView.findViewById(R.id.tvLabelTax);
            tvTax = itemView.findViewById(R.id.tvTax);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            ivMenu = itemView.findViewById(R.id.ivMenu);
            ivRateUs = itemView.findViewById(R.id.ivRateUs);

        }
    }

    public interface OnRateUsClick {
        void OnRateUsClick(int position);
    }

    public void setOnRateUsClick(OrderDetailAdapter.OnRateUsClick onRateUsClick) {
        this.onRateUsClick = onRateUsClick;
    }
}

