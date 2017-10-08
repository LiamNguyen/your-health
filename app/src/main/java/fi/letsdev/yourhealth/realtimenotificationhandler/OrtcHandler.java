package fi.letsdev.yourhealth.realtimenotificationhandler;

import android.content.Context;
import android.util.Log;

import java.util.List;

import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.config.Config;
import fi.letsdev.yourhealth.interfaces.InterfaceRefresher;
import fi.letsdev.yourhealth.utils.PreferencesManager;
import ibt.ortc.api.Ortc;
import ibt.ortc.extensibility.OnConnected;
import ibt.ortc.extensibility.OnDisconnected;
import ibt.ortc.extensibility.OnException;
import ibt.ortc.extensibility.OnMessage;
import ibt.ortc.extensibility.OnReconnected;
import ibt.ortc.extensibility.OnReconnecting;
import ibt.ortc.extensibility.OnSubscribed;
import ibt.ortc.extensibility.OnUnsubscribed;
import ibt.ortc.extensibility.OrtcClient;
import ibt.ortc.extensibility.OrtcFactory;

public class OrtcHandler {

	private static OrtcHandler selfHandler = null;

	private OrtcHandler() {}

	public static OrtcHandler getInstance() {
		if (selfHandler == null) {
			selfHandler = new OrtcHandler();
		}
		return selfHandler;
	}

	private static OrtcClient client = null;
	private InterfaceRefresher rootView;
	public Context context;

	final private static String ORTC_TAG = OrtcHandler.class.getSimpleName();

	public void subscribeChannel(String channel) {
		if (client == null) return;
		client.subscribeWithNotifications(channel, true, new OnMessage() {
			@Override
			public void run(OrtcClient sender, String channel, String message) {
				selfHandler.handleNotification(message, channel);
			}
		});
	}

	public void unsubscribeChannel(String channel) {
		client.unsubscribe(channel);
	}

	public void prepareClient(Context context, InterfaceRefresher rootView) {
		if (selfHandler.context != null) return;

		selfHandler.context = context;
		selfHandler.rootView = rootView;

		Ortc api = new Ortc();
		OrtcFactory factory;

		try {
			factory = api.loadOrtcFactory(Config.ortcFactory);
			client = factory.createClient();
			client.setConnectionMetadata(Config.METADATA);

			if (!Config.CLUSTERURL.isEmpty()) {
				client.setClusterUrl(Config.CLUSTERURL);
			} else {
				client.setUrl(Config.URL);
			}

			client.setApplicationContext(selfHandler.context);
			client.setGoogleProjectId(Config.PROJECT_ID);

			client.onConnected = new OnConnected() {
				@Override
				public void run(OrtcClient sender) {
					List<String> channels = PreferencesManager
						.getInstance(selfHandler.context)
						.loadChannels();

					if (!channels.isEmpty()) {
						for(String channel: channels) {
							subscribeChannel(channel);
						}
						selfHandler.rootView.refreshData("Connected to push notification server");
					} else {
						Log.d(ORTC_TAG, "Channel has not been set to stored preference");
						selfHandler.rootView.refreshData(null);
					}
				}
			};

			client.onDisconnected = new OnDisconnected() {
				@Override
				public void run(OrtcClient sender) {
					Log.i(ORTC_TAG, "Disconnected");
				}
			};

			client.onSubscribed = new OnSubscribed() {
				@Override
				public void run(OrtcClient sender, String channel) {
					Log.i(ORTC_TAG, "Subscribed to " + channel);
				}
			};

			client.onUnsubscribed = new OnUnsubscribed(){
				@Override
				public void run(OrtcClient sender, String channel) {
					Log.i(ORTC_TAG, "Unsubscribed from " + channel);
				}
			};

			client.onException = new OnException(){
				@Override
				public void run(OrtcClient sender, Exception exc) {
					Log.e(ORTC_TAG, "Exception " + exc.toString());
				}
			};

			client.onReconnected = new OnReconnected(){
				@Override
				public void run(OrtcClient sender) {
					Log.i(ORTC_TAG, "Reconnected");
				}
			};

			client.onReconnecting = new OnReconnecting(){
				@Override
				public void run(OrtcClient sender) {
					Log.i(ORTC_TAG, "Reconnecting");
				}
			};

			client.connect(Config.APPKEY, Config.TOKEN);
		} catch (Exception e) {
			Log.e(ORTC_TAG, e.toString());
		}
	}

	public void sendNotification(String channel) {
		OrtcHandler.client.send(
			channel,
			selfHandler.context.getString(
				R.string.emergency_notification_message
			)
		);
	}

	private void handleNotification(String message, String channel) {
		Log.d(ORTC_TAG, String.format(
			"Received notification: %s from channel %s",
			message,
			channel
		));

		selfHandler.rootView.refreshData(message);
	}
}
