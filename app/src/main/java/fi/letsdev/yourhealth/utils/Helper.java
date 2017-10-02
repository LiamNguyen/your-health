package fi.letsdev.yourhealth.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import fi.letsdev.yourhealth.model.Patient;

public class Helper {
	public static String formStringFromPatientList(List<Patient> patients) {
		StringBuilder stringFromHashMap = new StringBuilder();

		for (Patient patient: patients) {
			stringFromHashMap
				.append(patient.getName())
				.append(":")
				.append(patient.getChannel());
		}

		return stringFromHashMap.toString();
	}
}
