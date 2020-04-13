package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseCartList {
	public String message;
	public int code;
	public String tax;
	public List<cartList> cart_list;
	
	public class cartList {
		public String cart_id;
		public String user_id;
		public String menu_id;
		public String menu_name;
		public String menu_image;
		public String menu_qty;
		public String menu_price;
		public String base_price;
	}
}