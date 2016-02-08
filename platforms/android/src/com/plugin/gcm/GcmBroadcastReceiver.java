package com.plugin.gcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cisco.dft.notification.GCMNotificationIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ionicframework.csgtools667618.MainActivity;

import org.json.JSONObject;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
    static final String TAG = "pushnotification";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
	public void onReceive(Context context, Intent intent)
	{
		ComponentName comp = new ComponentName(context.getPackageName(), GCMNotificationIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
		System.out.println("Inside onReceive method");
        recieveMessage(context, intent);
	}

    public void recieveMessage(Context context, Intent intent)
    {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        WakeLocker.acquire(ctx);
        String messageType = gcm.getMessageType(intent);
        System.out.println("Server message type " + messageType);
        System.out.println("Notification message on recieve " + intent.getExtras().toString());

        String message = intent.getStringExtra("message");
        System.out.println("Server Message Recieved " + message);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
        {
            sendNotification("Send error: " + intent.getExtras().toString(), intent, context);
        }
        else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
        {
            sendNotification("Deleted messages on server: " + intent.getExtras().toString(), intent, context);
        }
        else
        {
            sendNotification("Received: " + intent.getExtras().toString(), intent, context);
        }
        System.out.println("Notification display");
        setResultCode(Activity.RESULT_OK);
        WakeLocker.release();
    }

    private void sendNotification(String message, Intent recvdIntent, Context context)
    {
        //Intent intent = new Intent(this, MainActivity.class);
        String serverMsg = recvdIntent.getStringExtra("message");
        JSONObject serverObj = null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        try
        {
            if (serverMsg != null)
            {
                serverObj = new JSONObject(serverMsg);
                String redirectClientScreenURL = "toolInfo";
                Intent serverIntent = new Intent(context, com.cisco.dft.seed.SplashScreen.class);

                /*serverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                serverIntent.putExtra("toolId", serverObj.getString("id"));
                serverIntent.putExtra("info", serverObj.getString("info"));
                serverIntent.putExtra("action", serverObj.getString("action"));
                serverIntent.putExtra("webpage", serverObj.getString("webpage"));
                serverIntent.putExtra("topic", serverObj.getString("topic"));
                serverIntent.putExtra("notificationMessage", message);

                serverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/

                SharedPreferences.Editor editor = context.getSharedPreferences("ServerParams", context.MODE_PRIVATE).edit();
                editor.putString("toolId", serverObj.getString("id"));
                editor.putString("info", serverObj.getString("info"));
                editor.putString("action", serverObj.getString("action"));
                editor.putString("topic", serverObj.getString("topic"));
                editor.putString("redirectServerURL", serverObj.getString("webpage"));
                editor.putString("redirectClientScreenURL", redirectClientScreenURL);
                editor.putString("notificationMessage", message);
                editor.commit();

                PendingIntent serverPendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, serverIntent, PendingIntent.FLAG_ONE_SHOT);

                notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(context.getApplicationInfo().icon)
                        .setContentTitle(serverObj.getString("id") + " - CSG Tools Mobile App")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(serverPendingIntent);
            }
            else
            {
                Intent registerIntent = new Intent(context, com.cisco.dft.seed.SplashScreen.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent gcmIntent = PendingIntent.getActivity(context, 0 /* Request code */, registerIntent, PendingIntent.FLAG_ONE_SHOT);
                notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(context.getApplicationInfo().icon)
                        .setContentTitle("GCM Registered - CSG Tools Mobile App")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(gcmIntent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
