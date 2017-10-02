package fi.letsdev.yourhealth.interfaces;

import fi.letsdev.yourhealth.model.Patient;

public interface InterfacePatientRepository {
	void onChannelValidityResult(Boolean valid, Patient patient);
}
