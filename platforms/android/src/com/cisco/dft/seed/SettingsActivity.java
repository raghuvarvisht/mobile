package com.cisco.dft.seed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.cisco.dft.oauth.passcode.ChangePasscode;
import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.oauth.passcode.PassCodeUtils;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.oauth.utils.AuthUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Shows the about page and option to logout from application
 *
 * @author tchodey
 *
 */
public class SettingsActivity extends PassCodeActivity implements
		OnCheckedChangeListener, OnClickListener {
	private RelativeLayout mRelativeLayoutText, mRelativeLayoutPassword,
			mRelativeLayoutSignout, mRelativeLayoutUser;
	private Switch mSwitchlogout,mSwitchpasscode;
	private View mIvLine;
	private Button mRefreshCookie;
	int requestCode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mRelativeLayoutSignout = (RelativeLayout) findViewById(R.id.rlsignout);
		mRelativeLayoutText = (RelativeLayout) findViewById(R.id.container);
		mRelativeLayoutPassword = (RelativeLayout) findViewById(R.id.pwd);
		if(PassCodeUtils.isPassCodeEnabled(getApplicationContext())==false){
			mRelativeLayoutPassword.setVisibility(View.GONE);
		}
		mRelativeLayoutUser = (RelativeLayout) findViewById(R.id.userdatacontainer);
		mRefreshCookie = (Button) findViewById(R.id.refreshcookie);

		mIvLine = findViewById(R.id.ivline);
		mRefreshCookie.setOnClickListener(this);

		mRelativeLayoutUser.setOnClickListener(this);

		// About Activity
		mRelativeLayoutText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent aboutpage = new Intent(SettingsActivity.this,
						AboutActivity.class);
				startActivity(aboutpage);
			}
		});


		mRelativeLayoutPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent changePasscode = new Intent(SettingsActivity.this,
						ChangePasscode.class);
				startActivity(changePasscode);

			}
		});

		// Logout Switch
		mSwitchlogout = (Switch) findViewById(R.id.logoutbtn);
		mSwitchpasscode = (Switch) findViewById(R.id.passcode);

		// Disabling the Logout Switch as there is no Login and Logout
		// functionalities for Client Credentials Grant type
		if (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL)) {
			mRelativeLayoutSignout.setVisibility(RelativeLayout.GONE);
			mIvLine.setVisibility(View.GONE);

		} else {
			mRelativeLayoutSignout.setVisibility(RelativeLayout.VISIBLE);
		}

		mSwitchlogout.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					showLogOutAlert();
				}
			}

		});


	mSwitchpasscode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					showLogOutAlert();
				}
			}

		});
	}

	/**
	 * Shows the Login Activity
	 */
	private void startlogin() {
		Intent intent = new Intent(SettingsActivity.this,
				SplashScreen.class);
		intent.putExtra(AuthConstants.KEY_NEXT_ACTIVITY, MainActivity.class);
		intent.putExtra(AuthConstants.KEY_APP_TITLE,
				getResources().getString(R.string.app_name));
		startActivity(intent);
		finish();

	}

	/**
	 * Shows the Alert Dialog for the user to confirm the logout operation
	 */
	private void showLogOutAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingsActivity.this);
		builder.setMessage("Are you sure want to Logout ?");
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				mSwitchlogout.setChecked(true);
				AuthUtils.doLogOut(getApplicationContext());
				startlogin();
			}

		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				mSwitchlogout.setChecked(false);

			}
		});

		AlertDialog alertlogout = builder.create();
		alertlogout.show();

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean checked) {

		if (checked) {
			mRelativeLayoutPassword.setEnabled(true);
		} else {
			mRelativeLayoutPassword.setEnabled(false);
		}

	}

	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}

	@Override
	public void onClick(View v) {

		if (v == mRelativeLayoutUser) {
			Intent userdatapage = new Intent(SettingsActivity.this,
					UserDataActivity.class);
			startActivity(userdatapage);
		}
	}

}
