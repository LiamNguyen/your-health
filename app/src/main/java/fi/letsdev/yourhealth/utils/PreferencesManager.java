package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

	private final SharedPreferences settings;

	private static PreferencesManager preferencesManager;

	private PreferencesManager(SharedPreferences sp) {
		settings = sp;
	}

	public static PreferencesManager getInstance(Context context) {
		if (preferencesManager == null) {
			preferencesManager =
				new PreferencesManager(PreferenceManager.getDefaultSharedPreferences(context));
		}
		return preferencesManager;
	}

	public String loadChannel() {
		return settings.getString(Constants.PreferenceKey.CHANNEL, null);
	}

	public void saveChannel(String channel) {
		SharedPreferences.Editor e = settings.edit();
		e.putString(Constants.PreferenceKey.CHANNEL, channel);
		e.apply();
	}

	public void removeChannel() {
		SharedPreferences.Editor e = settings.edit();
		e.remove(Constants.PreferenceKey.CHANNEL);
		e.apply();
	}

	public Constants.UserRole loadUserRole() {
		String userRole = settings.getString(Constants.PreferenceKey.USER_ROLE, null);

		if (userRole == null) return Constants.UserRole.NOT_SET;

		switch (userRole) {
			case "PATIENT":
				return Constants.UserRole.PATIENT;
			case "WATCHER":
				return Constants.UserRole.WATCHER;
			default:
				return Constants.UserRole.NOT_SET;
		}
	}

	public void saveUserRole(Constants.UserRole userRole) {
		SharedPreferences.Editor e = settings.edit();
		e.putString(Constants.PreferenceKey.USER_ROLE, userRole.toString());
		e.apply();
	}
}
