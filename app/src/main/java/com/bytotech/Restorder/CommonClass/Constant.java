package com.bytotech.Restorder.CommonClass;

public class Constant {
	
	public static final String WEBSERVICE_PATH = "http://bytotech.com/envato/restorder/";
//public static final String WEBSERVICE_PATH = "http://192.168.1.22/Restorder/";
	public static final String WEBSERVICE_API_PATH = "api.php?";
	public static final int Response_OK = 200;
	
	public static class RequestPermissions {
		public static final int READ_EXTERNAL_STORAGE = 1111;
		public static final int CAMERA = 1112;
	}
	
	public static class ActivityForResult {
		public static final int CAMERA = 1010;
		public static final int GALLERY = 1011;
	}
}
