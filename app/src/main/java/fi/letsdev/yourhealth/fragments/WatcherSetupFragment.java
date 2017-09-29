package fi.letsdev.yourhealth.fragments;

import android.graphics.Color;
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

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.interfaces.InterfaceRefresher;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class WatcherSetupFragment extends Fragment implements InterfaceRefresher {

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
	
	public WatcherSetupFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OrtcHandler.getInstance().prepareClient(getContext(), WatcherSetupFragment.this);
		OrtcHandler.getInstance().channel = WatcherSetupFragment.this;
		
		preferencesManager = PreferencesManager.getInstance(getContext());
		notificationAlertManager = NotificationAlertManager.getInstance(getContext());
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
				OrtcHandler.getInstance().subscribeChannel(channel);
				preferencesManager.saveChannel(channel);

				txtMessageWatcherHint.setText(getString(R.string.message_watcher_already_subscribe));
				editTextPatientCode.setVisibility(View.GONE);
				btnStartSubscribing.setVisibility(View.GONE);
				btnUnsubscribe.setVisibility(View.VISIBLE);
			}
		});

		btnUnsubscribe.setVisibility(View.INVISIBLE);
		btnUnsubscribe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String channel = preferencesManager.loadChannel();

				if (channel == null) return;
				OrtcHandler.getInstance().unsubscribeChannel(channel);

				txtMessageWatcherHint.setText(getString(R.string.message_watcher_hint));
				editTextPatientCode.setVisibility(View.VISIBLE);
				btnStartSubscribing.setVisibility(View.VISIBLE);
				btnUnsubscribe.setVisibility(View.INVISIBLE);
				preferencesManager.removeChannel();
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

		if (preferencesManager.loadChannel() != null) {
			btnStartSubscribing.setVisibility(View.GONE);
			addProgressBar(view);
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

	private void addProgressBar(View view) {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);;
			return;
		}

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
		((ViewGroup)view.findViewById(R.id.main_layout)).addView(progressBarLayout, progressBarLayout_params);
	}
}
