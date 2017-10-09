package fi.letsdev.yourhealth.repository;

import android.util.Log;

import fi.letsdev.yourhealth.interfaces.InterfacePatientRepository;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.remote.PatientService;
import fi.letsdev.yourhealth.utils.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientRepository {

	private static PatientRepository instance = null;

	private PatientRepository() {
		mService = ApiUtils.getPatientService();
	}

	public static PatientRepository getInstance() {
		if (instance == null) {
			instance = new PatientRepository();
		}
		return instance;
	}

	private static final String TAG = PatientRepository.class.getSimpleName();

	private PatientService mService;
	private InterfacePatientRepository listener;

	public void checkChannelValidity(final String channel) {
		mService.getPatientByChannel(channel).enqueue(new Callback<Patient>() {
			@Override
			public void onResponse(Call<Patient> call, Response<Patient> response) {
				listener.onChannelValidityResult(
					response.isSuccessful() && !response.body().isNull(),
					response.body()
				);
			}

			@Override
			public void onFailure(Call<Patient> call, Throwable t) {
				Log.e(TAG, "Load patient failure", t);
				listener.onChannelValidityResult(false, new Patient());
			}
		});
	}

	public void addPatient(Patient patient) {
		mService.addPatient(patient).enqueue(new Callback<Patient>() {
			@Override
			public void onResponse(Call<Patient> call, Response<Patient> response) {
				listener.onAddPatient(response.body());
			}

			@Override
			public void onFailure(Call<Patient> call, Throwable t) {

			}
		});
	}

	public void setListener(InterfacePatientRepository listener) {
		this.listener = listener;
	}
}
