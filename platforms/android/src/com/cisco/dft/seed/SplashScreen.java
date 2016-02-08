package com.cisco.dft.seed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cisco.dft.dbhandler.DatabaseHandler;
import com.cisco.dft.oauth.database.DataHelper;
import com.cisco.dft.oauth.entities.EntityAuth;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.oauth.utils.AuthUtils;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Splash Screen of the application which takes to the login page if the user is
 * not logged-in or to the home page if the user is already logged in.
 **/

public class SplashScreen extends Activity
{
	private Context mCtx;
	/**
	 * Checks if the user is logged in for different authentication and grant
	 * types and navigates to the appropriate screen
	 *
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_layout);
		mCtx = getApplicationContext();
		AuthUtils.initializelogintype(mCtx);
		doAuthentication();
	}

	private void doAuthentication()
    {
		if (AuthConstants.AUTHENTICATION_TYPE.equalsIgnoreCase(AuthConstants.KEY_TYPE_OAM))
        {
			validateLogin();
		}
        else if (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_AUTHORIZATION_CODE) || (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_PASSWORD)))
        {
			validateLogin();
		}
        else if (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL))
        {
			validateToken();
		}
        else if (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_IMPLICIT))
        {
			if (AppUtils.isShowLogin(mCtx))
            {
				startlogin();
			}
            else
            {
				callHome();
			}
		}
	}

	private void validateLogin()
	{
		if (AuthUtils.isSignedIn(mCtx))
		{
			callHome();
		}
		else
		{
			setContentView(R.layout.splash_screen_layout);
			startlogin();
		}
	}

	/**
	 * Checks the login status for client credentials
	 */
	private void validateToken()
    {
		DataHelper db = new DataHelper(mCtx);
		EntityAuth entity = db.getToken();
		boolean isInValid = AuthUtils.isInValidToken(entity);
		if (isInValid)
        {
			startlogin();
		}
        else
        {
			callHome();
		}
	}

	/**
	 * Shows the Home Screen
	 */
	private void callHome()
    {
		Intent intent = new Intent(SplashScreen.this, MainActivity.class);

		/*Intent intn= getIntent();
		Bundle b = intn.getExtras();

		if(b != null)
		{
            String toolId = b.getString("toolId");
            String info = b.getString("info");
            String action = b.getString("action");
            String topic = b.getString("topic");
            String webpage = b.getString("webpage");
            String redirectClientScreenURL = "toolInfo";
            String notificationMsg = b.getString("notificationMessage");
        }*/

        startActivity(intent);
        finish();
	}

	/**
	 * Shows the login activity
	 */
  private void startlogin()
  {
		Intent intent = new Intent(SplashScreen.this, com.cisco.dft.oauth.activities.LoginActivity.class);
		intent.putExtra(AuthConstants.KEY_NEXT_ACTIVITY, MainActivity.class);
		intent.putExtra(AuthConstants.KEY_ERROR_ACTIVITY, ErrorActivity.class);
		intent.putExtra(AuthConstants.KEY_APP_TITLE, getResources().getString(R.string.app_name));
		startActivity(intent);
		finish();
	}
}
