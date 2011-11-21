package com.proto.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * A collection of utility methods.
 */
public class StaticUtils {
	
	/* TODO: If these were to make use of the static getContext for the Application Singleton,
	 * they would only need one argument.
	 */
	/**
	 * Displays a Toast/popup message, based on a string parameter ID.
	 *
	 * @param caller The activity wishing to display the message.
	 * @param msgID The ID of the string (from the R file) to be displayed.
	 */
	public static void toaster(Context context, int msgID) {
		toaster(context, context.getString(msgID));
	}
	
	/**
	 * Displays a Toast/popup message, based on a string.
	 *
	 * @param caller The activity wishing to display the message.
	 * @param msgD The string to be displayed.
	 */
	public static void toaster(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		/* This will queue with other messages if they are already present.
		 * I would prefer to flush the queue, but this is not possible.*/
		toast.show();
	}
	
	public static boolean serviceRunning(Context context, String target) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (target.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Converts a hashmap into a Bundle, which can be passed with an {@link Intent}.
	 * Each item should be a String key and a corresponding Object that implements {@link Serializable}.
	 * Primitives will also work via this implementation, but you will probably need to use the non-primitive
	 * wrapper classes when casting the decoded objects.
	 * 
	 * Not sure why I bothered making this, as it would take the same amount of effort to 
	 * initially construct a Bundle instead of a HashMap. 
	 *
	 * @param map The hashmap to be converted.
	 * @return the bundle
	 */
	public static Bundle hashToBundle(HashMap<String, Serializable> map) {
		Bundle b = new Bundle();
		Set<String> keys = map.keySet();
		for (String k : keys)
			b.putSerializable(k, map.get(k));
		
		return b;
	}
}