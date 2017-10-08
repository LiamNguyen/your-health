package fi.letsdev.yourhealth.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ViewHelper {

	//Programmatically add progress bar

	public static void addProgressBar(RelativeLayout progressBarLayout, ViewGroup mainLayout, Context context) {
		ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		progressBar.setIndeterminate(true);
		progressBar.setVisibility(View.VISIBLE);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 200);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		progressBarLayout.addView(progressBar, params);
		progressBarLayout.setBackgroundColor(Color.LTGRAY);

		RelativeLayout.LayoutParams progressBarLayout_params =
			new RelativeLayout.LayoutParams(300, 300);
		progressBarLayout_params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mainLayout.addView(progressBarLayout, progressBarLayout_params);
		mainLayout.requestLayout();
		mainLayout.bringChildToFront(progressBarLayout);

		progressBarLayout.setVisibility(View.INVISIBLE);
	}
}
