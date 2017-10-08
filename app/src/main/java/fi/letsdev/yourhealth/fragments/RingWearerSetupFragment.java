package fi.letsdev.yourhealth.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.View.PlayGifView;
import fi.letsdev.yourhealth.interfaces.InterfaceMySignalSensorService;
import fi.letsdev.yourhealth.receiver.MySignalsDataReceiver;
import fi.letsdev.yourhealth.service.BackgroundService;
import fi.letsdev.yourhealth.utils.Constants;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class RingWearerSetupFragment extends Fragment implements InterfaceMySignalSensorService {

	private MySignalsDataReceiver mySignalsDataReceiver;
	private IntentFilter ifilter;
	private Boolean mBound = false;
	private Intent serviceIntent;
	private PlayGifView pGif;
	private Integer bpm;
	private Integer stepsPerMinute;
	private static Boolean shouldAlertEmergency = false;

	public RingWearerSetupFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mySignalsDataReceiver = new MySignalsDataReceiver();
		ifilter = new IntentFilter();
		ifilter.addAction(Constants.IntentActions.MYSIGNAL_HR_DATA_RECEIVE);

		serviceIntent = new Intent(getContext(), BackgroundService.class);
		if (!mBound) {
			getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
			getActivity().startService(serviceIntent);
		}
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
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mBound) {
			getActivity().unbindService(mConnection);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ring_wearer_setup, container, false);
		setHasOptionsMenu(true);

		pGif = view.findViewById(R.id.viewGif);
		pGif.setImageResource(R.drawable.scanning);
		view.setBackgroundColor(Color.BLACK);

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
			BackgroundService mService = binder.getInstance();
			mService.setListener(RingWearerSetupFragment.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {

		}
	};

	@Override
	public void onReceiveHeartRate(Integer bpm) {
		if (PreferencesManager.getInstance(getContext()).loadRingWearer().isNull())
			((MainActivity) getActivity()).onShowRingWearerCollectInformationFragment();

		pGif.setVisibility(View.INVISIBLE);
		this.bpm = bpm;
		handleReceivedData();
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
		if (bpm > Constants.BPM_MAX) {
			if (stepsPerMinute > Constants.STEPS_PER_MINUTE_MAX) {
				// TODO: Transition to new fragment with question if user is running
			}
			// TODO: Transition to new fragment with question if user is doing heavy lifting
		}
	}
}
