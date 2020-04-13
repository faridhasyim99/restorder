package com.bytotech.Restorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bytotech.Restorder.Activity.OrderDetailsActivity;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseOrder;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class OrderAdapter extends RecyclerView.Adapter {

    private List<ResponseOrder.orderSummary> cartList;
    private Context mContext;
    public OnRateUsClick onRateUsClick;

    public OrderAdapter(Context mContext, List<ResponseOrder.orderSummary> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list, parent, false);
        return new OrderAdapter.MenuViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final OrderAdapter.MenuViewHolder holder = (OrderAdapter.MenuViewHolder) viewHolder;
        if (cartList != null) {
            holder.tvItemTitle.setText(cartList.get(position).order_id);
            holder.tvItemPrice.setText(mContext.getString(R.string.currency) + " " + cartList.get(position).total_price);
            holder.tvItemstatus.setText("Your order : " + cartList.get(position).status);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    intent.putExtra("orderId", cartList.get(position).order_id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemTitle, tvItemPrice, tvItemstatus;
        /*CircleImageView  ivRateUs;*/

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemstatus = itemView.findViewById(R.id.tvItemstatus);
            //ivRateUs = itemView.findViewById(R.id.ivRateUs);

        }
    }

    public interface OnRateUsClick {
        void OnRateUsClick(int position);
    }

    public void setOnRateUsClick(OnRateUsClick onRateUsClick) {
        this.onRateUsClick = onRateUsClick;
    }
}

