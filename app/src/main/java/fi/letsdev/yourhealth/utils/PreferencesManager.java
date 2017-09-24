package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

	private final SharedPreferences settings;

	private static String CHANNEL = "CHANNEL";

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
		return settings.getString(CHANNEL, null);
	}

	public void saveChannel(String channel) {
		SharedPreferences.Editor e = settings.edit();
		e.putString(CHANNEL, channel);
		e.apply();
	}
}
