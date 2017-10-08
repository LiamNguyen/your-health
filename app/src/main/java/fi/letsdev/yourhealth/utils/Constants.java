package fi.letsdev.yourhealth.utils;

public class Constants {

	static class PreferenceKey {
		static String USER_ROLE = "USER_ROLE";
		static String PATIENT = "PATIENT";
		static String RING_WEARER = "RING_WEARER";
	}

	public static class IntentActions {
		public static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
		public static String MYSIGNAL_HR_DATA_RECEIVE = "fi.letsdev.android.mysignals.RECEIVE";
	}

	public static class IntentExtras {
		public static String BPM = "bpm";
		public static String STEPS_PER_MINUTE = "StepPerMinute";
	}

	public static String MYSIGNALS_ID = "mysignals 000065";

//	static String API_URL = "http://10.112.204.134:5000/api/";

		static String API_URL = "http://192.168.20.106:5000/api/";

	static String NOTIFICATION_CHANNEL_NAME = "Main channel";

	public static class SaveInstanceStateKey {
		public static String SERVICE_STATE = "Bluetooth service state";
	}

	public static Integer BPM_MAX = 150;

	public static Integer BPM_MIN = 50;

	public static Integer STEPS_PER_MINUTE_MAX = 250;

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
