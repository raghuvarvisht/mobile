package com.plugin.gcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cisco.dft.notification.GCMNotificationIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ionicframework.csgtools667618.MainActivity;

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
        String message = intent.getStringExtra("data");
        System.out.println("Server Message Recieved " + message);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
        {
            sendNotification("Send error: " + intent.getExtras().toString(), intent, context, "earms");
        }
        else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
        {
            sendNotification("Deleted messages on server: " + intent.getExtras().toString(), intent, context, "earms");
        }
        else
        {
            sendNotification("Received: " + intent.getExtras().toString(), intent, context, "earms");
        }
        System.out.println("Notification display");
        setResultCode(Activity.RESULT_OK);
        WakeLocker.release();
    }

    private void sendNotification(String message, Intent intent1, Context context, String toolId)
    {
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(context, com.cisco.dft.seed.SplashScreen.class);
        //data check the filessdsd
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isNotificationRedirect", toolId);
        intent.putExtra("notificationMessage",message);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
