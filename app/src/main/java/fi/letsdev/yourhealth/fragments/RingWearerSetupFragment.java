package fi.letsdev.yourhealth.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.View.PlayGifView;
import fi.letsdev.yourhealth.interfaces.InterfaceMySignalSensorService;
import fi.letsdev.yourhealth.interfaces.InterfaceRefresher;
import fi.letsdev.yourhealth.model.Patient;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.receiver.MySignalsDataReceiver;
import fi.letsdev.yourhealth.service.BackgroundService;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class RingWearerSetupFragment extends Fragment implements InterfaceMySignalSensorService, InterfaceRefresher {

	private final static String ARGUMENT_KEY = "ReconfirmResult";
	private static CountDownTimer countdownTimer;

	private MySignalsDataReceiver mySignalsDataReceiver;
	private IntentFilter ifilter;
	private Boolean mBound = false;
	private Intent serviceIntent;
	private PlayGifView pGif;
	private static Integer bpm = 0;
	private Integer stepsPerMinute = 0;
	private static Boolean shouldAlertEmergency = false;
	private RelativeLayout relativeLayoutHeartrate;
	private TextView txtBpm;
	private Boolean hasChangedToRingWearerCollectInformationPage = false;
	private NotificationAlertManager notificationAlertManager;
	private Button btnStopAlert;
	private BackgroundService mService;
	private static boolean handlerShouldNotWork;
	private RelativeLayout relativeLayoutEmulate;

	public RingWearerSetupFragment() {}

	public static RingWearerSetupFragment newInstance(Boolean reconfirmResult) {
		RingWearerSetupFragment fragment = new RingWearerSetupFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARGUMENT_KEY, reconfirmResult);
		fragment.setArguments(args);

		deactivateHandler();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		OrtcHandler.getInstance().prepareClient(getContext(), this);

		mySignalsDataReceiver = new MySignalsDataReceiver();
		ifilter = new IntentFilter();
		ifilter.addAction(Constants.IntentActions.MYSIGNAL_HR_DATA_RECEIVE);

		serviceIntent = new Intent(getContext(), BackgroundService.class);
		if (!mBound) {
			getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
			getActivity().startService(serviceIntent);
		}

		notificationAlertManager = NotificationAlertManager.getInstance(getContext());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.SaveInstanceStateKey.SERVICE_STATE, mBound);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		mBound = savedInstanceState != null &&
			savedInstanceState.getBoolean(Constants.SaveInstanceStateKey.SERVICE_STATE);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (!mBound) {
			getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		}
		getActivity().registerReceiver(mySignalsDataReceiver, ifilter);

		// Get bundle argument
		if (getArguments() != null)
			shouldAlertEmergency = (Boolean) getArguments().getSerializable(ARGUMENT_KEY);
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mBound) {
			getActivity().unbindService(mConnection);
			mBound = false;
		}
		if (countdownTimer != null)
			countdownTimer.cancel();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ring_wearer_setup, container, false);
		setHasOptionsMenu(true);

		pGif = view.findViewById(R.id.viewGif);

//		if (PreferencesManager.getInstance(getContext()).loadRingWearer().isNull() && !hasChangedToRingWearerCollectInformationPage) {
			pGif.setImageResource(R.drawable.scanning);
			view.setBackgroundColor(Color.BLACK);
//		}

		relativeLayoutHeartrate = view.findViewById(R.id.relativeLayout_heartrate);
		relativeLayoutHeartrate.setVisibility(View.INVISIBLE);
		txtBpm = view.findViewById(R.id.txt_bpm);

		relativeLayoutEmulate = view.findViewById(R.id.relativeLayout_emulate);
		relativeLayoutEmulate.setVisibility(View.INVISIBLE);

		btnStopAlert = view.findViewById(R.id.btn_stopAlert);
		btnStopAlert.setVisibility(View.INVISIBLE);
		btnStopAlert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				notificationAlertManager.stopAlert();
				btnStopAlert.setVisibility(View.INVISIBLE);
				txtBpm.setTextColor(Color.GRAY);
			}
		});

		final EditText editTextBpm = view.findViewById(R.id.editText_emulate_bpm);
		editTextBpm.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (editTextBpm.getText().toString().isEmpty())
					editTextBpm.setText("0");
				mService.setEmulateBpm(Integer.parseInt(editTextBpm.getText().toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

		final EditText editTextSpm = view.findViewById(R.id.editText_emulate_spm);
		editTextSpm.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (editTextSpm.getText().toString().isEmpty())
					editTextSpm.setText("0");
				mService.setEmulateSpm(Integer.parseInt(editTextSpm.getText().toString()));
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

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
				Patient ringWearer =
					PreferencesManager
						.getInstance(getContext())
						.loadRingWearer();

				if (!ringWearer.isNull()) {
					OrtcHandler.getInstance().unsubscribeChannel(ringWearer.getChannel());
					PreferencesManager.getInstance(getContext()).removeRingWearer();
				}
				hasChangedToRingWearerCollectInformationPage = false;
				PreferencesManager.getInstance(getContext()).saveUserRole(Constants.UserRole.NOT_SET);

				((MainActivity)getActivity()).onRechooseRole();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			mBound = true;

			BackgroundService.LocalBinder binder =
				(BackgroundService.LocalBinder) iBinder;
			mService = binder.getInstance();
			mService.setListener(RingWearerSetupFragment.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {

		}
	};

	@Override
	public void onReceiveHeartRate(final Integer bpm) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (PreferencesManager.getInstance(getContext()).loadRingWearer().isNull() && !hasChangedToRingWearerCollectInformationPage) {
					((MainActivity) getActivity()).onShowRingWearerCollectInformationFragment();
					hasChangedToRingWearerCollectInformationPage = true;
				} else {
					pGif.setImageResource(R.drawable.heartbeat);
				}

				if (getView() != null)
					getView().setBackgroundColor(Color.WHITE);

				if (relativeLayoutHeartrate.getVisibility() == View.INVISIBLE)
					relativeLayoutHeartrate.setVisibility(View.VISIBLE);
				txtBpm.setText(String.valueOf(bpm));

				if (relativeLayoutEmulate.getVisibility() == View.INVISIBLE)
					relativeLayoutEmulate.setVisibility(View.VISIBLE);

				RingWearerSetupFragment.bpm = bpm;
				handleReceivedData();
			}
		});
	}

	@Override
	public void onReceiveStepsPerMinute(Integer stepsPerMinute) {
		this.stepsPerMinute = stepsPerMinute;
		handleReceivedData();
	}

	public static Boolean shouldAlertEmergency() {
		return shouldAlertEmergency;
	}

	private void handleReceivedData() {
		if (handlerShouldNotWork) return;

		if (PreferencesManager.getInstance(getContext()).loadRingWearer().isNull() && !hasChangedToRingWearerCollectInformationPage) {
			return;
		}

		if (bpm > Constants.BPM_MAX) {
			if (stepsPerMinute > Constants.STEPS_PER_MINUTE_MAX) {
				((MainActivity) getActivity()).onShowReconfirmFragment(Constants.PredictedReason.RUNNING_FAST);
			}
			((MainActivity) getActivity()).onShowReconfirmFragment(Constants.PredictedReason.HEAVY_LIFTING);
		}
	}

	@Override
	public void refreshData(final String message) {
		if (getActivity() == null) return;

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (message != null && message.equals(getString(R.string.emergency_notification_message))) {
					btnStopAlert.setVisibility(View.VISIBLE);
					txtBpm.setTextColor(Color.RED);
				}
			}
		});
	}

	private static void activateHandler() {
		handlerShouldNotWork = false;
	}

	private static void deactivateHandler() {
		handlerShouldNotWork = true;
		countdownTimer = new CountDownTimer(3600000, 1000) {
			public void onTick(long millisUntilFinished) {}

			public void onFinish() {
				activateHandler();
			}
		}.start();
	}
}
