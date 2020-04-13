package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseMenuItemDetail {
	
	public Response response;
	public MenuListCat menu_list_cat;
	public List<menuComment> menu_comment;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class MenuListCat {
		public String menu_name;
		public String menu_price;
		public String menu_qty;
		public String count;
	}
	
	public class menuComment {
		public String c_id;
		public String comment;
		public String name;
		public String user_id;
	}
}