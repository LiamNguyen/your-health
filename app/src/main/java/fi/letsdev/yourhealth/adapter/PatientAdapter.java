package fi.letsdev.yourhealth.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.model.Patient;

public class PatientAdapter extends ArrayAdapter<Patient> {

	private static class ViewHolder {
		TextView name;
		TextView channel;
	}

	public PatientAdapter(Context context, ArrayList<Patient> patients) {
		super(context, R.layout.item_patient, patients);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Patient president = getItem(position);
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();

			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.item_patient, parent, false);

			viewHolder.name = convertView.findViewById(R.id.txt_patientName);
			viewHolder.channel = convertView.findViewById(R.id.txt_channel);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		assert president != null;
		viewHolder.name.setText(president.getName());
		viewHolder.channel.setText(String.valueOf(president.getChannel()));

		return convertView;
	}
}
