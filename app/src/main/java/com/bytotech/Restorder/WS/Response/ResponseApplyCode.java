package com.bytotech.Restorder.WS.Response;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class ResponseApplyCode {
	public String message;
	public int code;
	
	public promoCodeApply promo_code_apply;
	
	public class promoCodeApply {
		public String promo_value;
		public String promo_percentage;
		public String promo_title;
	}
}