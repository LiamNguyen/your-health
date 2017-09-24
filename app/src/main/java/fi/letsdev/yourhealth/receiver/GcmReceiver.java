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
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import ibt.ortc.extensibility.GcmOrtcBroadcastReceiver;
import ibt.ortc.plugins.IbtRealtimeSJ.OrtcMessage;

public class GcmReceiver extends GcmOrtcBroadcastReceiver {
	private static final String TAG = "GcmReceiver";

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

			try {
//				NotificationManager notificationManager =
//					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//				Intent intent = new Intent(context, MainActivity.class);
//
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				PendingIntent pendingIntent =
//					PendingIntent.getActivity(
//						context,
//						9999,
//						intent,
//						PendingIntent.FLAG_UPDATE_CURRENT
//					);

//				Notification notification =
//					new NotificationCompat.Builder(context)
//						.setContentTitle(channel)
//						.setContentText(context.getString(R.string.emergency_notification_message))
//						.setSmallIcon(R.drawable.ic_launcher)
////						.setContentIntent(pendingIntent)
//						.setAutoCancel(true)
//						.build();
//
//				notificationManager.notify(getAppName(context), 9999, notification);

				PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					new Intent(context, MainActivity.class), 0);

				NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("My notification")
						.setContentText("Hello World!");
				mBuilder.setContentIntent(contentIntent);
				mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				mBuilder.setAutoCancel(true);
				NotificationManager mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(1, mBuilder.build());


				if (parsedMessage.equals(context.getString(R.string.emergency_notification_message)))
					NotificationAlertManager.getInstance(context).startAlert();
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
