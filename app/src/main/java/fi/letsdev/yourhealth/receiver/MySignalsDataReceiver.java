package fi.letsdev.yourhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fi.letsdev.yourhealth.utils.Constants;

public class MySignalsDataReceiver extends BroadcastReceiver {

	private final static String TAG = MySignalsDataReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Integer bpm = intent.getIntExtra(Constants.IntentExtras.BPM, -1);
		Log.d(TAG, bpm.toString());

		// TODO: Process bpm with business logic
	}
}
