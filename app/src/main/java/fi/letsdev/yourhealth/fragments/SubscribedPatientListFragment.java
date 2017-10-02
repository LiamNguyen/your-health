package fi.letsdev.yourhealth.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.adapter.PatientAdapter;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class SubscribedPatientListFragment extends Fragment {

	public SubscribedPatientListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subscribed_patient_list, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ArrayList<Patient> patientsList =
			PreferencesManager.getInstance(getContext()).loadPatients();
		PatientAdapter patientAdapter = new PatientAdapter(getContext(), patientsList);

		ListView listViewPatientList = (ListView) view.findViewById(R.id.listView_subscribedPatients);
		listViewPatientList.setAdapter(patientAdapter);
		listViewPatientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Patient chosenPatient = patientsList.get(position);

				OrtcHandler.getInstance().unsubscribeChannel(chosenPatient.getChannel());
				PreferencesManager.getInstance(getContext()).removeChannel(chosenPatient.getChannel());
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});
	}
}
