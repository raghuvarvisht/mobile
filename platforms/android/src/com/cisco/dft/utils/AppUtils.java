package com.cisco.dft.utils;

import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.cisco.dft.oauth.connections.AuthConnection;
import com.cisco.dft.oauth.connections.ConnectionUtils;
import com.cisco.dft.oauth.entities.EntityDataResponse;
import com.cisco.dft.oauth.utils.AuthConstants;

/**
 * Defines the constants and methods used for the Application
 * 
 * @author tchodey
 * 
 */
public class AppUtils {
	/**
	 * URL to fetch the data for the Team directory
	 */
	public static final String URL_METADATA = "https://api.cisco.com/dft/common/teamdata";
	public static final String KEY_HOME = "Home";
	public static final String KEY_TEAM = "Web API";
	public static final String KEY_WEBVIEW = "Web APP";
	public static final String GCM_SCREEN = "GCM";
	public static final String KEY_SETTINGS = "Settings";
	public static final String KEY_PREFERENCE_NAME = "PIN_INFO";
	static String KEY_ACT_NAME = "ACTIVITY";

	public static final String KEY_BG_TIME = "backgroundtime";
	public static final String KEY_IS_FIRSTOPEN = "isfirstopen";
	public static final int LOGIN_TIME_OUT = AuthConstants.IMPLICIT_INACTIVE_TIME;

	public static String USER_ID = "";

	public static final String KEY_NAME = "name";
	public static final String KEY_ACC_LEVEL = "accLevel";
	public static final String KEY_MAIL = "mail";
	public static final String KEY_UID = "uid";

	/**
	 * Decodes the image
	 * 
	 * @param input
	 * @return
	 */
	public static Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	/**
	 * Checks if the Internet connection is available
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetAvaliable(Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isConnected()) {
			return true;
		} else if (mobile.isConnected()) {
			return true;
		}
		return false;

	}

	/**
	 * Gets the data for the TeamFragment by passing a ObSSOCookie from the
	 * database as part of the connection
	 * 
	 * @param url
	 * @param ctx
	 * @param cookie
	 * @return response string for team data
	 */
	public static String getDataBasedOnCookie(String url, Context ctx) {
		try {

			EntityDataResponse response = new EntityDataResponse();
			AuthConnection connection = new AuthConnection(ctx);
			response = connection.getDataWithCookie(url,
					ConnectionUtils.METHOD_GET);
			if (response.responseId == AuthConstants.RESPONSE_SUCCESS) {
				return response.responseData;
			} else {
				return response.responseData;
			}

		} catch (Exception e) {
			Log.w(" ERROR In Get Data :" + url + ":" + e.toString(), "==");
			return " ERROR " + e.getMessage();
		}
	}

	/**
	 * Fetches the data from the server by sending the updated access token
	 * 
	 * @param url
	 * @param ctx
	 * @return
	 */
	public static String getData(String url, Context ctx) {
		try {

			EntityDataResponse response = new EntityDataResponse();
			AuthConnection connection = new AuthConnection(ctx);
			response = connection.getDataWithAccessToken(url, "GET");

			if (response.responseId == AuthConstants.RESPONSE_SUCCESS) {
				return response.responseData;
			} else {
				return response.responseData;
			}

		} catch (Exception e) {
			Log.w(" ERROR In Get Data :" + url + ":" + e.toString(), "==");
			return " ERROR " + e.getMessage();
		}
	}

	/**
	 * Used for Implicit Grant type where the idle time for the application when
	 * sent to background can be configured
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isShowLogin(Context ctx) {
		long time = getBgTime(ctx) + AppUtils.LOGIN_TIME_OUT;
		if (time < (System.currentTimeMillis() / 1000)
				&& (AuthConstants.GRANT_TYPE
						.equalsIgnoreCase(AuthConstants.KEY_GRANT_TYPE_IMPLICIT))) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Gets Cisco Font
	 * 
	 * @param ctx
	 * @return
	 */
	public static Typeface getCiscoFont(Context ctx) {

		String CISCO_FONT_NAME = "CiscoSansExtraLight.otf";

		Typeface face = Typeface.createFromAsset(ctx.getAssets(), "fonts/"
				+ CISCO_FONT_NAME);
		return face;

	}

	/**
	 * Saves the value of External Activity when the application enters into the
	 * background
	 * 
	 */
	public static void setChildActivity(Context context, boolean isClosed) {
		SharedPreferences preferences = context.getSharedPreferences(
				AppUtils.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(KEY_ACT_NAME, isClosed);
		editor.commit();
	}

	public static boolean getChildActivity(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				AppUtils.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(KEY_ACT_NAME, false);
	}

	/**
	 * Saves the value of the time-stamp when the application enters into the
	 * background
	 * 
	 */
	public static void setBgTime(Context context, boolean isClosed) {

		if (isClosed) {
			SharedPreferences preferences = context.getSharedPreferences(
					AppUtils.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(AppUtils.KEY_BG_TIME,
					System.currentTimeMillis() / 1000);
			editor.putBoolean(AppUtils.KEY_IS_FIRSTOPEN, isClosed);
			editor.commit();
		}

	}

	/**
	 * Retrieves the value of the time-stamp when the application is sent to
	 * background
	 * 
	 * @param context
	 * @return
	 */
	public static long getBgTime(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				AppUtils.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
		long status = preferences.getLong(AppUtils.KEY_BG_TIME, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(AppUtils.KEY_IS_FIRSTOPEN, false);
		editor.commit();
		return status;
	}

	/**
	 * Checks if the application is being sent in the background
	 * 
	 * @param context
	 * @return <code>true</code>
	 */
	public static boolean isApplicationSentToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;

			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Saves the information of the user logged-in to the application in shared
	 * preferences
	 * 
	 * @param ctx
	 * @param name
	 * @param accLevel
	 */
	public static void storeTitleInfo(Context ctx, String name,
			String accLevel, String mail, String uid) {

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor edit = pref.edit();
		edit.putString(KEY_NAME, name);
		edit.putString(KEY_ACC_LEVEL, accLevel);
		edit.putString(KEY_MAIL, mail);

		edit.putString(KEY_UID, uid);

		edit.commit();
	}

	/**
	 * Retrieves UserName and access level of the user from the Application
	 * preferences and saves them in a hash-map
	 * 
	 * @param ctx
	 * @return map
	 */
	public static HashMap<String, String> getTitleInfo(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KEY_NAME, pref.getString(KEY_NAME, ""));
		map.put(KEY_ACC_LEVEL, pref.getString(KEY_ACC_LEVEL, ""));
		map.put(KEY_MAIL, pref.getString(KEY_MAIL, ""));
		map.put(KEY_UID, pref.getString(KEY_UID, ""));

		return map;
	}

	/**
	 * Retrieves the information of the user logged-in from the Hash-map
	 * 
	 * @param ctx
	 * @return String
	 */
	public static final String getTitle(Context ctx) {
		HashMap<String, String> map = getTitleInfo(ctx);
		return ("Welcome " + map.get("name"));
	}

	/**
	 * Checks if the information of the user logged-in to the application
	 * already exists or not
	 * 
	 * @param ctx
	 * @return boolean
	 */
	public static boolean isTitleavailable(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getString("name", "").length() > 2;
	}
}
