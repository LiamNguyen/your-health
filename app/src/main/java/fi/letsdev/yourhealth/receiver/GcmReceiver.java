package fi.letsdev.yourhealth.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import ibt.ortc.extensibility.GcmOrtcBroadcastReceiver;
import ibt.ortc.plugins.IbtRealtimeSJ.OrtcMessage;

public class GcmReceiver extends GcmOrtcBroadcastReceiver {
	private static final String TAG = "GcmReceiver";

	public GcmReceiver() {}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

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

			try {
				NotificationManager notificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				NotificationCompat.Builder notification =
					new NotificationCompat.Builder(context)
						.setContentTitle(channel).setContentText(
							context.getString(R.string.emergency_notification_message)
						)
						.setSmallIcon(R.drawable.ic_launcher);
				Intent intent = new Intent(context, MainActivity.class);
				PendingIntent pi = PendingIntent.getActivity(context, 0, intent, Intent.FILL_IN_ACTION);

				notification.setContentIntent(pi);

				notificationManager.notify(getAppName(context), 9999, notification.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getAppName(Context context) {
		CharSequence appName =
			context
				.getPackageManager()
				.getApplicationLabel(context.getApplicationInfo());

		return (String)appName;
	}

}
