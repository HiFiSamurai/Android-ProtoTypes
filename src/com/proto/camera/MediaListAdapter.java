package com.proto.camera;

import java.io.File;
import java.util.List;

import com.proto.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MediaListAdapter extends ArrayAdapter<File> {

	private static final int textViewResourceId = R.layout.media_listitem;
	private Context context;
	private List<File> files;
	private ViewHolder holder;
	private String extension;
	
	protected Activity activity;
	
	public MediaListAdapter(Context context, List<File> files, String extension) {
		super(context, textViewResourceId, files);
		this.context = context;
		this.files = files;
		this.extension = extension;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);

			holder = new ViewHolder();
			holder.captionText = (TextView) v.findViewById(R.id.media_listitem_text);
			holder.thumbnail = (ImageView) v.findViewById(R.id.media_listitem_thumb);
			v.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();

		File f = files.get(position);

		holder.captionText.setText(f.getName().replace(extension, ""));
		if (extension.equals(PhotoList.PHOTO_EXTENSION))
			holder.thumbnail.setImageBitmap(MediaUtils.getThumbnail(f));
		/*else if (extension.equals(VideoList.VIDEO_EXTENSION) && activity != null)
			holder.thumbnail.setImageBitmap(PhotoUtils.getScreenCap(f, activity));*/

		return v;
	}
	
	private static class ViewHolder {
		TextView captionText;	
		ImageView thumbnail;
	}
}
