package com.cisco.dft.seed;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cisco.dft.dbObjects.UserInfo;
import com.cisco.dft.dbhandler.DatabaseHandler;
import com.cisco.dft.oauth.database.DataHelper;
import com.cisco.dft.oauth.entities.EntityAuth;
import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Displays the data stored in the database
 *
 * @author tchodey
 *
 */
public class UserDataActivity extends PassCodeActivity {
	private RelativeLayout mRefreshTokenLayout, mUserFirstNameLayout,
			mUserLastNameLayout, mUserUidLayout, mUserMailLayout,
			mUserAccessLevelLayout, mCookieLayout, mAccessTokenLayout;
	private View mUserLastnameView, mUserIdView, mUserMailIdView,
			mUserAccessLevelView, mSeperatorView, mRefreshTokenView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userdata);
		mRefreshTokenLayout = (RelativeLayout) findViewById(R.id.refreshtoken);
		mUserFirstNameLayout = (RelativeLayout) findViewById(R.id.firstnamecontainer);
		mUserLastNameLayout = (RelativeLayout) findViewById(R.id.userdatacontainer);
		mUserUidLayout = (RelativeLayout) findViewById(R.id.userid);
		mUserMailLayout = (RelativeLayout) findViewById(R.id.usermailid);
		mUserAccessLevelLayout = (RelativeLayout) findViewById(R.id.useraccesslevel);
		mCookieLayout = (RelativeLayout) findViewById(R.id.cookie);
		mUserLastnameView = findViewById(R.id.userlastname);
		mUserIdView = findViewById(R.id.useridview);
		mUserMailIdView = findViewById(R.id.usermailview);
		mUserAccessLevelView = findViewById(R.id.useraccesslevelview);
		mSeperatorView = findViewById(R.id.seperatorview);
		mRefreshTokenView = findViewById(R.id.refreshtokenview);
		mAccessTokenLayout = (RelativeLayout) findViewById(R.id.accesstoken);

		showKeys();
	}

	/**
	 * Shows the access token, refresh token , ObSSOCookie and user information
	 * depending on the authentication and grant type
	 */
	public void showKeys() {

		DataHelper hel = new DataHelper(getApplicationContext());
		EntityAuth auth = hel.getToken();

		HashMap<String, String> usermap = AppUtils.getTitleInfo(getApplicationContext());

		String names[] = usermap.get(AppUtils.KEY_NAME).split(" ");

		String fname = "";
		String lname = "";
		String mail = usermap.get(AppUtils.KEY_MAIL);

		String uid = usermap.get(AppUtils.KEY_UID);
		String accesslevel = usermap.get(AppUtils.KEY_ACC_LEVEL);
		TextView firstnametextview = (TextView) findViewById(R.id.textView1);
		TextView lastnametextview = (TextView) findViewById(R.id.userdata);
		TextView userid = (TextView) findViewById(R.id.userdataid);
		TextView usermailid = (TextView) findViewById(R.id.usermailidtextview);
		TextView useraccesslevel = (TextView) findViewById(R.id.useraccessleveltextview);
		TextView accesstoken = (TextView) findViewById(R.id.accesstokentextview);
		TextView refreshtoken = (TextView) findViewById(R.id.refreshtokentextview);

		if (names != null && names.length != 0) {

			fname = names[0];
			if (names.length >= 2) {
				lname = names[1];

			}

		}

		String acctoken = auth.accessToken;
		String reftoken = auth.refreshToken;
		String ObSSOCookie = auth.cookie;

		if (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL)) {
			mUserFirstNameLayout.setVisibility(RelativeLayout.GONE);
			mUserLastNameLayout.setVisibility(RelativeLayout.GONE);
			mUserUidLayout.setVisibility(RelativeLayout.GONE);
			mUserMailLayout.setVisibility(RelativeLayout.GONE);
			mUserAccessLevelLayout.setVisibility(RelativeLayout.GONE);
			mCookieLayout.setVisibility(RelativeLayout.GONE);
			mUserLastnameView.setVisibility(View.GONE);
			mUserIdView.setVisibility(View.GONE);
			mUserMailIdView.setVisibility(View.GONE);
			mUserAccessLevelView.setVisibility(View.GONE);
			mSeperatorView.setVisibility(View.GONE);

		} else {
			firstnametextview.setText("First Name :" + " " + fname);
			lastnametextview.setText("Last Name :" + " " + lname);
			userid.setText("User ID :" + " " + uid);
			usermailid.setText("Mail Address :" + " " + mail);
			useraccesslevel.setText("Access Level :" + " " + accesslevel);
		}
		if (AuthConstants.AUTHENTICATION_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_TYPE_OAM)) {
			mAccessTokenLayout.setVisibility(View.GONE);

		} else {
			accesstoken.setText("Access Token: " + acctoken.substring(0, 10)
					+ "...");
		}

		if (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_AUTHORIZATION_CODE)
				|| (AuthConstants.GRANT_TYPE
						.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_PASSWORD))) {
			if (refreshtoken.length() > 0) {
				refreshtoken.setText("Refresh Token: "
						+ reftoken.substring(0, 10) + "...");
			}
		} else {
			mRefreshTokenLayout.setVisibility(RelativeLayout.GONE);
			mRefreshTokenView.setVisibility(View.GONE);
		}
		TextView cookie = (TextView) findViewById(R.id.cookietextview);
		if (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_AUTHORIZATION_CODE)
				|| (AuthConstants.GRANT_TYPE
						.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_PASSWORD))
				|| (AuthConstants.AUTHENTICATION_TYPE
						.equalsIgnoreCase(AuthConstants.KEY_TYPE_OAM))
				|| AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_IMPLICIT)) {
				if (ObSSOCookie != null && ObSSOCookie.length() > 10) {
					cookie.setText("ObSSOCookie: " + ObSSOCookie.substring(0, 10)
							+ "...");
				} else {
					cookie.setText("ObSSOCookie: Not Found" + "...");
				}
			}
		}

	@Override
	public void onBackPressed() {
		AppUtils.setChildActivity(getApplicationContext(), true);
		super.onBackPressed();
	}

}
