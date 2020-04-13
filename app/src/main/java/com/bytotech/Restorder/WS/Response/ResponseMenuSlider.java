package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseMenuSlider {
	public String message;
	public int code;
	public List<menuMultipleImage> menu_multiple_image;
	
	public class menuMultipleImage {
		public String mid;
		public String items_images;
	}
	
}
