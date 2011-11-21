package com.proto.list;

import java.util.List;

import com.proto.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DynamicListAdapter extends ArrayAdapter<String> {

	private Context context;
	private int textViewResourceId;
	private List<String> vals;
	private ViewHolder holder;

	public DynamicListAdapter(Context context, int textViewResourceId,List<String> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.vals = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);

			holder = new ViewHolder();
			holder.captionText = (TextView) v.findViewById(R.id.dynamicItemLabel);
			v.setTag(holder);
		} else
			holder = (ViewHolder)convertView.getTag();
		
		holder.captionText.setText(vals.get(position));
		
		return v;
	}
	
	private static class ViewHolder {
		TextView captionText;
	}

}
