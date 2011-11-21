package com.proto.camera;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;

public class CameraDetailedReviewActivity extends Activity{
	private final String IMAGE_FILENAME = "temp.jpg";
	private File imageFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String path = getIntent().getExtras().getString(PhotoList.PHOTO_EXTRA);
		File photo = new File(path); 
		if(!(photo.exists()))
			throw new RuntimeException("Photo Does not exist: " + path);
					
		String filename = PhotoList.PHOTO_PATH + IMAGE_FILENAME;
		
		try {
			imageFile = new File(filename);
			FileOutputStream out = new FileOutputStream(imageFile);
			Bitmap image = MediaUtils.getWebPreview(photo);
			image.compress(Bitmap.CompressFormat.JPEG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}

		WebView webview = new WebView(this);
		webview.loadUrl("file://" + filename);
		this.setContentView(webview);
	}

	@Override
	public void finish() {		// Delete the temporary file
		if (imageFile != null && imageFile.exists())
			imageFile.delete();
		super.finish();
	}
}
