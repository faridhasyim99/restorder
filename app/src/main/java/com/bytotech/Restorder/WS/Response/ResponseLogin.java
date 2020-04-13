package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseLogin {
	public Response response;
	public LoginDetail login_detail;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class LoginDetail {
		public String user_id;
		public String name;
		public String email;
		public String phone;
		public String address_line_1;
		public String address_line_2;
		public String city;
		public String state;
		public String country;
		public String zipcode;
		public String user_image;
		public String cart_items;
		public String promo_rowcount;
		public String success;
	}
}