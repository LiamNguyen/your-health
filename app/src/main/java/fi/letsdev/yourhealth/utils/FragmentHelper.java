package fi.letsdev.yourhealth.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import fi.letsdev.yourhealth.R;

public class FragmentHelper {

	//Overload method
	public static void addFragment(Fragment fragment, FragmentActivity fragmentActivity) {
		addFragment(fragment, R.id.main_frameLayout, fragmentActivity);
	}

	private static void addFragment(Fragment fragment, Integer frame, FragmentActivity fragmentActivity) {
		fragmentActivity.getSupportFragmentManager()
			.beginTransaction()
			.add(frame, fragment)
			.commit();
	}

	// Overload method
	public static void replaceFragment(Fragment fragment, FragmentActivity fragmentActivity) {
		replaceFragment(fragment, R.id.main_frameLayout, false, fragmentActivity);
	}

	public static void replaceFragment(Fragment fragment, Boolean isBackToStack, FragmentActivity fragmentActivity) {
		replaceFragment(fragment, R.id.main_frameLayout, isBackToStack, fragmentActivity);
	}

	private static void replaceFragment(Fragment fragment, Integer frame, Boolean isBackToStack, FragmentActivity fragmentActivity) {
		FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager()
			.beginTransaction()
			.replace(frame, fragment);

		if (isBackToStack) transaction.addToBackStack(null);

		transaction.commit();
	}

}
