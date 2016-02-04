package com.cisco.dft.seed;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.cisco.dft.dbObjects.ToolConfig;
import com.cisco.dft.dbObjects.UserInfo;
import com.cisco.dft.dbhandler.DatabaseHandler;
import com.cisco.dft.notification.RegisterActivity;
import com.cisco.dft.oauth.connections.AuthConnection;
import com.cisco.dft.oauth.passcode.PassCodeUtils;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

import java.util.List;

/**
 * Creates tabs for the application
 *
 * @author tchodey
 *
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements OnTabChangeListener, OnClickListener
{
	private ActionBar mActionBar;
	private TextView mUserProfile;
	private Handler mHandler;
	private Context mCtx;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);

        DatabaseHandler databasehandler = new DatabaseHandler(getApplicationContext());
        databasehandler.getWritableDatabase();

		mUserProfile = (TextView) findViewById(R.id.tvinfo);

		mCtx = getApplicationContext();
		PassCodeUtils.enablePasscode(this, true);

		// Displays the action bar on top of the tab activity
		mActionBar = getActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		setActiobarTitle(AppUtils.KEY_HOME);

		TabHost tabHost = getTabHost();

		// Ionic Tab
		TabSpec ionicspec = tabHost.newTabSpec(AppUtils.KEY_HOME);
		ionicspec.setIndicator(AppUtils.KEY_HOME);
		Intent ionicIntent = new Intent(this, com.ionicframework.csgtools667618.MainActivity.class);
		ionicspec.setContent(ionicIntent);

		// Home Tab
		/*TabSpec homespec = tabHost.newTabSpec(AppUtils.KEY_HOME);
		homespec.setIndicator(AppUtils.KEY_HOME);
		Intent homeIntent = new Intent(this, HomeScreen.class);
		homespec.setContent(homeIntent);*/

		// WebAPI Tab
		/*TabSpec TeamSpec = tabHost.newTabSpec(AppUtils.KEY_TEAM);
		TeamSpec.setIndicator(AppUtils.KEY_TEAM);
		Intent TeamIntent = new Intent(this, TeamListActivity.class);
		TeamSpec.setContent(TeamIntent);*/

		// WebAPP Tab
		/*TabSpec productWebViewSpec = tabHost.newTabSpec(AppUtils.KEY_WEBVIEW);
		productWebViewSpec.setIndicator(AppUtils.KEY_WEBVIEW);
		Intent ProductIntent = new Intent(this, ProductWebView.class);
		productWebViewSpec.setContent(ProductIntent);*/

		// GCM Tab
		TabSpec GCMSpec = tabHost.newTabSpec(AppUtils.KEY_SETTINGS);
		GCMSpec.setIndicator(AppUtils.GCM_SCREEN);
		Intent gcmIntent = new Intent(this, RegisterActivity.class);
		GCMSpec.setContent(gcmIntent);


		// Settings Tab
		TabSpec SettingsSpec = tabHost.newTabSpec(AppUtils.KEY_SETTINGS);
		SettingsSpec.setIndicator(AppUtils.KEY_SETTINGS);
		Intent SettingsIntent = new Intent(this, SettingsActivity.class);
		SettingsSpec.setContent(SettingsIntent);

		// Adding the tabSpecs to TabHost
		tabHost.addTab(ionicspec);
		/*tabHost.addTab(homespec);
		tabHost.addTab(TeamSpec);
		tabHost.addTab(productWebViewSpec);*/
        tabHost.addTab(GCMSpec);
		tabHost.addTab(SettingsSpec);
		tabHost.setOnTabChangedListener(this);

		tabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);

		mHandler = new Handler()
        {
			@Override
			public void handleMessage(Message msg)
            {
				super.handleMessage(msg);
				showTitle();
			}
		};
	}

	/**
	 * If the Grant type is Client credentials, disabling the header which shows
	 * the User Profile of the person logged in
	 */
	@Override
	protected void onResume()
    {
		super.onResume();
		if (AuthConstants.GRANT_TYPE.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL))
        {
			mUserProfile.setVisibility(TextView.GONE);
		}
        else
        {
			setTitle();
		}
	}

	@Override
	protected void onRestart()
    {
		super.onRestart();
		boolean isShowLogin = AppUtils.getChildActivity(mCtx);
		if (AppUtils.isShowLogin(mCtx) && !isShowLogin)
        {
			startlogin();
		}
		AppUtils.setChildActivity(mCtx, false);
	}

	/**
	 * Shows the Login Activity
	 */
	private void startlogin()
    {
		Intent intent = new Intent(MainActivity.this, com.cisco.dft.oauth.activities.LoginActivity.class);
		intent.putExtra(AuthConstants.KEY_NEXT_ACTIVITY, MainActivity.class);
		intent.putExtra(AuthConstants.KEY_APP_TITLE, getResources().getString(R.string.app_name));
		startActivity(intent);
		finish();
	}

	@Override
	protected void onStop()
    {
		boolean isSent = AppUtils.isApplicationSentToBackground(mCtx);
		AppUtils.setBgTime(mCtx, isSent);
		super.onStop();
	}

	/**
	 * Shows the UserProfile of the person logged in
	 */
	private void setTitle()
    {
		if (AppUtils.isTitleavailable(mCtx))
        {
			showTitle();
		}
        else
        {
			loadData();
		}
	}

	/**
	 * If the User is already logged in, the UserProfile is stored in shared
	 * preferences, and shown on the header by fetching the information.
	 *
	 */
	private void showTitle()
    {
		mUserProfile.setText(AppUtils.getTitle(mCtx));
	}

	/**
	 * If the UserProfile is not available in the shared preferences or if the
	 * User is logging in for the first time it gets the information from the
	 * server
	 */
	JSONObject jsonObject = null;

	private void loadData()
    {
		Runnable r = new Runnable()
        {
			@Override
			public void run()
            {
				loadAccesslevelInfo();
				mHandler.sendEmptyMessage(1);
			}
		};
		Thread th = new Thread(r);
		th.start();
	}

	/**
	 * Gets the User Information
	 */
	private void loadAccesslevelInfo()
    {
		AuthConnection conn = new AuthConnection(mCtx);
		jsonObject = conn.getUserInfo();

		try
        {
			String userid = jsonObject.getString("cn");
			String accesslevel = jsonObject.getString("accessLevel");
			String mail = jsonObject.getString("mail");
			String uid = jsonObject.getString("uid");
			AppUtils.storeTitleInfo(mCtx, userid, accesslevel, mail, uid);

            Log.d("CSG Tools", uid);
            DatabaseHandler databasehandler = new DatabaseHandler(getApplicationContext());
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(uid);
            databasehandler.addUserInfo(userInfo);

            List<UserInfo> userInfoRecords = databasehandler.getUserList();
            Log.d("User Record Retrieved :", userInfoRecords.get(0).getUserId());
		}
        catch (JSONException json)
        {
			Log.w(" Error ", json.toString());
		}
	}

	/**
	 * Sets the title and the button on the action bar.
	 *
	 * @param title
	 */
	private void setActiobarTitle(String title)
    {
		mActionBar.setCustomView(R.layout.custom_actionbar);
		View v = mActionBar.getCustomView();
		TextView titleTxtView = (TextView) v.findViewById(R.id.tvactionbar);
		ImageView btn = (ImageView) v.findViewById(R.id.btnref);

		if (title.equalsIgnoreCase(AppUtils.KEY_SETTINGS))
        {
			btn.setVisibility(Button.GONE);
		}
        else if (title.equalsIgnoreCase(AppUtils.KEY_HOME))
        {
			btn.setVisibility(Button.GONE);
		}
        else if (title.equalsIgnoreCase(AppUtils.KEY_WEBVIEW))
        {
			btn.setVisibility(Button.GONE);
		}
        else if (title.equalsIgnoreCase(AppUtils.KEY_TEAM))
        {
			btn.setVisibility(Button.VISIBLE);
			btn.setOnClickListener(this);
		}
		titleTxtView.setText(title);
	}

	@Override
	public void onTabChanged(String tabId)
    {
		setActiobarTitle(tabId);
	}

	/**
	 * Activity called when the button clicked on the action bar
	 */
	@Override
	public void onClick(View arg0)
    {
		getTabHost().setCurrentTab(1);
		TeamListActivity MyActivity = (TeamListActivity) this.getCurrentActivity();
		MyActivity.call();
	}

	public void demo(View v)
    {
	}

	/**
	 * Collects the time-stamp when the activity is back-pressed or goes into
	 * the background
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
