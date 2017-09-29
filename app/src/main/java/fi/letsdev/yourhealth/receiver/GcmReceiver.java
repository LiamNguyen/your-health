package fi.letsdev.yourhealth.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
				NotificationManager mNotificationManager =
					(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				if (Build.VERSION.SDK_INT < 26) {
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
						new Intent(context, MainActivity.class), 0);

					NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(context)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle("ALERT!!!")
							.setContentText(parsedMessage);
					mBuilder.setContentIntent(contentIntent);
					mBuilder.setDefaults(Notification.DEFAULT_SOUND);
					mBuilder.setAutoCancel(true);
					mNotificationManager.notify(1, mBuilder.build());
				} else {
//					NotificationChannel notificationChannel = new NotificationChannel("default",
//						"Channel name",
//						NotificationManager.IMPORTANCE_DEFAULT
//					);
//					notificationChannel.setDescription("Channel description");
//					mNotificationManager.createNotificationChannel(notificationChannel);

					NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(context)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle("ALERT!!!")
							.setContentText(parsedMessage);

					Intent resultIntent = new Intent(context, MainActivity.class);

					TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
					stackBuilder.addParentStack(MainActivity.class);
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent =
						stackBuilder.getPendingIntent(
							0,
							PendingIntent.FLAG_UPDATE_CURRENT
						);
					mBuilder.setContentIntent(resultPendingIntent);
					mNotificationManager.notify(1, mBuilder.build());
				}

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
