package fi.letsdev.yourhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fi.letsdev.yourhealth.fragments.RingWearerSetupFragment;
import fi.letsdev.yourhealth.utils.Constants;

public class MySignalsDataReceiver extends BroadcastReceiver {

	private final static String TAG = MySignalsDataReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Integer bpm = intent.getIntExtra(Constants.IntentExtras.BPM, -1);
		Integer stepsPerMinute = intent.getIntExtra(Constants.IntentExtras.STEPS_PER_MINUTE, -1);
		Log.d(TAG, bpm.toString());
		Log.d(TAG, stepsPerMinute.toString());

		if (RingWearerSetupFragment.shouldAlertEmergency()) {
			if (bpm < Constants.BPM_MIN || bpm > Constants.BPM_MAX) {
				// TODO: Broadcast emergency notificaiton alert
			}
		}
		// TODO: Process bpm, steps per minute with business logic
	}
}
