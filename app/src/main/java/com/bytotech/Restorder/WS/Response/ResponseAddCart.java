package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseAddCart {
	public Response response;
	public AddCart add_cart;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class AddCart {
		public String cart_items;
		public String success;
		
	}
}