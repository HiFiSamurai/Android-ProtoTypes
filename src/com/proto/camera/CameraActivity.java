package com.proto.camera;

import java.io.File;
import java.io.FileOutputStream;

import com.proto.ProtoCore;
import com.proto.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

//most of the camera stuff is based on this code: http://marakana.com/forums/android/examples/39.html

public class CameraActivity extends Activity implements OnClickListener {
	private Dialog saveDialog;
	private CameraPreview preview;

	private boolean capturing = false;	//mutex to prevent simultaneous pictures
	private boolean dialogDisplayed = false;
	private String descText;

	private final String TEMP_NAME = PhotoList.PHOTO_PATH + "capture" + PhotoList.PHOTO_EXTENSION;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.photo_capture_layout);

		LayoutParams blah = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		preview = new CameraPreview(this);
		preview.setLayoutParams(blah);

		LinearLayout layout = (LinearLayout)this.findViewById(R.id.camera_preview_location);
		layout.addView(preview);

		// TODO: Hide button if hardware has 'camera' button (if check is possible)
		Button captureButton = (Button)findViewById(R.id.camera_capture_button);
		captureButton.setOnClickListener(this);

		if(savedInstanceState != null) {
			dialogDisplayed = savedInstanceState.getBoolean("dialogDisplayed", false);
			descText = savedInstanceState.getString("descText");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(dialogDisplayed) {
			// Photo Description -> File name
			String descText =((EditText)saveDialog.findViewById(R.id.media_save_decription)).getText().toString();
			outState.putString("descText", descText);
		}
		outState.putBoolean("dialogDisplayed", dialogDisplayed);
	}

	@Override
	public void onClick(View v) {
		takePhoto();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			return true;

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		/*	Override key presses to control camera from hardware buttons.
		 * 	Note the direction of Volume +/- versus Zoom In/Out. Camera activity
		 * 	must always be in Landscape mode, so it assumes a left/right orientation,
		 * 	as opposed to Up/Down.	*/
		switch(keyCode) {
		case KeyEvent.KEYCODE_CAMERA:
			takePhoto();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			preview.zoomOut();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			preview.zoomIn();
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	private void takePhoto() {
		if (!capturing) {
			capturing = true;
			preview.takePic(jpegCallback);
		}
	}

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap pic = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			// Set rotation of picture, based on screen orientation
			Matrix matrix = new Matrix();
			matrix.postRotate(OrientationListener.getOrientation());
			Bitmap rotatedPic = Bitmap.createBitmap(pic, 0, 0,
					pic.getWidth(), pic.getHeight(), matrix, true);

			FileOutputStream outStream = null;

			try {
				//save to a temporary file on the sd card
				outStream = new FileOutputStream(TEMP_NAME);
				rotatedPic.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
				outStream.close();

				Log.d(ProtoCore.TAG, "Picture taken - wrote " + data.length + " bytes to temporary file.");
				showSaveDialog();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void showSaveDialog() {
		dialogDisplayed = true;
		saveDialog = new Dialog(this);
		saveDialog.setCancelable(true);
		saveDialog.setTitle("Save Photo");
		saveDialog.setContentView(R.layout.media_save_dialog);
		saveDialog.setCancelable(false);
		saveDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		((Button)saveDialog.findViewById(R.id.media_save_savebutton)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				renamePhoto();
				restartCapture();
			}});
		((Button)saveDialog.findViewById(R.id.media_save_discardbutton)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				discardPhoto();
				restartCapture();
			}});

		final EditText desc = ((EditText)saveDialog.findViewById(R.id.media_save_decription));
		desc.setText(descText);
		desc.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		saveDialog.show();
	}

	private void restartCapture() {
		preview.startPreview();
		dialogDisplayed = false;
		capturing = false;
	}

	private void renamePhoto() {
		File photo = new File(TEMP_NAME);

		String description = ((EditText)saveDialog.findViewById(R.id.media_save_decription)).getText().toString();
		if (description.length() == 0)
			description = "photo";		// Default name if no description is entered
		
		String fileName = MediaUtils.getUsableFileName(PhotoList.PHOTO_PATH + description, PhotoList.PHOTO_EXTENSION);
		
		boolean success = photo.renameTo(new File(fileName));
		Log.i(ProtoCore.TAG, "Photo was " + ((success)?"":"not ") + "renamed sucessfully");
		saveDialog.dismiss();
	}

	private void discardPhoto() {
		new File(TEMP_NAME).delete();

		Log.i(ProtoCore.TAG, "Photo discarded.");
		saveDialog.dismiss();
	}

	@Override
	protected void onPause() {
		preview.stopPreview();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		preview.startPreview();
		if(dialogDisplayed)
			showSaveDialog();
	}
}