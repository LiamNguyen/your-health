package fi.letsdev.yourhealth.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BluetoothBackgroundService extends Service {

	private final static String TAG = BluetoothBackgroundService.class.getSimpleName();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "on start command");

		MySignalSensorService.getInstance(this).startService();

		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
