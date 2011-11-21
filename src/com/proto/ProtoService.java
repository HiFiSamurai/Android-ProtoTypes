package com.proto;

import java.util.List;

import com.proto.activities.ProtoAppActivity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ProtoService extends Service {

	private Class<ProtoAppActivity> notificationTarget = ProtoAppActivity.class;

	private int msgCount = 0;

	@Override 
	public void onCreate() {
		Log.i(ProtoCore.TAG, "Created Service...");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.i(ProtoCore.TAG, "Starting Service...");

		new Thread() {
			public void run(){
				schedule();
			}
		}.start();
	}

	private void schedule() {
		long endTime = System.currentTimeMillis() + 60*1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}

		//dispatchNotification();
		this.stopSelf();
	}

	protected void dispatchNotification() {
		msgCount++;
		Context context = getApplicationContext();
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.icon,"PROTO!",System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context,notificationTarget), PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, "Something Happened", "Count: " + msgCount, contentIntent);
		int NOTIFICATION_ID = 10;
		manager.notify(NOTIFICATION_ID, notification);

		checkProcesses();
//		if (msgCount >= 5)
//			this.stopSelf();
//		else
//			schedule();		
	}

	@Override
	public void onDestroy() {
		Log.i(ProtoCore.TAG, "Ending Service...");
		super.onDestroy();
	}
	
	private void checkProcesses() {
		ActivityManager aMan = (ActivityManager) getApplication().getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> l = aMan.getRunningAppProcesses();
		
		for (RunningAppProcessInfo i : l) {
			if (i.processName == getPackageName())
				System.out.println("Name: " + i.processName + "\tImportance: " + i.importance);
		}
	}
}
