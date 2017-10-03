package fi.letsdev.yourhealth.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;

public class LocalNotificationHelper {

	private static LocalNotificationHelper instance;

	private LocalNotificationHelper(Context context) {
		this.mContext = context;
	}

	public static LocalNotificationHelper getInstance(Context context) {
		if (instance == null) {
			instance = new LocalNotificationHelper(context);
		}
		return instance;
	}

	private NotificationManager manager;
	private static final String PRIMARY_CHANNEL = "default";
	private Context mContext;

	public void createNotification(String title, String content) {
		NotificationManager mNotificationManager =
			(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
			new Intent(mContext, MainActivity.class), 0);

		if (Build.VERSION.SDK_INT < 26) {

			NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(mContext)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(title)
					.setContentText(content);
			mBuilder.setContentIntent(contentIntent);
			mBuilder.setDefaults(Notification.DEFAULT_SOUND);
			mBuilder.setAutoCancel(true);
			mNotificationManager.notify(1, mBuilder.build());
		} else {

			NotificationChannel notificationChannel = new NotificationChannel(
				PRIMARY_CHANNEL,
				"Channel name",
				NotificationManager.IMPORTANCE_MAX
			);
			notificationChannel.setDescription("EMERGENCY ALERT");
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
			mNotificationManager.createNotificationChannel(notificationChannel);

			Notification.Builder notification = new Notification.Builder(mContext, PRIMARY_CHANNEL)
				.setContentTitle(title)
				.setContentText(content)
				.setSmallIcon(getSmallIcon())
				.setAutoCancel(true);
			notification.setContentIntent(contentIntent);

			getManager().notify(9000, notification.build());
		}
	}

	private int getSmallIcon() {
		return android.R.drawable.stat_notify_chat;
	}

	private NotificationManager getManager() {
		if (manager == null) {
			manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		return manager;
	}
}
