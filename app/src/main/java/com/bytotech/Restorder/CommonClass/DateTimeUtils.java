package com.bytotech.Restorder.CommonClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class DateTimeUtils {
	public static String getCurrentDateTimeMix() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
			return sdf.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return "0000-00-00 00:00:00";
		}
	}
	
}
