package fi.letsdev.yourhealth.interfaces;

public interface InterfacePatientRepository {
	void onChannelValidityResult(Boolean valid, String channel);
}
