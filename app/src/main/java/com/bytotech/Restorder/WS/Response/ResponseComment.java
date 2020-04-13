package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseComment {
	
	public Response response;
	public commentLst comment_lst;
	
	public class Response {
		public String message;
		public int code;
	}
	
	public class commentLst {
		public String count;
	}
}