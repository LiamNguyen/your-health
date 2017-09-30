package fi.letsdev.yourhealth.utils;

import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.remote.PatientService;
import fi.letsdev.yourhealth.remote.RetrofitClient;

public class ApiUtils {

	public static final String BASE_URL = "http://10.0.2.2:5000/api/";

	public static PatientService getPatientService() {
		return RetrofitClient.getClient(BASE_URL).create(PatientService.class);
	}
}
