package fi.letsdev.yourhealth.utils;

import fi.letsdev.yourhealth.remote.PatientService;
import fi.letsdev.yourhealth.remote.RetrofitClient;

public class ApiUtils {

	private static final String BASE_URL = Constants.API_URL;

	public static PatientService getPatientService() {
		return RetrofitClient.getClient(BASE_URL).create(PatientService.class);
	}
}
