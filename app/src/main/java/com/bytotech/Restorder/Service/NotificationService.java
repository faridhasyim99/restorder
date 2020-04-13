package com.bytotech.Restorder.Service;

import android.support.v4.app.NotificationCompat;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;


public class NotificationService extends NotificationExtenderService {
	@Override
	protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
		OverrideSettings overrideSettings = new OverrideSettings();
		overrideSettings.extender = new NotificationCompat.Extender() {
			@Override
			public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
				return builder.setColor(new BigInteger("FFCA4E", 16).intValue());
			}
		};
		OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
		return true;
	}
}
