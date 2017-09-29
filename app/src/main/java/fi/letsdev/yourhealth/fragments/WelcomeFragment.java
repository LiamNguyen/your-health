package fi.letsdev.yourhealth.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class WelcomeFragment extends Fragment {
	public WelcomeFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_welcome, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		getView().findViewById(R.id.btn_patient).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PreferencesManager.getInstance(getContext()).saveUserRole(Constants.UserRole.PATIENT);
				((MainActivity)getActivity()).onUserChooseToBePatient();
			}
		});

		getView().findViewById(R.id.btn_familyMember).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PreferencesManager.getInstance(getContext()).saveUserRole(Constants.UserRole.WATCHER);
				((MainActivity)getActivity()).onUserChooseToBeWatcher();
			}
		});
	}
}
