package com.cisco.dft.seed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ionicframework.csgtools667618.R;

import com.cisco.dft.oauth.passcode.PassCodeActivity;

/**
 * Opens the Cisco Online Privacy Statement Highlights in a WebView within the
 * application
 *
 * @author tchodey
 *
 */

public class PrivacyContent extends PassCodeActivity {

	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		WebView mainWebView = (WebView) findViewById(R.id.mainWebView);
		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		mainWebView.setWebViewClient(new MyCustomWebViewClient());
		mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		// Loads the WebView with the given URL
		mainWebView.loadUrl("http://www.cisco.com/web/siteassets/legal/privacy.html");
	}

	private class MyCustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
