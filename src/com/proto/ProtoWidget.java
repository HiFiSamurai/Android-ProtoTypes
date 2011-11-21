package com.proto;

import com.proto.R;
import com.proto.activities.ProtoAppActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ProtoWidget extends AppWidgetProvider {

	private int counter = 0;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(ProtoCore.TAG, "Updating Widget...");
		counter++;
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.proto_widget);
		remoteViews.setTextViewText(R.id.widget_textview, "Count: " + counter);
		
		Intent intent = new Intent(context, ProtoAppActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.proto_widget, pendingIntent);
		
		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(context, ProtoWidget.class);
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}
}
