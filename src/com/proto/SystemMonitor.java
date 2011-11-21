package com.proto;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * Singleton class to monitor signal strength and location.
 * 
 * Author: JMR, September 2011
 */
public class SystemMonitor extends PhoneStateListener implements LocationListener {

	/** Checks for location updates this many milliseconds */
	protected long UPDATE_INTERVAL = 60 * 1000;		// 

	/** Location readings are considered old if age difference exceeds this. */
	protected long SHELF_LIFE = UPDATE_INTERVAL*5;	// 

	/** The current location. */
	private Location currentLocation;

	/** The signal strength. */
	private int signalStrength;

	private int altSignalStrength;

	/** TelephonyManager for checking signal strength. */
	private TelephonyManager tel;

	/** LocationManager for checking current location. */
	private LocationManager loc;

	/** Instance of the singleton class. */
	private static SystemMonitor instance;

	/**
	 * Gets the single instance of SystemMonitor.
	 *
	 * @param app The application using the SystemMonitor
	 * @return Single instance of SystemMonitor
	 */
	public static SystemMonitor getInstance(Application app) {
		if (instance == null)
			instance = new SystemMonitor(app);

		return instance;
	}

	/**
	 * Instantiates a new system monitor.
	 *
	 * @param app The application using the SystemMonitor
	 */
	private SystemMonitor(Application app) {
		tel = (TelephonyManager) app.getSystemService(Context.TELEPHONY_SERVICE);
		loc = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
		resumeMonitoring();
	}

	/**
	 * Pause monitoring.
	 */
	public void pauseMonitoring() {
		loc.removeUpdates(this);
	}

	/**
	 * Resume monitoring.
	 */
	public void resumeMonitoring() {
		loc.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, 0, this);
		tel.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
		tel.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	/* Listener Functions*/
	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onSignalStrengthsChanged(android.telephony.SignalStrength)
	 */
	@Override
	public void onSignalStrengthChanged(int asu) {
		super.onSignalStrengthChanged(asu);
		this.altSignalStrength = asu;
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		super.onSignalStrengthsChanged(signalStrength);
		this.signalStrength = signalStrength.getGsmSignalStrength();
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		if (isBetterLocation(location))
			this.currentLocation = location;
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {

	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {

	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * Checks if incoming location is more accurate/newer than currently stored location.
	 *
	 * @param location The newly detected location
	 * @return true, if new location is much newer or more accurate
	 */
	private boolean isBetterLocation(Location location) {
		/* 	TODO: This could be improved upon, mainly by comparing the times and 
		 *  providers of the different locations. But it provides a good baseline,
		 *  especially since updates are only requested once a minute. */
		if (currentLocation == null)			// no location is set yet
			return true;

		long timeDelta = location.getTime() - currentLocation.getTime();
		if (timeDelta > SHELF_LIFE)		// current location is out-dated
			return true;

		if (location.getAccuracy() > currentLocation.getAccuracy())	// new location more accurate
			return true;

		return false;
	}

	public int getSignalStrength() {
		return signalStrength;
	}
	
	public int getAltSignalStrength() {
		return altSignalStrength;
	}

	public Location getLocation() {
		return currentLocation;
	}
}