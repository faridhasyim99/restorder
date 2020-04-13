package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseAboutUs {
	public String message;
	public int code;
	public aboutUsDetail about_us_detail;
	
	public class aboutUsDetail {
		public String about_desc;
	}
}