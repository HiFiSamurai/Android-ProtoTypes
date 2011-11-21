package com.proto.list;

import java.util.ArrayList;
import java.util.List;

import com.proto.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class DynamicListActivity extends Activity implements OnScrollListener {

	private ListView list;
	private List<String> v;
	private ProgressBar prog;

	/** The handler for thread posting. */
	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dynamic_list_layout);

		prog = (ProgressBar) findViewById(R.id.dynamicProgress);

		list = (ListView) findViewById(R.id.dynamicList);
		list.setOnScrollListener(this);

		v = new ArrayList<String>();
		for (int i=1; i<20; i++)
			v.add("Item #" + i);

		DynamicListAdapter adapter = new DynamicListAdapter(this, R.layout.dynamic_list_item, v);
		list.setAdapter(adapter);
	}

	// Dynamic List stuff
	private int listState;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (listState == SCROLL_STATE_TOUCH_SCROLL && firstVisibleItem + visibleItemCount >= totalItemCount)
			delay();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.listState = scrollState;
	}

	private boolean expanding = false;
	
	private void delay() {
		if (!expanding) {
			expanding = true;
			prog.setVisibility(View.VISIBLE);

			new Thread() {
				public void run() {
					try {
						sleep(1000);
						handler.post(loadingComplete);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	/** 
	 * Thread initiated after RSS data has been downloaded.
	 */
	private final Runnable loadingComplete = new Runnable() {
		public void run() {
			expanding = false;
			addItem();
		}
	};

	private void addItem() {
		DynamicListAdapter adapter = (DynamicListAdapter) list.getAdapter();
		prog.setVisibility(View.GONE);
		adapter.add("Item #" + String.valueOf(adapter.getCount()+1));
	}
}
