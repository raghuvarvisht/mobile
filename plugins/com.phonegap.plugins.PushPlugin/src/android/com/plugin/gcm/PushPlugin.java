package com.plugin.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cisco.dft.notification.RegisterActivity;
import com.google.android.gcm.GCMRegistrar;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


/**
 * @author awysocki
 */

public class PushPlugin extends CordovaPlugin
{
	private static final String PACKAGE = "com.javapapers.android.gcm.multiple";
	public static final String TAG = "PushPlugin";
	public static final String REGISTER = "register"; // used from js
	public static final String SEND_MESSAGE = "sendmessage"; // used from js
	public static final String UNREGISTER = "unregister"; // used from js
	public static final String EXIT = "exit"; // used from js

	private static CordovaWebView gWebView;
	private static String gECB;
	private static String gSenderID;
	private static Bundle gCachedExtras = null;
	private static boolean gForeground = false;

	GoogleCloudMessaging gcm;
	String regId;
	AsyncTask<Void, Void, String> sendTask;
	AtomicInteger ccsMsgId = new AtomicInteger();
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	/**
	 * Gets the application context from cordova's main activity.
	 * @return the application context
	 */
	private Context getApplicationContext()
	{
		return this.cordova.getActivity().getApplicationContext();
	}

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext)
	{
		boolean result = false;
		Log.v(TAG, "execute: action=" + action);

		if (action.equals(REGISTER))
		{
			Log.v(TAG, "execute: data=" + data.toString());
			try
			{
				JSONObject jo = data.getJSONObject(0);

				gWebView = this.webView;
				Log.v(TAG, "execute: jo=" + jo.toString());

				gECB = (String) jo.get("ecb");
				gSenderID = (String) jo.get("senderID");

				Log.v(TAG, "execute: ECB=" + gECB + " senderID=" + gSenderID);

				//GCMRegistrar.register(getApplicationContext(), gSenderID);
				registerGCM();
				result = true;
				callbackContext.success();
			}
			catch (JSONException e)
			{
				Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
				result = false;
				callbackContext.error(e.getMessage());
			}

			if ( gCachedExtras != null)
			{
				Log.v(TAG, "sending cached extras");
				sendExtras(gCachedExtras);
				gCachedExtras = null;
			}
		}
		else if (action.equals(UNREGISTER))
		{
			GCMRegistrar.unregister(getApplicationContext());
			Log.v(TAG, "UNREGISTER");
			result = true;
			callbackContext.success();
		}
		else if (action.equals(SEND_MESSAGE))
		{
			Log.v(TAG, "Sending Message" + data.toString());
			try
			{
				if (TextUtils.isEmpty(regId))
				{
					//regId = registerGCM();
					Log.d("RegisterActivity", "GCM RegId: " + regId);
					//sendMessage("REGISTER");
					callbackContext.error("Device is not Registered with GCM");
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "Already Registered with GCM Server!", Toast.LENGTH_LONG).show();
					sendMessage("ECHO"); // TODO change it to ACTION_TOOLS_CONFIG = PACKAGE + ".TOOLCONFIG"  as in ProcessorFactory.java
				}
			}
			catch (Exception e)
			{
				Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
				result = false;
				callbackContext.error(e.getMessage());
			}
			if ( gCachedExtras != null)
			{
				Log.v(TAG, "sending cached extras");
				sendExtras(gCachedExtras);
				gCachedExtras = null;
			}
		}
		else
		{
			result = false;
			Log.e(TAG, "Invalid action : " + action);
			callbackContext.error("Invalid action : " + action);
		}
		return result;
	}

	private void sendMessage(final String action)
	{
		sendTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				Bundle data = new Bundle();
				data.putString("ACTION", PACKAGE + "." +  action);
				data.putString("CLIENT_MESSAGE", "Hello GCM CCS XMPP!");
				String id = Integer.toString(ccsMsgId.incrementAndGet());
				try
				{
					Log.d("RegisterActivity", "messageid: " + id);
					gcm.send(gSenderID + "@gcm.googleapis.com", id,data);
					Log.d("RegisterActivity", "After gcm.send successful.");
				}
				catch (IOException e)
				{
					Log.d("RegisterActivity", "Exception: " + e);
					e.printStackTrace();
				}
				return "Sent message.";
			}
			@Override
			protected void onPostExecute(String result)
			{
				sendTask = null;
				//Toast.makeText(getApplicationContext(), result,Toast.LENGTH_LONG).show();
			}
		};
		sendTask.execute(null, null, null);
	}

	/*
	 * Sends a json object to the client as parameter to a method which is defined in gECB.
	 */
	public static void sendJavascript(JSONObject _json)
	{
		String _d = "javascript:" + gECB + "(" + _json.toString() + ")";
		Log.v(TAG, "sendJavascript: " + _d);
		if (gECB != null && gWebView != null)
		{
			gWebView.sendJavascript(_d);
		}
	}

	/*
	 * Sends the pushbundle extras to the client application.
	 * If the client application isn't currently active, it is cached for later processing.
	 */
	public static void sendExtras(Bundle extras)
	{
		if (extras != null) {
			if (gECB != null && gWebView != null)
			{
				sendJavascript(convertBundleToJson(extras));
			}
			else
			{
				Log.v(TAG, "sendExtras: caching extras to send at a later time.");
				gCachedExtras = extras;
			}
		}
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView)
	{
		super.initialize(cordova, webView);
		gForeground = true;
	}

	@Override
	public void onPause(boolean multitasking)
	{
		super.onPause(multitasking);
		gForeground = false;
		final NotificationManager notificationManager = (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	@Override
	public void onResume(boolean multitasking)
	{
		super.onResume(multitasking);
		gForeground = true;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		gForeground = false;
		gECB = null;
		gWebView = null;
	}

	/*
     * serializes a bundle to JSON.
     */
	private static JSONObject convertBundleToJson(Bundle extras)
	{
		try
		{
			JSONObject json;
			json = new JSONObject().put("event", "message");

			JSONObject jsondata = new JSONObject();
			Iterator<String> it = extras.keySet().iterator();
			while (it.hasNext())
			{
				String key = it.next();
				Object value = extras.get(key);

				// System data from Android
				if (key.equals("from") || key.equals("collapse_key"))
				{
					json.put(key, value);
				}
				else if (key.equals("foreground"))
				{
					json.put(key, extras.getBoolean("foreground"));
				}
				else if (key.equals("coldstart"))
				{
					json.put(key, extras.getBoolean("coldstart"));
				}
				else
				{
					// Maintain backwards compatibility
					if (key.equals("message") || key.equals("msgcnt") || key.equals("soundname"))
					{
						json.put(key, value);
					}

					if ( value instanceof String )
					{
						// Try to figure out if the value is another JSON object
						String strValue = (String)value;
						if (strValue.startsWith("{"))
						{
							try
							{
								JSONObject json2 = new JSONObject(strValue);
								jsondata.put(key, json2);
							}
							catch (Exception e)
							{
								jsondata.put(key, value);
							}
							// Try to figure out if the value is another JSON array
						}
						else if (strValue.startsWith("["))
						{
							try
							{
								JSONArray json2 = new JSONArray(strValue);
								jsondata.put(key, json2);
							}
							catch (Exception e)
							{
								jsondata.put(key, value);
							}
						}
						else
						{
							jsondata.put(key, value);
						}
					}
				}
			} // while
			json.put("payload", jsondata);
			Log.v(TAG, "extrasToJSON: " + json.toString());
			return json;
		}
		catch( JSONException e)
		{
			Log.e(TAG, "extrasToJSON: JSON exception");
		}
		return null;
	}

	public static boolean isInForeground()
	{
		return gForeground;
	}

	public static boolean isActive()
	{
		return gWebView != null;
	}

	public String registerGCM()
	{
		gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		regId = getRegistrationId(getApplicationContext());
		if (TextUtils.isEmpty(regId))
		{
			registerInBackground();
			Log.d("RegisterActivity", "registerGCM - successfully registered with GCM server - regId: " + regId);
		}
		else
		{
			//Toast.makeText(getApplicationContext(), "RegId already available. RegId: " + regId, Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getApplicationContext().getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty())
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			Log.d("RegisterActivity", "I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String msg = "";
				try
				{
					if (gcm == null)
					{
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regId = gcm.register(gSenderID);
					Log.d("RegisterActivity", "registerInBackground - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;
					storeRegistrationId(getApplicationContext(), regId);
				}
				catch (IOException ex)
				{
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				//Toast.makeText(getApplicationContext(), "Registered with GCM Server." + msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getApplicationContext().getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
		//sendRegistrationToServer(regId);
		sendMessage("REGISTER");
	}
	
	/*private void sendRegistrationToServer(String token)
	{
		Bundle data = new Bundle();
		data.putString("ACTION", "REGISTER");
		data.putString("CLIENT_MESSAGE", "Hello GCM CCS XMPP!");
		String id = Integer.toString(ccsMsgId.incrementAndGet());

		try
		{
			Log.d("RegisterActivity", "messageid: " + id);
			gcm.send(gSenderID + "@gcm.googleapis.com", id, data);
			Log.d("RegisterActivity", "After gcm.send successful.");
		}
		catch (IOException e)
		{
			Log.d("RegisterActivity", "Exception: " + e);
			e.printStackTrace();
		}
	}*/

}
