package fi.letsdev.yourhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.utils.DialogHelper;
import fi.letsdev.yourhealth.utils.LocalNotificationHelper;
import fi.letsdev.yourhealth.utils.NetworkConnectivityHelper;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!NetworkConnectivityHelper.isOnline(context)) {
			if (MainActivity.isInForeGroundMode())
				DialogHelper.createConnectivityStatusDialog(context);
			else
				LocalNotificationHelper
					.getInstance(context)
					.createNotification(
						"ALERT!",
						context.getString(R.string.message_notification_network_connectivity_lost)
					);
		}
	}
}
