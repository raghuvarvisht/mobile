package com.plugin.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBroadcastReceiver;
import static com.google.android.gcm.GCMConstants.DEFAULT_INTENT_SERVICE_CLASS_NAME;

/*
 * Implementation of GCMBroadcastReceiver that hard-wires the intent service to be 
 * com.plugin.gcm.GCMIntentService, instead of your_package.GCMIntentService 
 */
public class CordovaGCMBroadcastReceiver extends GCMBroadcastReceiver
{
	@Override
	protected String getGCMIntentServiceClassName(Context context)
	{
    	System.out.println("Inside getGCMIntentServiceClassName method");
		return "com.plugin.gcm" + DEFAULT_INTENT_SERVICE_CLASS_NAME;
    }
}