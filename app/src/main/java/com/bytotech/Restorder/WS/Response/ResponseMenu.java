package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseMenu {
	
	public List<MenucategoryList> menucategory_list;
	

		public String message;
		public int code;

	
	public class MenucategoryList {
		public String cid;
		public String category_name;
		public String category_image;
		public String total_rate;
		public String rate_avg;
		public String count;
	}
}
