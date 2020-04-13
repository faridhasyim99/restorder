package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseForgotPassword {
	public Response response;
	public ForgetPassword forget_password;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class ForgetPassword {
		public String success;
	}
}