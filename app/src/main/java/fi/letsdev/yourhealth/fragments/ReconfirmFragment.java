package fi.letsdev.yourhealth.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fi.letsdev.yourhealth.MainActivity;
import fi.letsdev.yourhealth.R;
import fi.letsdev.yourhealth.View.PlayGifView;
import fi.letsdev.yourhealth.utils.Constants;

public class ReconfirmFragment extends Fragment {

	private final static String ARGUMENT_KEY = "PredictedReason";

	private Constants.PredictedReason predictedReason;
	private CountDownTimer countDownTimer;

	public ReconfirmFragment() {}

	public static ReconfirmFragment newInstance(Constants.PredictedReason predictedReason) {
		ReconfirmFragment fragment = new ReconfirmFragment();
		Bundle args = new Bundle();
		args.putSerializable(ARGUMENT_KEY, predictedReason);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null)
			predictedReason = (Constants.PredictedReason) getArguments().getSerializable(ARGUMENT_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reconfirm, container, false);

		PlayGifView pGif = view.findViewById(R.id.viewGif);
		TextView txtQuestion = view.findViewById(R.id.txt_question);

		int gif = (predictedReason == Constants.PredictedReason.RUNNING_FAST)
			? R.drawable.naruto
			: R.drawable.gym;

		String question = (predictedReason == Constants.PredictedReason.RUNNING_FAST)
			? getActivity().getString(R.string.message_confirm_running)
			: getActivity().getString(R.string.message_confirm_exercising);

		pGif.setImageResource(gif);
		txtQuestion.setText(question);

		view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				countDownTimer.cancel();
				((MainActivity) getActivity()).onReturnReconfirmResult(false);
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final TextView txtCountDown = view.findViewById(R.id.txt_countdown);

		countDownTimer = new CountDownTimer(10000, 1000) {

			public void onTick(long millisUntilFinished) {
				txtCountDown.setText(String.valueOf(millisUntilFinished / 1000));
			}

			public void onFinish() {
				((MainActivity) getActivity()).onReturnReconfirmResult(true);
			}
		}.start();
	}
}
