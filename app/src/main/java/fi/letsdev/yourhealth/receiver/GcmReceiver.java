package fi.letsdev.yourhealth.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.utils.LocalNotificationHelper;
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import ibt.ortc.extensibility.GcmOrtcBroadcastReceiver;
import ibt.ortc.plugins.IbtRealtimeSJ.OrtcMessage;

public class GcmReceiver extends GcmOrtcBroadcastReceiver {
	private static final String TAG = GcmReceiver.class.getSimpleName();

	public GcmReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Notification received");
		Bundle extras = intent.getExtras();
		if (extras != null) {
			createNotification(context, extras);
		}
	}

	public void createNotification(Context context, Bundle extras) {
		String message = extras.getString("M");
		String channel = extras.getString("C");
		String payload = extras.getString("P");

		if (message != null && !message.isEmpty()) {

			String parsedMessage = OrtcMessage.parseOrtcMultipartMessage(message);

			if (payload != null) {
				Log.i(TAG, String.format(
					"Custom push notification on channel: %s message: %s payload: %s",
					channel, parsedMessage, payload
				));
			} else {
				Log.i(TAG, String.format(
					"Automatic push notification on channel: %s message: %s ",
					channel, parsedMessage
				));
			}

			if (parsedMessage.equals(context.getString(R.string.emergency_notification_message)))
				NotificationAlertManager.getInstance(context).startAlert();

			if (!MainActivity.isInForeGroundMode())
				LocalNotificationHelper
					.getInstance(context)
					.createNotification("EMERGENCY!!", parsedMessage);
		}
	}
}
