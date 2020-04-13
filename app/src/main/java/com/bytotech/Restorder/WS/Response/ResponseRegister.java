package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseRegister {
	
	public Response response;
	public RegisterDetail register_detail;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class RegisterDetail {
		public String user_id;
		public String name;
		public String email;
		public String phone;
		public String success;
	}
}