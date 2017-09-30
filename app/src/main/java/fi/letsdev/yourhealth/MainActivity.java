package fi.letsdev.yourhealth;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fi.letsdev.yourhealth.fragments.RingWearerSetupFragment;
import fi.letsdev.yourhealth.fragments.WatcherSetupFragment;
import fi.letsdev.yourhealth.fragments.WelcomeFragment;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		navigate(savedInstanceState);
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
}
