package fi.letsdev.yourhealth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.interfaces.InterfacePatientRepository;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.repository.PatientRepository;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;
import fi.letsdev.yourhealth.utils.ViewHelper;

public class RingWearerCollectInformationFragment extends Fragment implements InterfacePatientRepository {

	private RelativeLayout progressBarLayout;
	private ViewGroup mainLayout;
	private EditText editTextName;
	private Button btnSubmitNewPatient;
	private PatientRepository patientRepository;

	public RingWearerCollectInformationFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		patientRepository = PatientRepository.getInstance(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ring_wearer_collect_information, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mainLayout = view.findViewById(R.id.main_layout);
		progressBarLayout = new RelativeLayout(getContext());
		ViewHelper.addProgressBar(progressBarLayout, mainLayout, getContext());

		editTextName = view.findViewById(R.id.editText_patientName);
		editTextName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (editTextName.getText().toString().isEmpty()) {
					btnSubmitNewPatient.setEnabled(false);
				} else {
					btnSubmitNewPatient.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

		btnSubmitNewPatient = view.findViewById(R.id.btn_submitNewPatient);
		btnSubmitNewPatient.setEnabled(false);
		btnSubmitNewPatient.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressBarLayout.setVisibility(View.VISIBLE);
				patientRepository.addPatient(
					new Patient(
						editTextName.getText().toString().trim(),
						Constants.MYSIGNALS_ID.replace(" ", "") // Hardcoded because I don't have the real ring
					)
				);
			}
		});
	}

	@Override
	public void onAddPatient(Patient patient) {
		progressBarLayout.setVisibility(View.INVISIBLE);

		if (patient.isNull()) {
			// Failed for some reason on server side

			Toast.makeText(
				getContext(),
				getActivity().getString(R.string.message_server_error),
				Toast.LENGTH_LONG
			).show();
		} else {
			OrtcHandler.getInstance().subscribeChannel(patient.getChannel());
			PreferencesManager.getInstance(getContext()).saveRingWearer(patient);
			if (getActivity() != null)
				((MainActivity) getActivity()).onShowRingWearerSetupFragment();
		}
	}

	@Override
	public void onChannelValidityResult(Boolean valid, Patient patient) {}
}
