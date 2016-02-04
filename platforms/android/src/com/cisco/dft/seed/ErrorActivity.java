package com.cisco.dft.seed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.ionicframework.csgtools667618.R;

import com.cisco.dft.oauth.utils.AuthConstants;

public class ErrorActivity extends Activity {

	private TextView mTextViewResultMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_error);
		mTextViewResultMessage = (TextView) findViewById(R.id.id_textview_errormessage);
		showErrorMessage();

	}

	private void showErrorMessage() {
		Intent intent = getIntent();
		String errorMessage = intent.getExtras().getString(
				AuthConstants.KEY_ERROR_MSG);
		int errorCode = intent.getExtras()
				.getInt(AuthConstants.KEY_RESULT_CODE);

		if (errorCode == AuthConstants.RESULT_NO_NETWORK) {
			mTextViewResultMessage.setText(errorMessage+"\n"+"Please Check your internet connection");

		} else if ((errorCode == AuthConstants.RESULT_HOST_NOT_REACHABLE)||(errorCode == AuthConstants.RESULT_TIMED_OUT)) {
			mTextViewResultMessage.setText(errorMessage+"\n"+"Please try again");

		} else {
			mTextViewResultMessage.setText(errorMessage);
		}

	}

}
