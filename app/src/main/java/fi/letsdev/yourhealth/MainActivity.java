package fi.letsdev.yourhealth;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fi.letsdev.yourhealth.fragments.RingWearerSetupFragment;
import fi.letsdev.yourhealth.fragments.SubscribedPatientListFragment;
import fi.letsdev.yourhealth.fragments.WatcherSetupFragment;
import fi.letsdev.yourhealth.fragments.WelcomeFragment;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.receiver.NetworkConnectivityReceiver;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class MainActivity extends FragmentActivity {

	private static boolean mIsInForegroundMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigate(savedInstanceState);

		NetworkConnectivityReceiver rcvNetworkConnectivity = new NetworkConnectivityReceiver();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(rcvNetworkConnectivity, ifilter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mIsInForegroundMode = true;
	}

	@Override
	protected void onPause() {
		super.onPause();

		mIsInForegroundMode = false;
	}

	private void navigate(Bundle savedInstanceState) {
		Constants.UserRole userRole =
			PreferencesManager.getInstance(getApplicationContext()).loadUserRole();

		if (savedInstanceState == null) {
			switch (userRole) {
				case PATIENT:
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.main_frameLayout, new RingWearerSetupFragment())
						.commit();
					break;
				case WATCHER:
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.main_frameLayout, new WatcherSetupFragment())
						.commit();
					break;
				case NOT_SET:
					getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.main_frameLayout, new WelcomeFragment())
						.commit();
					break;
			}
		}
	}

	public void onRechooseRole() {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.main_frameLayout, new WelcomeFragment())
			.commit();
	}

	public void onUserChooseToBePatient() {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.main_frameLayout, new RingWearerSetupFragment())
			.addToBackStack(null)
			.commit();
	}

	public void onUserChooseToBeWatcher() {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.main_frameLayout, new WatcherSetupFragment())
			.addToBackStack(null)
			.commit();
	}

	public void onShowingPatientListFragment() {
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.main_frameLayout, new SubscribedPatientListFragment())
			.addToBackStack(null)
			.commit();
	}

	public static boolean isInForeGroundMode() {
		return mIsInForegroundMode;
	}
}
