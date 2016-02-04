package com.cisco.dft.seed;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cisco.dft.oauth.connections.AuthConnection;
import com.cisco.dft.oauth.connections.ConnectionUtils;
import com.cisco.dft.oauth.entities.EntityAuth;
import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.oauth.utils.AuthUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Opens a Protected WebView within the application
 **/
public class ProductWebView extends PassCodeActivity {
	private ProgressBar mPbar;
	private String url = "http://tools.cisco.com/gct/Upgrade/jsp/index.jsp";
	private boolean isInitital = false;
	private WebView mainWebView;
	private Map<String, String> headers;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		mPbar = (ProgressBar) findViewById(R.id.pbarWeb);
		mainWebView = (WebView) findViewById(R.id.mainWebView);
		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		mainWebView.setWebViewClient(new MyCustomWebViewClient());
		mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		mainWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		mainWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		if (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL)) {
			mainWebView.loadUrl(url);
		} else {
			FetchCookie myCookie = new FetchCookie();
			myCookie.execute();
		}
	}

	private class FetchCookie extends AsyncTask<Void, Boolean, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPbar.setVisibility(ProgressBar.VISIBLE);
			headers = new HashMap<String, String>();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return getUpdatedCookie();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				mainWebView.clearCache(true);
				mainWebView.loadUrl(url, headers);
			} else {
				startlogin();
			}
		}
	}

	private class MyCustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mPbar.setVisibility(ProgressBar.GONE);
			if (!(AuthConstants.GRANT_TYPE
					.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_CLIENT_CREDENTIAL))) {
				String cookies = CookieManager.getInstance().getCookie(url);

				// Fetch the new ObSSOCookie from the WebView response and
				// over-write it in database if its a valid cookie
				String webCookie = ConnectionUtils.getCookieValueByName(cookies, "ObSSOCookie=");
				if (webCookie != null
						&& webCookie.contains("loggedoutcontinue")
						&& isInitital) {
					view.clearCache(true);
					AuthUtils.doLogOut(getApplicationContext());
					startlogin();
					isInitital = false;
				} else {
					EntityAuth auth = AuthUtils
							.getAuth(getApplicationContext());
					auth.cookie = webCookie;
					auth.cookieExpiresIn = ((System.currentTimeMillis() / 1000) + AuthConstants.COOKIE_EXPIRATIONTIME);
					AuthUtils.setAuth(getApplicationContext(), auth);
					auth = AuthUtils.getAuth(getApplicationContext());
				}
			}
		}
	}

	/**
	 * Used to refresh the cookie when it is expired for different
	 * authentication and grant types
	 */

	private boolean getUpdatedCookie() {

		boolean isUpdated = true;
		CookieManager.getInstance().setAcceptCookie(true);
		if (AuthConstants.AUTHENTICATION_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_TYPE_OAM)) {
			AuthConnection connection = new AuthConnection(
					getApplicationContext());
			connection.refreshCookieUsingExistingCookie();
			EntityAuth auth = AuthUtils.getAuth(getApplicationContext());

			headers = new HashMap<String, String>();
			headers.put("Cookie", "ObSSOCookie=" + auth.cookie);
		} else if (((AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_AUTHORIZATION_CODE)) || (AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_IMPLICIT)))) {
			AuthConnection connection = new AuthConnection(
					getApplicationContext());
			connection.getUserCookie();
			EntityAuth auth = AuthUtils.getAuth(getApplicationContext());
			headers.put(ConnectionUtils.KEY_COOKIE,
					ConnectionUtils.KEY_OBS_COOKIE + auth.cookie);
		} else if ((AuthConstants.GRANT_TYPE
				.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_PASSWORD))) {
			if (AuthUtils.isCookieEnabled()) {
				AuthConnection connection = new AuthConnection(
						getApplicationContext());
				connection.getUserCookie();
				EntityAuth auth = AuthUtils.getAuth(getApplicationContext());

				headers.put(ConnectionUtils.KEY_COOKIE,
						ConnectionUtils.KEY_OBS_COOKIE + auth.cookie);
			} else {
				isUpdated = false;
			}
		}
		return isUpdated;
	}

	/**
	 * Shows the login activity
	 */
	private void startlogin() {
		Intent intent = new Intent(ProductWebView.this,
				com.cisco.dft.oauth.activities.LoginActivity.class);
		intent.putExtra("nextActivity", MainActivity.class);
		intent.putExtra("app_title", getResources()
				.getString(R.string.app_name));
		startActivity(intent);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		isInitital = true;
	}

	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}

}
