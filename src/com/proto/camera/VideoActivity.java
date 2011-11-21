package com.proto.camera;

import java.io.File;

import com.proto.ProtoCore;
import com.proto.R;
import com.proto.util.StaticUtils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class VideoActivity extends Activity implements Callback, OnClickListener {
	
	private MediaRecorder recorder;
	private SurfaceHolder holder;
	private Button trigger;
	private boolean recording = false;

	private Camera camera;
	
	private Dialog saveDialog;
	private boolean dialogDisplayed = false;
	private String descText;

	private final String TEMP_NAME = VideoList.VIDEO_PATH + "videoCapture" + VideoList.VIDEO_EXTENSION;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.video_capture_layout);

		SurfaceView preview = (SurfaceView) findViewById(R.id.video_preview_location);

		camera = Camera.open();

		holder = preview.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		recorder = new MediaRecorder();

		trigger = (Button) findViewById(R.id.video_capture_button);
		trigger.setOnClickListener(this);
	}

	private void initRecorder() {
		camera.unlock();
		recorder.setCamera(camera);

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

		//recorder.setVideoSize(288, 288);
		recorder.setVideoSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
		recorder.setVideoFrameRate(20);
		recorder.setMaxDuration(50000); // 50 seconds
		recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
		recorder.setOutputFile(TEMP_NAME);
		recorder.setPreviewDisplay(holder.getSurface());
		
		try {
			recorder.prepare();
		} catch (Exception e) {
			StaticUtils.toaster(ProtoCore.getContext(), "Couldn't prepare recorder");
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initRecorder();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (recording) {
			recorder.stop();
			recording = false;
		}
		finish();
	}

	@Override
	public void onClick(View arg0) {
		if (recording) {
			recorder.stop();
			camera.lock();
			recording = false;
			showSaveDialog();
			
			trigger.setText("Capture");
			trigger.setBackgroundColor(Color.GRAY);
		} else {
			recording = true;
			recorder.start();
			trigger.setText("Stop");
			trigger.setBackgroundColor(Color.RED);
		}
	}

	private void restartVideo() {
		// Let's initRecorder so we can record again
		initRecorder();
	}
	
	@Override
	public void finish() {
		recorder.release();
		camera.release();
		new File(TEMP_NAME).delete();		// Delete preview file when closing
		
		super.finish();
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
				renameVideo();
				restartVideo();
			}});
		((Button)saveDialog.findViewById(R.id.media_save_discardbutton)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				discardVideo();
				restartVideo();
			}});

		final EditText desc = ((EditText)saveDialog.findViewById(R.id.media_save_decription));
		desc.setText(descText);
		desc.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		saveDialog.show();
	}
	
	private void renameVideo() {
		File photo = new File(TEMP_NAME);

		String description = ((EditText)saveDialog.findViewById(R.id.media_save_decription)).getText().toString();
		if (description.length() == 0)
			description = "video";		// Default name if no description is entered
		
		String fileName = MediaUtils.getUsableFileName(VideoList.VIDEO_PATH + description, VideoList.VIDEO_EXTENSION);
		
		boolean success = photo.renameTo(new File(fileName));
		Log.i(ProtoCore.TAG, "Video was " + ((success)?"":"not ") + "renamed sucessfully");
		saveDialog.dismiss();
	}

	private void discardVideo() {
		new File(TEMP_NAME).delete();

		Log.i(ProtoCore.TAG, "Video discarded.");
		saveDialog.dismiss();
	}
}
