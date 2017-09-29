package fi.letsdev.yourhealth.utils;

public class Constants {

	public static class PreferenceKey {
		public static String CHANNEL = "CHANNEL";
		public static String USER_ROLE = "USER_ROLE";
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
