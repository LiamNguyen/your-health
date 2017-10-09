package fi.letsdev.yourhealth;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fi.letsdev.yourhealth.fragments.ReconfirmFragment;
import fi.letsdev.yourhealth.fragments.RingWearerCollectInformationFragment;
import fi.letsdev.yourhealth.fragments.RingWearerSetupFragment;
import fi.letsdev.yourhealth.fragments.SubscribedPatientListFragment;
import fi.letsdev.yourhealth.fragments.WatcherSetupFragment;
import fi.letsdev.yourhealth.fragments.WelcomeFragment;
import fi.letsdev.yourhealth.receiver.NetworkConnectivityReceiver;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.FragmentHelper;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class MainActivity extends FragmentActivity {

	private static boolean mIsInForegroundMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigate(savedInstanceState);

		// Register network connectivity receiver

		NetworkConnectivityReceiver rcvNetworkConnectivity = new NetworkConnectivityReceiver();
		IntentFilter ifilter = new IntentFilter();
		ifilter.addAction(Constants.IntentActions.CONNECTIVITY_CHANGE);
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
					FragmentHelper.addFragment(new RingWearerSetupFragment(), this);
					break;
				case WATCHER:
					FragmentHelper.addFragment(new WatcherSetupFragment(), this);
					break;
				case NOT_SET:
					FragmentHelper.addFragment(new WelcomeFragment(), this);
					break;
			}
		}
	}

	public void onRechooseRole() {
		FragmentHelper.replaceFragment(new WelcomeFragment(), this);
	}

	public void onUserChooseToBePatient() {
		FragmentHelper.replaceFragment(new RingWearerSetupFragment(), true, this);
	}

	public void onUserChooseToBeWatcher() {
		FragmentHelper.replaceFragment(new WatcherSetupFragment(), true, this);
	}

	public void onShowingPatientListFragment() {
		FragmentHelper.replaceFragment(new SubscribedPatientListFragment(), true, this);
	}

	public void onShowRingWearerCollectInformationFragment() {
		FragmentHelper.replaceFragment(new RingWearerCollectInformationFragment(), this);
	}

	public void onShowRingWearerSetupFragment() {
		FragmentHelper.replaceFragment(new RingWearerSetupFragment(), this);
	}

	public void onShowReconfirmFragment(Constants.PredictedReason predictedReason) {
		FragmentHelper.replaceFragment(ReconfirmFragment.newInstance(predictedReason), true, this);
	}

	public void onReturnReconfirmResult(Boolean reconfirmResult) {
		FragmentHelper.replaceFragment(RingWearerSetupFragment.newInstance(reconfirmResult), this);
	}

	public static boolean isInForeGroundMode() {
		return mIsInForegroundMode;
	}
}
