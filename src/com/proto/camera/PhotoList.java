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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class PhotoList extends Activity implements OnItemClickListener, OnCreateContextMenuListener {

	protected static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/proto/photos/";
	protected static final String PHOTO_EXTRA = "photoPath";
	protected static final String PHOTO_EXTENSION = ".jpg";
	
	private ListView list;
	private List<File> photos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.media_list_layout);
		this.setTitle("Photos");

		list = (ListView)findViewById(R.id.media_review_listview);
		list.setOnItemClickListener(this);
		this.registerForContextMenu(list);

		Button trigger = (Button)findViewById(R.id.media_review_new);
		trigger.setText("New Photo");
		
		trigger.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PhotoList.this ,CameraActivity.class);
				startActivity(i);
			}
		});
		
		((ImageButton)findViewById(R.id.media_review_toggle)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i = new Intent(PhotoList.this ,VideoList.class);
				startActivity(i);
				PhotoList.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPhotos();
	}

	private void getPhotos() {
		// Create output folder, if not already present
		File f = new File(PHOTO_PATH);
		if (!f.exists())
			f.mkdirs();

		photos = MediaUtils.getFiles(f, PHOTO_EXTENSION);
		MediaListAdapter adapter = new MediaListAdapter(this, photos, PHOTO_EXTENSION);
		list.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// Determine selected filename and open preview
		Intent i = new Intent(this ,CameraDetailedReviewActivity.class);
		i.putExtra(PHOTO_EXTRA, photos.get(position).getAbsolutePath());
		startActivity(i);
	}

	//**************************************
	// Menu related items for long pressing on the listview

	private final int CONTEXTMENU_RENAMEITEM = 0;
	private final int CONTEXTMENU_DELETEITEM = 1;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Photo Menu");
		menu.add(0, CONTEXTMENU_RENAMEITEM, Menu.NONE, "Rename Photo");
		menu.add(0, CONTEXTMENU_DELETEITEM, Menu.NONE, "Delete Photo");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//determine which menu item was selected.
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();

		switch (item.getItemId()) {
		case CONTEXTMENU_DELETEITEM:
			photos.get(info.position).delete();	// Delete selected photo
			getPhotos();
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

		final File f = photos.get(position);
		final EditText desc = ((EditText)renameDialog.findViewById(R.id.media_save_decription));
		desc.setText(f.getName().replace(PHOTO_EXTENSION, ""));		// set old filename into text field
		desc.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);	

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.media_save_savebutton) {
					String description = desc.getText().toString();
					String fileName = MediaUtils.getUsableFileName(PhotoList.PHOTO_PATH + description, PHOTO_EXTENSION);
					f.renameTo(new File(fileName));
					getPhotos();		// re-fetch photo list after rename

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
