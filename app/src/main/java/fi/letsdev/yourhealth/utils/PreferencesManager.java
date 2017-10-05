package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import fi.letsdev.yourhealth.model.Patient;

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

	public void savePatient(Patient patient) {
		SharedPreferences.Editor e = settings.edit();
		ArrayList<Patient> patients = loadPatients();

		patients.add(patient);

		String patientsString = Helper.formStringFromPatientList(patients);

		e.putString(Constants.PreferenceKey.PATIENT, patientsString);
		e.apply();
	}

	private void savePatients(ArrayList<Patient> patients) {
		SharedPreferences.Editor e = settings.edit();
		ArrayList<Patient> storedPatients = loadPatients();

		for (Patient patient: patients) {
			storedPatients.add(patient);
		}

		String patientsString = Helper.formStringFromPatientList(patients);

		e.putString(Constants.PreferenceKey.PATIENT, patientsString);
		e.apply();
	}

	public ArrayList<Patient> loadPatients() {
		ArrayList<Patient> patients = new ArrayList<>();
		String patientsString = settings.getString(Constants.PreferenceKey.PATIENT, null);

		if (patientsString == null || patientsString.isEmpty())
			return new ArrayList<>();

		String[] patientStringArray = patientsString.split(",");

		for (String patient: patientStringArray) {
			String[] parts = patient.split(":");
			patients.add(new Patient(
				parts[0],
				parts[1]
			));
		}
		return patients;
	}

	public List<String> loadChannels() {
		List<String> channels = new ArrayList<>();
		String patientsString = settings.getString(Constants.PreferenceKey.PATIENT, null);

		if (patientsString == null || patientsString.isEmpty())
			return new ArrayList<>();

		String[] patientStringArray = patientsString.split(",");

		for (String patient: patientStringArray) {
			String[] parts = patient.split(":");
			channels.add(parts[1]);
		}
		return channels;
	}

	public void removeChannel(String channel) {
		ArrayList<Patient> patients = loadPatients();

		for (int i = 0; i < patients.size(); i++) {
			if (patients.get(i).getChannel().equals(channel)) {
				patients.remove(i);
				break;
			}
		}

		savePatients(patients);
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
