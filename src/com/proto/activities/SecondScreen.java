package com.proto.activities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.proto.ProtoCore;
import com.proto.R;
import com.proto.classes.SaveBucket;
import com.proto.util.StaticUtils;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SecondScreen extends Activity implements OnClickListener {

	private Integer loadCounter;

	private final String RX_FILE = "/sys/class/net/eth0/statistics/rx_bytes"; 
	private final String MAC_FILE = "/sys/class/net/eth0/address";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_screen);

		/* The savedInstanceState will get populated whenever the screen is rotated or closed,
		 * and this allows it to restore the information when it gets redrawn. */
		SaveBucket s = (savedInstanceState == null) ? null : 
			(SaveBucket) savedInstanceState.getSerializable(SaveBucket.SAVE_KEY);
		loadCounter = (s == null) ? 0 : s.val;

		loadCounter++;		// Increment counter of how many times Activity has been created

		TextView loadCountText = (TextView) findViewById(R.id.loadCounter);
		if (loadCountText != null)
			loadCountText.setText("Rotations:" + loadCounter.toString());

		/* Event handler for button activity */
		Button closeButton = (Button) findViewById(R.id.closeBtn);
		if (closeButton != null) {
			closeButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		/* Event handler for button activity */
		Button sigStrBtn = (Button) findViewById(R.id.sigBtn);
		if (sigStrBtn != null)
			sigStrBtn.setOnClickListener(this);

		Button locBtn = (Button) findViewById(R.id.locBtn);
		if (locBtn != null)
			locBtn.setOnClickListener(this);

		Button rxBtn = (Button) findViewById(R.id.rxBtn);
		if (rxBtn != null)
			rxBtn.setOnClickListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SaveBucket s = new SaveBucket(loadCounter);
		outState.putSerializable(SaveBucket.SAVE_KEY, s);
	}

	/* This will override the 'back' button, only for this activity.*/
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			StaticUtils.toaster(this, "No escape");
		return true;
	}

	@Override
	public void onClick(View v) {
		ProtoCore app = (ProtoCore) getApplication();
		if (v.getId() == R.id.sigBtn) {
			String address = readFileData(MAC_FILE);
			int sigStr = app.sys.getSignalStrength();
			
			String sigMsg = String.format("MAC: %s\nGSM: %d\ndBm: %d", 
					address, app.sys.getSignalStrength(), (sigStr*2) -113);
			StaticUtils.toaster(this, sigMsg);
		} else if (v.getId() == R.id.locBtn) {
			Location l = app.sys.getLocation();
			String msg = (l == null) ? "Not yet..." : String.format("Latitude: %s, Longitude: %s",
					l.getLatitude(), l.getLongitude());
			StaticUtils.toaster(this, msg);
		} else if (v.getId() == R.id.rxBtn) {
			/* API >= 8 (Ver 2.2) Only)
			Long classData = TrafficStats.getTotalRxBytes();
			StaticUtils.toaster(this, classData.toString()); */

			String fileData = readFileData(RX_FILE);
			StaticUtils.toaster(this, fileData);
		}
	}

	private String readFileData(String fileName) {
		String fileData;
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			fileData = "Bytes Received: " + in.readLine();
		} catch (FileNotFoundException e) {
			fileData = "File not found";
		} catch (IOException e) {
			fileData = "Couldn't read line";
		}

		return fileData;
	}
}