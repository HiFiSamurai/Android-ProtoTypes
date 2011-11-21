package com.proto.camera;

import java.io.File;
import java.util.List;

import com.proto.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class VideoList extends Activity implements OnItemClickListener {
	protected static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/proto/videos/";
	protected static final String VIDEO_EXTRA = "videoPath";
	protected static final String VIDEO_EXTENSION = ".mp4";

	private ListView list;
	private List<File> videos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.media_list_layout);
		this.setTitle("Videos");

		list = (ListView)findViewById(R.id.media_review_listview);
		list.setOnItemClickListener(this);
		this.registerForContextMenu(list);

		Button trigger = (Button)findViewById(R.id.media_review_new);
		trigger.setText("New Video");

		trigger.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(VideoList.this ,VideoActivity.class);
				startActivity(i);
			}
		});
		
		((ImageButton)findViewById(R.id.media_review_toggle)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(VideoList.this ,PhotoList.class);
				startActivity(i);
				VideoList.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getVideos();
	}

	private void getVideos() {
		// Create output folder, if not already present
		File f = new File(VIDEO_PATH);
		if (!f.exists())
			f.mkdirs();

		videos = MediaUtils.getFiles(f, VIDEO_EXTENSION);
		MediaListAdapter adapter = new MediaListAdapter(this, videos, VIDEO_EXTENSION);
		adapter.activity = this;	// Needed to generate cursor for creating thumb-nail
		list.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// Determine selected filename and open play-back screen
		Intent i = new Intent(this ,VideoPlayer.class);
		i.putExtra(VIDEO_EXTRA, videos.get(position).getAbsolutePath());
		startActivity(i);
	}

	//**************************************
	// Menu related items for long pressing on the listview

	private final int CONTEXTMENU_RENAMEITEM = 0;
	private final int CONTEXTMENU_DELETEITEM = 1;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Photo Menu");
		menu.add(0, CONTEXTMENU_RENAMEITEM, Menu.NONE, "Rename Video");
		menu.add(0, CONTEXTMENU_DELETEITEM, Menu.NONE, "Delete Video");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//determine which menu item was selected.
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

		switch (item.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			videos.get(info.position).delete();	// Delete selected photo
			getVideos();
			return true; /* true means: "we handled the event". */
		case CONTEXTMENU_RENAMEITEM:
			showRenameDialog(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void showRenameDialog(final int position) {
		final Dialog renameDialog = new Dialog(this);
		renameDialog.setCancelable(true);
		renameDialog.setTitle("Rename Photo");
		renameDialog.setContentView(R.layout.media_save_dialog);
		renameDialog.setCancelable(false);
		renameDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		final File f = videos.get(position);
		final EditText desc = ((EditText)renameDialog.findViewById(R.id.media_save_decription));
		desc.setText(f.getName().replace(VIDEO_EXTENSION, ""));		// set old filename into text field
		desc.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);	

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.media_save_savebutton) {
					String description = desc.getText().toString();
					String fileName = MediaUtils.getUsableFileName(VIDEO_PATH + description, VIDEO_EXTENSION);
					f.renameTo(new File(fileName));
					getVideos();		// re-fetch video list after rename

					renameDialog.dismiss();
				} else if (v.getId() == R.id.media_save_discardbutton)
					renameDialog.dismiss();
			}
		};

		((Button)renameDialog.findViewById(R.id.media_save_savebutton)).setOnClickListener(listener);
		((Button)renameDialog.findViewById(R.id.media_save_discardbutton)).setOnClickListener(listener);

		renameDialog.show();
	}
}
