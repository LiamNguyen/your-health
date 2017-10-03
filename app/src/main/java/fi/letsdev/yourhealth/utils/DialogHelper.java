package fi.letsdev.yourhealth.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import fi.letsdev.yourhealth.R;

public class DialogHelper {

	private static AlertDialog connectivityStatusDialog;

	public static void createConnectivityStatusDialog(final Context context) {
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(context.getString(R.string.message_network_connectivity_lost));
		alertDialogBuilder.setPositiveButton(context.getString(R.string.btn_tryAgain),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {

				}
			});

		connectivityStatusDialog = alertDialogBuilder.create();
		connectivityStatusDialog.setCanceledOnTouchOutside(false);
		connectivityStatusDialog.show();
		connectivityStatusDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				connectivityStatusDialog.dismiss();
				if (!NetworkConnectivityHelper.isOnline(context)) {
					connectivityStatusDialog.show();
				}
			}
		});
	}
}
