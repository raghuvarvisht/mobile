package com.cisco.dft.seed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Shows the name and version of the application and also navigates to the
 * privacy content and the usage terms of Cisco
 **/

public class AboutActivity extends Activity implements OnClickListener {
	private Button mButtonPrivacy, mButtonTerms;
	private TextView mAppTitle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_about);
		mAppTitle = (TextView) findViewById(R.id.titletext);

		// Sets Cisco Sans Font to the Application title
		Typeface face = AppUtils.getCiscoFont(getApplicationContext());
		if (face != null) {
			mAppTitle.setTypeface(face);
		}

		// Opens Cisco Online Privacy Statement Highlights

		mButtonPrivacy = (Button) findViewById(R.id.buttonprivacy);
		mButtonPrivacy.setOnClickListener(this);

		// Opens Cisco Terms and Conditions

		mButtonTerms = (Button) findViewById(R.id.buttonterms);
		mButtonTerms.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		if (v == mButtonPrivacy) {
			Intent i = new Intent(AboutActivity.this, PrivacyContent.class);
			startActivity(i);
		} else if (v == mButtonTerms) {
			Intent i = new Intent(AboutActivity.this, UsageTerms.class);
			startActivity(i);
		}
	}

	@Override
	public void onBackPressed() {
		AppUtils.setChildActivity(getApplicationContext(), true);
		super.onBackPressed();
	}
}
