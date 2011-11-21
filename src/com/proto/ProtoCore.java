package com.proto;

import hifi.db.DBAdapter;
import hifi.db.DBHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.proto.activities.maps.GeoSpot;
import com.proto.ajax.AJAXHandler;
import com.proto.ajax.GNewsObject;
import com.proto.ajax.PicasaObject;
import com.proto.db.GeoBean;
import com.proto.util.StaticUtils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ProtoCore extends Application {

	public static String TAG = "Proto";
	public SystemMonitor sys;

	private static ProtoCore instance;

	public static ProtoCore getContext() {
		return instance;
	}

	public static void serviceStart() {
		if (!StaticUtils.serviceRunning(instance, ProtoService.class.getName()))
			instance.startService(new Intent(instance, ProtoService.class));
	}

	public ProtoCore() {
		instance = this;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		sys = SystemMonitor.getInstance(this);

		// Run Android Test code here
		//Log.d(TAG, "RX Count: " + RXCounter.getRXCount());

	}

	private AJAXHandler ajax;

	protected void ajaxTester() {
		ajax = new AJAXHandler(instance);

		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";
		ajax.fetchXML(url, xmlCallback);

		//url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		//ajax.fetchJSON(url, jsonCallback);
	}

	Runnable xmlCallback = new Runnable() {
		public void run() {
			System.out.println("Posted callback...");
			List<PicasaObject> pics = AJAXHandler.getVals(PicasaObject.class, ajax.xmlPayload);

			for (PicasaObject p : pics) {
				System.out.println("ID: " + p.id);
				System.out.println("Title: " + p.title);
			}
		}
	};

	Runnable jsonCallback = new Runnable() {
		public void run() {
			System.out.println("Posted callback...");

			try {
				List<GNewsObject> news = AJAXHandler.getVals(GNewsObject.class, ajax.jsonPayload);

				for (GNewsObject g : news) {
					System.out.println("Title: " + g.title);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	protected class SC implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(ProtoCore.TAG, "Bound Service...");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(ProtoCore.TAG, "Unbound Service...");
		}
	};

	protected void scanGeoPoints() {
		DBHelper.setDatabaseName("Proto");
		DBHelper.addTable(GeoBean.GEN_TABLE_NAME, GeoBean.GEN_CREATE);

		DBAdapter db = new DBAdapter(getApplicationContext());
		List<GeoSpot> geos = db.fetchObjects(GeoSpot.class);
		for (GeoSpot g : geos)
			System.out.println("Geo: " + g.toString());
	}

	protected void readNetFile() {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("/proc/net/dev"));
			String s = in.readLine();

			while (s != null) {
				Log.d(TAG, s);
				s = in.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTerminate() {
		sys.pauseMonitoring();		// stops looking for location updates on close
		super.onTerminate();
	}
}