package fi.letsdev.yourhealth.utils;

import fi.letsdev.yourhealth.remote.PatientService;
import fi.letsdev.yourhealth.remote.RetrofitClient;

public class ApiUtils {

	private static final String BASE_URL = "http://10.112.204.134:5000/api/";

	public static PatientService getPatientService() {
		return RetrofitClient.getClient(BASE_URL).create(PatientService.class);
	}
}
