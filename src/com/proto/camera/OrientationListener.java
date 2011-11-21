package com.proto.camera;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;

public class OrientationListener extends OrientationEventListener {

	public static final int UNKNOWN = 0;
	public static final int UPRIGHT = 90;
	public static final int LANDSCAPE = 0;
	public static final int UPSIDE_DOWN = 270;
	public static final int INVERSE_LANDSCAPE = 180;
	
	public static int orientation;
	
	private static OrientationListener listener;
	
	public static void startListening(Context context) {
		if (listener == null)
			listener = new OrientationListener(context, SensorManager.SENSOR_DELAY_NORMAL);
		
		listener.enable();
	}
	
	public static void stopListening() {
		listener.disable();
	}
	
	private OrientationListener(Context context, int rate) {
		super(context, rate);
	}
	
	public static int getOrientation(){
		if(orientation >= 315 || orientation < 45)
			return UPRIGHT;
		else if(orientation >=45 && orientation < 135)
			return INVERSE_LANDSCAPE;
		else if(orientation >=135 && orientation < 225)
			return UPSIDE_DOWN;
		else if(orientation >=225 && orientation < 315)
			return LANDSCAPE;
		else
			return UNKNOWN;
	}

	@Override
	public void onOrientationChanged(int arg0) {
		orientation = arg0;
		Log.d("Camera", "Orientation Changed: " + orientation);		
	}
}
