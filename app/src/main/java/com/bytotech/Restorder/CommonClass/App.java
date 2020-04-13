package com.bytotech.Restorder.CommonClass;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bytotech.Restorder.Activity.MainActivity;
import com.facebook.FacebookSdk;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * @author Pradip Bhuva
 * Bytotech Solutions
 * +91 8866036909
 */
public class App extends Application {
	@SuppressLint("StaticFieldLeak")
	public static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		FacebookSdk.sdkInitialize(getApplicationContext());
		
		OneSignal.startInit(this)
			   .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
			   .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
			   .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
			   .unsubscribeWhenNotificationsAreDisabled(true)
			   .init();
	}
	
	private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
		@Override
		public void notificationReceived(OSNotification notification) {
			JSONObject data = notification.payload.additionalData;
			String notificationID = notification.payload.notificationID;
			String title = notification.payload.title;
			String body = notification.payload.body;
			String smallIcon = notification.payload.smallIcon;
			String largeIcon = notification.payload.largeIcon;
			String bigPicture = notification.payload.bigPicture;
			String smallIconAccentColor = notification.payload.smallIconAccentColor;
			String sound = notification.payload.sound;
			String ledColor = notification.payload.ledColor;
			int lockScreenVisibility = notification.payload.lockScreenVisibility;
			String groupKey = notification.payload.groupKey;
			String groupMessage = notification.payload.groupMessage;
			String fromProjectNumber = notification.payload.fromProjectNumber;
			String rawPayload = notification.payload.rawPayload;
			
			String customKey;
			
			Log.i("OneSignalExample", "NotificationID received: " + notificationID);
			
			if (data != null) {
				customKey = data.optString("customkey", null);
				if (customKey != null)
					Log.i("OneSignalExample", "customkey set with value: " + customKey);
			}
		}
	}
	
	private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
		@Override
		public void notificationOpened(OSNotificationOpenResult result) {
			OSNotificationAction.ActionType actionType = result.action.type;
			JSONObject data = result.notification.payload.additionalData;
			String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
			
			String customKey;
			String openURL = null;
			Object activityToLaunch = MainActivity.class;
			
			if (data != null) {
				customKey = data.optString("customkey", null);
				openURL = data.optString("openURL", null);
				
				if (customKey != null)
					Log.i("OneSignalExample", "customkey set with value: " + customKey);
				
				if (openURL != null)
					Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
			}
			
			if (actionType == OSNotificationAction.ActionType.ActionTaken) {
				Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
				
				if (result.action.actionID.equals("id1")) {
					Log.i("OneSignalExample", "button id called: " + result.action.actionID);
					activityToLaunch = MainActivity.class;
				} else
					Log.i("OneSignalExample", "button id called: " + result.action.actionID);
			}
			Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("openURL", openURL);
			Log.i("OneSignalExample", "openURL = " + openURL);
			startActivity(intent);
			
		}
	}
}