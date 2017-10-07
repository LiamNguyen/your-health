package fi.letsdev.yourhealth.interfaces;

public interface InterfaceMySignalSensorService {
	void onReceiveHeartRate(Integer bpm);
	void onReceiveStepsPerMinute(Integer stepsPerMinute);
}
