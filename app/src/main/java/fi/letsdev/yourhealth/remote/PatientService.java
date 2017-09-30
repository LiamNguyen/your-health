package fi.letsdev.yourhealth.remote;

import fi.letsdev.yourhealth.model.Patient;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PatientService {

	@GET("patient/{channel}")
	Call<Patient> getPatientByChannel(@Path("channel") String channel);

	@POST("patient")
	Call<Patient> addPatient(@Body Patient patient);
}
