package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseSearch {
	public String message;
	public int code;
	
	public List<menuSearch> menu_search;
	
	public class menuSearch {
		public String mid;
		public String menu_name;
		public String menu_info;
		public String menu_type;
		public String menu_image;
		public String menu_price;
		public String menu_weight;
	}
}