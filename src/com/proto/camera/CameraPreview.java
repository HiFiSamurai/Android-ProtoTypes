package com.proto.camera;

//most of the camera stuff is based on this code: http://marakana.com/forums/android/examples/39.html

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

class CameraPreview extends SurfaceView implements Callback {

	private SurfaceHolder mHolder;
	private Camera camera;

	CameraPreview(Context context) {
		super(context);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();

		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	// Called once the holder is ready
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where to draw.
		camera = Camera.open();

		try {
			camera.setPreviewDisplay(holder);  // <9>

			camera.setPreviewCallback(new PreviewCallback() { 
				// Called for each frame previewed
				public void onPreviewFrame(byte[] data, Camera camera) {
					CameraPreview p = CameraPreview.this;
					p.invalidate();					
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Called when the holder is destroyed
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	// Called when holder has changed
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		camera.startPreview();
	}
	
	protected void takePic(PictureCallback callback) {
		if(camera!= null)
			camera.takePicture(null, null, callback);
	}

	protected void startPreview(){
		if(camera!= null)
			camera.startPreview();
	}

	protected void stopPreview(){
		if(camera != null)
			camera.stopPreview();
	}

	protected void zoomIn() {
		if(camera != null)
			MediaUtils.cameraZoom(camera, 1);
	}

	protected void zoomOut() {
		if(camera != null)
			MediaUtils.cameraZoom(camera, -1);
	}
}