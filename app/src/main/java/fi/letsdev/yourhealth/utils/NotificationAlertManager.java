package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class NotificationAlertManager {
	private static NotificationAlertManager instance = null;

	private final String TAG = "Alert manager";
	private MediaPlayer mMediaPlayer;
	private Uri alert;
	private Context context;

	private NotificationAlertManager(Context context) {
		this.context = context;
		alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	}

	public static NotificationAlertManager getInstance(Context context) {
		if (instance == null) {
			instance = new NotificationAlertManager(context);
		}
		return instance;
	}

	public void startAlert() {
		if (isPlaying()) return;

		if (mMediaPlayer != null) {
			mMediaPlayer.start();
			return;
		}

		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager =
				(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(
				AudioManager.STREAM_RING,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
				0
			);

			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Error when setting datasource");
		}
		mMediaPlayer.start();
	}

	public Boolean isPlaying() {
		return mMediaPlayer != null && mMediaPlayer.isPlaying();
	}

	public void stopAlert() {
		if (mMediaPlayer == null) return;
		mMediaPlayer.stop();
		mMediaPlayer = null;
	}
}
