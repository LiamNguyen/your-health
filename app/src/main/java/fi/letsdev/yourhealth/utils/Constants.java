package fi.letsdev.yourhealth.utils;

public class Constants {

	static class PreferenceKey {
		static String USER_ROLE = "USER_ROLE";
		static String PATIENT = "PATIENT";
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
