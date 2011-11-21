package com.proto.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.provider.MediaStore;

public class MediaUtils {
	protected static String getUsableFileName(String fileName, String extension) {
		File f = new File(fileName + extension);
		if (f.exists())
			return getUsableFileName(fileName, extension, 2);

		return fileName + extension;
	}

	private static String getUsableFileName(String fileName, String extension, int i) {
		File f = new File(fileName + "-" + i + extension);
		if (f.exists())
			return getUsableFileName(fileName, extension, i+1);

		return f.getAbsolutePath();
	}

	protected static List<File> getFiles(File dir, String extension) {
		List<File> files = new ArrayList<File>();

		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(extension))
				files.add(f);
		}

		return files;
	}

	protected static Bitmap getThumbnail(File f) {
		Options thumbOptions = new Options();
		thumbOptions.inSampleSize = 8; 

		return getImage(f, thumbOptions);
	}

	protected static Bitmap getWebPreview(File f) {
		return getImage(f, null);
	}

	private static Bitmap getImage(File f,Options options) {
		Bitmap image = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		return image;
	}

	public static Bitmap getScreenCap(File f, Activity activity) {
		String filePath = f.getAbsolutePath();
		String[] proj = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATA };
		Cursor cursor = activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
				proj, MediaStore.Video.Media.DISPLAY_NAME+"=?",new String[] {filePath}, null);
		cursor.moveToFirst();
		long fileID = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		ContentResolver crThumb = activity.getContentResolver();
		Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb,fileID, 
				MediaStore.Video.Thumbnails.MICRO_KIND, options);

		return curThumb;
	}

	private static final int MIN_ZOOM = 0;
	private static final int MAX_ZOOM = 6;

	protected static void cameraZoom(Camera cam, int dir) {
		Parameters p = cam.getParameters();
		String zoomSupportedString = p.get("zoom-supported");
		if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString))
			return;

		int zoom = getZoom(p);
		p.set("zoom", adjustZoom(zoom + dir));

		cam.setParameters(p);
	}

	private static int getZoom(Parameters p) {
		try {
			return Integer.valueOf(p.get("zoom"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static int adjustZoom(int zoom) {
		if (zoom < MIN_ZOOM)
			return MIN_ZOOM;
		else if (zoom > MAX_ZOOM)
			return MAX_ZOOM;

		return zoom;
	}
}
