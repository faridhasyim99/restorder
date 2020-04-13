package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseOrder {
	
	public String message;
	public int code;
	
	public List<orderSummary> user_order_list;
	
	public class orderSummary {
		public String order_id;
		public String status;
		public String total_price;
		}
	}
