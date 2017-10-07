package fi.letsdev.yourhealth.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import fi.letsdev.yourhealth.interfaces.InterfaceHeartRateDataCallback;

public class BluetoothBackgroundService extends Service {

	private final static String TAG = BluetoothBackgroundService.class.getSimpleName();

	private final IBinder mBinder = new LocalBinder();
	private MySignalSensorService mySignalSensorService;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "on start command");

		mySignalSensorService = MySignalSensorService.getInstance(this, intent);
		mySignalSensorService.startService();

		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public BluetoothBackgroundService getInstance() { return BluetoothBackgroundService.this; }
	}

	public void setListener(InterfaceHeartRateDataCallback listener) {
		mySignalSensorService.setListener(listener);
	}
}
