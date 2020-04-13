package com.bytotech.Restorder.WS.Response;

import java.util.List;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseOfferList {
	public String message;
	public int code;
	public List<promoCode> promo_code;
	
	public class promoCode {
		public String promo_value;
		public String promo_percentage;
		public String promo_title;
		public String promo_desc;
		public String promo_policy;
	}
}