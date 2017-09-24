package fi.letsdev.yourhealth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import fi.letsdev.yourhealth.interfaces.InterfaceRefresher;
import fi.letsdev.yourhealth.realtimenotificationhandler.OrtcHandler;
import fi.letsdev.yourhealth.utils.NotificationAlertManager;
import fi.letsdev.yourhealth.utils.PreferencesManager;

public class MainActivity extends Activity implements InterfaceRefresher {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String channel = PreferencesManager.getInstance(getApplicationContext()).loadChannel();

		OrtcHandler.getInstance().prepareClient(getApplicationContext(), this);
		OrtcHandler.getInstance().channel = this;

		if (channel == null) {
			PreferencesManager.getInstance(getApplicationContext()).saveChannel("TestChannel");
		}
		OrtcHandler.getInstance().subscribeChannel(
			PreferencesManager.getInstance(getApplicationContext()).loadChannel()
		);

		findViewById(R.id.btn_alert).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String channel = PreferencesManager.getInstance(getApplicationContext()).loadChannel();
				OrtcHandler.getInstance().sendNotification(channel);
			}
		});

		findViewById(R.id.btn_stopAlert).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NotificationAlertManager.getInstance(getApplicationContext()).stopAlert();
			}
		});
	}

	@Override
	public void refreshData(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String displayText = message == null
					? "ORTC is connected"
					: message;

				TextView connectionStatus = (TextView) findViewById(R.id.txt_connectionStatus);
				connectionStatus.setText(displayText);
			}
		});
	}
}
