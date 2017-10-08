package fi.letsdev.yourhealth.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fi.letsdev.yourhealth.fragments.RingWearerSetupFragment;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class MySignalsDataReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Integer bpm = intent.getIntExtra(Constants.IntentExtras.BPM, -1);
		String ringWearerChannel =
			PreferencesManager
				.getInstance(context)
				.loadRingWearer()
				.getChannel();

		if (ringWearerChannel == null) return;

		if (RingWearerSetupFragment.shouldAlertEmergency()) {
			if (bpm < Constants.BPM_MIN || bpm > Constants.BPM_MAX) {
				OrtcHandler.getInstance().sendNotification(ringWearerChannel);
			}
		}
	}
}
