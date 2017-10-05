package fi.letsdev.yourhealth.utils;

public class Constants {

	static class PreferenceKey {
		static String USER_ROLE = "USER_ROLE";
		static String PATIENT = "PATIENT";
	}

	public static class IntentActions {
		public static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
		public static String MYSIGNAL_HR_DATA_RECEIVE = "fi.letsdev.android.mysignals.RECEIVE";
	}

	public static class IntentExtras {
		public static String BPM = "bpm";
	}

	public enum UserRole {
		PATIENT("PATIENT"),
		WATCHER("WATCHER"),
		NOT_SET("NOT_SET");

		private String role;

		UserRole(String role) {
			this.role = role;
		}

		public String toString() {
			return this.role;
		}
	}
}
