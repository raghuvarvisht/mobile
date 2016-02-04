package com.cisco.dft.seed;

import android.os.Bundle;

import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Shows the welcome message and information of the application
 *
 * @author tchodey
 *
 */

public class HomeScreen extends PassCodeActivity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_tab_layout);
	}

	/**
	 * Collects the time-stamp when the application goes to background
	 */
	@Override
	public void onBackPressed()
	{
		if (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_IMPLICIT))
		{
			AppUtils.setBgTime(getApplicationContext(), true);
		}
		super.onBackPressed();
	}
}
