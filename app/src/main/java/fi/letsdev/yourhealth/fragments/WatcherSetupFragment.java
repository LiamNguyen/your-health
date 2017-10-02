package fi.letsdev.yourhealth.fragments;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import java.util.List;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.interfaces.InterfacePatientRepository;
import fi.letsdev.yourhealth.interfaces.InterfaceRefresher;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.remote.PatientService;
import fi.letsdev.yourhealth.repository.PatientRepository;
import fi.letsdev.yourhealth.utils.ApiUtils;
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import fi.letsdev.yourhealth.utils.PreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatcherSetupFragment extends Fragment implements InterfaceRefresher, InterfacePatientRepository {

	private static final String TAG = "Watch Fragment";

	private TextView txtMessageWatcherHint;
	private EditText editTextPatientCode;
	private Button btnStartSubscribing;
	private Button btnUnsubscribe;
	private Button btnStopAlert;
	private ProgressBar progressBar;
	private RelativeLayout progressBarLayout;
	private PreferencesManager preferencesManager;
	private NotificationAlertManager notificationAlertManager;
	private PatientRepository patientRepository;
	private ViewGroup mainLayout;

	public WatcherSetupFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OrtcHandler.getInstance().prepareClient(getContext(), WatcherSetupFragment.this);
		OrtcHandler.getInstance().channel = WatcherSetupFragment.this;
		
		preferencesManager = PreferencesManager.getInstance(getContext());
		notificationAlertManager = NotificationAlertManager.getInstance(getContext());
		patientRepository = PatientRepository.getInstance(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_watcher_setup, container, false);
		setHasOptionsMenu(true);

		txtMessageWatcherHint = (TextView)view.findViewById(R.id.txt_messageWatcherHint);
		editTextPatientCode = (EditText)view.findViewById(R.id.editText_patientCode);
		btnStartSubscribing = (Button)view.findViewById(R.id.btn_startSubscribing);
		btnUnsubscribe = (Button)view.findViewById(R.id.btn_unsubscribe);
		btnStopAlert = (Button)view.findViewById(R.id.btn_stopAlert);

		btnStartSubscribing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String channel = editTextPatientCode.getText().toString();
				if (channel.isEmpty()) {
					txtMessageWatcherHint.setTextColor(Color.RED);
					txtMessageWatcherHint.setText(getString(R.string.error_patientCodeEmtpy));
					return;
				}
				btnStartSubscribing.setVisibility(View.INVISIBLE);
				progressBarLayout.setVisibility(View.VISIBLE);

				patientRepository.checkChannelValidity(channel);
			}
		});

		btnUnsubscribe.setVisibility(View.INVISIBLE);
		btnUnsubscribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<String> channels = preferencesManager.loadChannels();

				if (channels.size() > 1) {
					((MainActivity)getActivity()).onShowingPatientListFragment();
				} else if (!channels.isEmpty()) {
					OrtcHandler.getInstance().unsubscribeChannel(channels.get(0));
					preferencesManager.removeChannel(channels.get(0));

					txtMessageWatcherHint.setText(getString(R.string.message_watcher_hint));
					editTextPatientCode.setVisibility(View.VISIBLE);
					btnStartSubscribing.setVisibility(View.VISIBLE);
					btnUnsubscribe.setVisibility(View.INVISIBLE);
				}
			}
		});

		if (!notificationAlertManager.isPlaying())
			btnStopAlert.setVisibility(View.INVISIBLE);
		btnStopAlert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				notificationAlertManager.stopAlert();
				txtMessageWatcherHint.setText(getString(R.string.message_watcher_already_subscribe));
				btnUnsubscribe.setVisibility(View.VISIBLE);
				btnStopAlert.setVisibility(View.INVISIBLE);
			}
		});

		mainLayout = (ViewGroup)view.findViewById(R.id.main_layout);
		addProgressBar(view);

		if (!preferencesManager.loadChannels().isEmpty()) {
			btnStartSubscribing.setVisibility(View.GONE);
			progressBarLayout.setVisibility(View.VISIBLE);
		}

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.option_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_backToWelcome:
				((MainActivity)getContext()).onRechooseRole();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void refreshData(final String message) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (message != null) {
					Log.d(TAG, message);

					if (message.equals(getString(R.string.emergency_notification_message)) ||
						notificationAlertManager.isPlaying()) {
						txtMessageWatcherHint.setText(getString(R.string.emergency_notification_message));
						btnUnsubscribe.setVisibility(View.GONE);
						btnStopAlert.setVisibility(View.VISIBLE);
					} else {
						txtMessageWatcherHint.setText(getString(R.string.message_watcher_already_subscribe));
						btnUnsubscribe.setVisibility(View.VISIBLE);
					}

					editTextPatientCode.setVisibility(View.GONE);
					btnStartSubscribing.setVisibility(View.GONE);

					if (progressBarLayout == null) return;
					progressBarLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	@Override
	public void onChannelValidityResult(final Boolean valid, final Patient patient) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressBarLayout.setVisibility(View.INVISIBLE);

				if (valid) {
					OrtcHandler.getInstance().subscribeChannel(patient.getChannel());
//					preferencesManager.saveChannel(patient.getChannel());
					preferencesManager.savePatient(patient);

					txtMessageWatcherHint.setText(getString(R.string.message_watcher_already_subscribe));
					editTextPatientCode.setVisibility(View.GONE);
					btnStartSubscribing.setVisibility(View.GONE);
					btnUnsubscribe.setVisibility(View.VISIBLE);
					txtMessageWatcherHint.setTextColor(Color.GRAY);
				} else {
					txtMessageWatcherHint.setText(getString(R.string.message_invalid_channel));
					txtMessageWatcherHint.setTextColor(Color.RED);
					btnStartSubscribing.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	//Programmatically add progress bar

	private void addProgressBar(View view) {
		progressBarLayout = new RelativeLayout(getContext());
		progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(View.VISIBLE);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		progressBarLayout.addView(progressBar, params);
		progressBarLayout.setBackgroundColor(Color.LTGRAY);

		RelativeLayout.LayoutParams progressBarLayout_params =
			new RelativeLayout.LayoutParams(300, 300);
		progressBarLayout_params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mainLayout.addView(progressBarLayout, progressBarLayout_params);
		mainLayout.requestLayout();
		mainLayout.bringChildToFront(progressBarLayout);

		progressBarLayout.setVisibility(View.INVISIBLE);
	}
}
