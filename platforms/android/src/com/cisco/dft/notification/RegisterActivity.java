package com.cisco.dft.notification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ionicframework.csgtools667618.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterActivity extends Activity
{
	Button btnGCMRegister;
	Button btnXmppRegiser;
	Button btnSendMessage;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;
	AsyncTask<Void, Void, String> sendTask;
	AtomicInteger ccsMsgId = new AtomicInteger();

	static final String GOOGLE_PROJECT_ID = "819109776891";
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		context = getApplicationContext();

		btnGCMRegister = (Button) findViewById(R.id.btnGCMRegister);
		btnGCMRegister.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (TextUtils.isEmpty(regId))
				{
					Log.d("RegisterActivity", "GCM RegId is empty : Trying in Background");
					regId = registerGCM();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Already Registered with GCM Server!", Toast.LENGTH_LONG).show();
				}
			}
		});

		btnXmppRegiser = (Button) findViewById(R.id.btnXmppRegiser);
		btnXmppRegiser.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (TextUtils.isEmpty(regId))
				{
					Toast.makeText(getApplicationContext(), "RegId is empty!", Toast.LENGTH_LONG).show();
				}
				else
				{
					Log.d("RegisterActivity", "REGISTER:SendRegId : "+ regId);
					sendMessage("REGISTER");
				}
			}
		});

		btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
		btnSendMessage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if (TextUtils.isEmpty(regId))
				{
					Toast.makeText(getApplicationContext(), "RegId is empty!", Toast.LENGTH_LONG).show();
				}
				else
				{
					Log.d("RegisterActivity", "SendRegId : "+ regId);
					sendMessage("ECHO");
				}
			}
		});
	}

	private void sendMessage(final String action)
	{
		sendTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				Bundle data = new Bundle();
				data.putString("ACTION", action);
				data.putString("CLIENT_MESSAGE", "Hello GCM CCS XMPP!");
				String id = Integer.toString(ccsMsgId.incrementAndGet());

				try
				{
					Log.d("RegisterActivity", "messageid: " + id);
					gcm.send(GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id, data);
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
				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			}

		};
		sendTask.execute(null, null, null);
	}

	public String registerGCM()
	{
		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId))
		{
			Log.d("RegisterActivity", "registerGCM - unsuccessfully registered with GCM server - Empty: Trying in Background");
			registerInBackground();
		}
		else
		{
			Toast.makeText(getApplicationContext(), "RegId already available. RegId: " + regId, Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;
					storeRegistrationId(context, regId);
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
				Toast.makeText(getApplicationContext(), "Registered with GCM Server." + msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getSharedPreferences(RegisterActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
		//shareRegIdWithAppServer(context, regId);
		sendRegistrationToServer(regId);
		//sendMessage("REGISTER");
	}

	private void sendRegistrationToServer(String token)
	{
		Bundle data = new Bundle();
		data.putString("ACTION", "REGISTER");
		data.putString("CLIENT_MESSAGE", "Hello GCM CCS XMPP!");
		String id = Integer.toString(ccsMsgId.incrementAndGet());

		try
		{
			Log.d("RegisterActivity", "messageid: " + id);
			gcm.send(GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id, data);
			Log.d("RegisterActivity", "After gcm.send successful.");
		}
		catch (IOException e)
		{
			Log.d("RegisterActivity", "Exception: " + e);
			e.printStackTrace();
		}
	}

	public String shareRegIdWithAppServer(final Context context, final String regId)
	{
		String APP_SERVER_URL = "http://10.142.104.99:8080/GCMXMPPServer/GCMNotification?shareRegId=1";
		String result = "";
		Map paramsMap = new HashMap();
		paramsMap.put("regId", regId);
		try
		{
			URL serverUrl = null;
			try
			{
				serverUrl = new URL(APP_SERVER_URL);
			}
			catch (MalformedURLException e)
			{
				Log.e("AppUtil", "URL Connection Error: " + APP_SERVER_URL, e);
				result = "Invalid URL: " + APP_SERVER_URL;
			}

			StringBuilder postBody = new StringBuilder();
			Iterator<Entry<String, String>> iterator = paramsMap.entrySet().iterator();

			while (iterator.hasNext())
			{
				Entry<String, String> param = iterator.next();
				postBody.append(param.getKey()).append('=').append(param.getValue());
				if (iterator.hasNext())
				{
					postBody.append('&');
				}
			}
			String body = postBody.toString();
			byte[] bytes = body.getBytes();
			HttpURLConnection httpCon = null;
			try
			{
				httpCon = (HttpURLConnection) serverUrl.openConnection();
				httpCon.setDoOutput(true);
				httpCon.setUseCaches(false);
				httpCon.setFixedLengthStreamingMode(bytes.length);
				httpCon.setRequestMethod("POST");
				httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				OutputStream out = httpCon.getOutputStream();
				out.write(bytes);
				out.close();

				int status = httpCon.getResponseCode();
				if (status == 200)
				{
					result = "RegId shared with Application Server. RegId: " + regId;
				}
				else
				{
					result = "Post Failure." + " Status: " + status;
				}
			}
			finally
			{
				if (httpCon != null)
				{
					httpCon.disconnect();
				}
			}
		}
		catch (IOException e)
		{
			result = "Post Failure. Error in sharing with App Server.";
			Log.e("AppUtil", "Error in sharing with App Server: " + e);
		}
		return result;
	}
}
