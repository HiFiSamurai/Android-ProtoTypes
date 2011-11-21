package com.proto.activities;

import java.util.ArrayList;
import java.util.List;

import com.proto.ProtoCore;
import com.proto.R;
import com.proto.activities.maps.MapProto;
import com.proto.camera.PhotoList;
import com.proto.chart.Chart;
import com.proto.classes.TransferBucket;
import com.proto.contacts.ContactScanner;
import com.proto.list.DynamicListActivity;
import com.proto.util.StaticUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

@SuppressWarnings("unused")
public class ProtoAppActivity extends Activity implements OnItemSelectedListener {
	private boolean departed = false;

	private Spinner spinner1;
	private Spinner spinner2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ProtoCore.serviceStart();

		ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		if (toggle != null) {
			final Activity caller = this;		// needed to pass reference to click handler

			toggle.setOnCheckedChangeListener( new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int toastID = isChecked ? R.string.toggleON : R.string.toggleOFF;
					StaticUtils.toaster(caller, toastID);
				}
			});
		}

		Button checkButton = (Button) findViewById(R.id.checkTrigger);
		if (checkButton != null) {
			checkButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					nextScreen(CallbackScreen.class, CallbackScreen.REQUEST_ID);
				}
			});
		}

		Button listButton = (Button) findViewById(R.id.listTrigger);
		if (listButton != null) {
			listButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					nextScreen(DynamicListActivity.class);
				}
			});
		}
		
		Button graphButton = (Button) findViewById(R.id.graphTrigger);
		if (graphButton != null) {
			graphButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Chart chart = new Chart("Demo", "ex", "why");
					List<Double> x = new ArrayList<Double>();
					for (int i=-5; i<=5; i++)
						x.add((double) i);
					
					List<Double> y = new ArrayList<Double>();
					for (int i=-5; i<=5; i++)
						y.add((double) (3*i + Math.pow(i, 2)));
					chart.addSeries("sample", x, y);
					
					y = new ArrayList<Double>();
					for (int i=-5; i<=5; i++)
						y.add((double) (15 - Math.pow(i, 3)));
					chart.addSeries("alternate", x, y);

					startActivity(chart.buildLineChart(ProtoAppActivity.this));
				}
			});
		}

		setupSpinners();
	}

	private void setupSpinners() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		initSpinner(spinner1, new String[]{"Alpha","Bravo","Charlie"});
		initSpinner(spinner2, new String[]{"X-Ray","Yankee","Zulu"});

		setVisibility();
	}

	private void initSpinner(Spinner s, String[] vals) {
		if (s == null)
			return;

		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);  
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(spinnerAdapter);

		for (String v : vals)
			spinnerAdapter.add(v);

		s.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.getId() == spinner1.getId())
			setVisibility();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void setVisibility() {
		if (spinner1 == null)
			return;

		String s = (String) spinner1.getSelectedItem();
		spinner2.setVisibility(("Bravo").equals(s) ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_list, menu);
		return result;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (departed) {
			StaticUtils.toaster(this, R.string.popupMSG);
			departed = false;
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.statButton:
			Bundle b = new Bundle();
			b.putSerializable(TransferBucket.TRANSFER_KEY, new TransferBucket("Operational!"));
			nextScreen(SecondScreen.class,b);
			return true;
		case R.id.camButton:
			nextScreen(PhotoList.class);			
			return true;
		case R.id.mapScreen:
			//nextScreen(MapProto.class);
			return true;
		case R.id.contactButton:
			nextScreen(ContactScanner.class);
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void nextScreen(Class<?> next) {
		Intent i = new Intent(this,next);
		startActivity(i);
	}

	private void nextScreen(Class<?> next, Bundle extras) {
		if (extras.isEmpty()) nextScreen(next);

		Intent i = new Intent(this, next);
		i.putExtras(extras);
		startActivity(i);
	}

	protected void nextScreen(Class<?> next, int requestCode) {
		Intent i = new Intent(this, next);
		startActivityForResult(i, requestCode);
	}

	protected void nextScreen(Class<?> next, int requestCode, Bundle extras) {
		if (extras.isEmpty()) nextScreen(next,requestCode);

		Intent i = new Intent(this, next);
		i.putExtras(extras);
		startActivityForResult(i, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == CallbackScreen.REQUEST_ID) {
			Bundle extras = intent.getExtras();
			String toastMSG = extras == null ? "Callback Error" : 
				extras.getInt(CallbackScreen.CHECKBOX_COUNT) + " Boxes Checked";

			StaticUtils.toaster(this, toastMSG);
		}
	}
}