package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;

import fi.letsdev.yourhealth.interfaces.InterfaceStepsPerMinuteListener;

public class StepsCounterManager implements SensorEventListener {

	private static StepsCounterManager instance;

	public static StepsCounterManager getInstance(Context context, InterfaceStepsPerMinuteListener listener) {
		if (instance == null) {
			instance = new StepsCounterManager(context, listener);
		}
		return instance;
	}

	private SensorManager ssManager;
	private Sensor stepsCounterSensor;
	private InterfaceStepsPerMinuteListener listener;
	private Integer startSteps = 0;
	private Integer numberOfSteps = 0;
	private long startTime = 0;

	private StepsCounterManager(Context context, InterfaceStepsPerMinuteListener listener) {
		ssManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		stepsCounterSensor = ssManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		this.listener = listener;
	}

	public void startMeasuringStepsPerMinute() {
		ssManager.registerListener(this, stepsCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Integer currentSteps = Math.round(sensorEvent.values[0]);
		long currentTime = new Date().getTime() / 1000;
		long measuringPeriod = currentTime - startTime;

		if (startTime == 0) {
			startTime = currentTime;
			startSteps = currentSteps;
		} else if (measuringPeriod > 5) {
			numberOfSteps = currentSteps - startSteps;
			startTime = 0;
			startSteps = currentSteps;
		}

		listener.onReceiveStepsPerMinute((numberOfSteps * 60 / Math.round(measuringPeriod)));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}
}
