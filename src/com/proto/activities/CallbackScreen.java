package com.proto.activities;

import java.util.ArrayList;

import com.proto.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;

public class CallbackScreen extends Activity {

	public static final String CHECKBOX_COUNT = "CB Count";
	public static final int REQUEST_ID = 0;
	private ArrayList<CheckBox> boxGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callback_screen);
		
		if (boxGroup == null)
			setupBoxGroup();
	}

	/* This is a method for placing grouped UI Elements into an ArrayList.
	 * NOTE: This class parameter will be volatile, in that it will be cleared every time
	 * the UI is altered. If you serialize/save it through on SaveInstanceState() it might 
	 * be quicker than reconstructing the array everytime.*/
	private void setupBoxGroup() {
		LinearLayout checkContainer = (LinearLayout) findViewById(R.id.checkBoxGroup);
		if (checkContainer == null) return;
		
		boxGroup = new ArrayList<CheckBox>();
		for (int i=0; i < checkContainer.getChildCount(); i++)
			boxGroup.add((CheckBox) checkContainer.getChildAt(i));
	}
	
	@Override
	public void finish() {
		Intent i = new Intent();
		i.putExtra(CHECKBOX_COUNT, getCheckCount());
		
		setResult(RESULT_OK, i);
		
		super.finish();
	}
	
	private int getCheckCount() {
		int x = 0;
		
		for (CheckBox c : boxGroup) {
			if (c.isChecked())
				x++;
		}
		
		return x;
	}

}
