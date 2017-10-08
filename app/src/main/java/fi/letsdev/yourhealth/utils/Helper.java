package fi.letsdev.yourhealth.utils;

import java.util.List;

import fi.letsdev.yourhealth.model.Patient;

class Helper {
	static String formStringFromPatientList(List<Patient> patients) {
		StringBuilder stringFromHashMap = new StringBuilder();

		for (Patient patient: patients) {
			stringFromHashMap
				.append(patient.getName())
				.append(":")
				.append(patient.getChannel());
		}

		return stringFromHashMap.toString();
	}

	static String formStringFromPatient(Patient patient) {
		return patient.getName() + ":" + patient.getChannel();
	}
}
