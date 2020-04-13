package com.bytotech.Restorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytotech.Restorder.R;
import com.bytotech.Restorder.WS.Response.ResponseMenuSlider;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class MenuViewPagerAdapter extends PagerAdapter {
	
	private List<ResponseMenuSlider.menuMultipleImage> sliderimageDetails;
	private LayoutInflater inflater;
	private Context context;
	
	public MenuViewPagerAdapter(Context context, List<ResponseMenuSlider.menuMultipleImage> sliderimageDetails) {
		this.context = context;
		this.sliderimageDetails = sliderimageDetails;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView((View) object);
	}
	
	@Override
	public int getCount() {
		return sliderimageDetails.size();
	}
	
	@SuppressLint("CheckResult")
	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup view, int position) {
		View myImageLayout = inflater.inflate(R.layout.slide, view, false);
		ImageView myImage = myImageLayout.findViewById(R.id.image);
		
		Glide.with(context)
			   .load(sliderimageDetails.get(position).items_images)
			   .into(myImage);
		view.addView(myImageLayout, 0);
		
		return myImageLayout;
	}
	
	@Override
	public boolean isViewFromObject(@NonNull View view, Object object) {
		return view.equals(object);
	}
	
}
