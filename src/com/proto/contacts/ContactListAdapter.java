package com.proto.contacts;

import java.util.List;

import com.proto.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContactListAdapter extends ArrayAdapter<String> {

	private static final int textViewResourceId = R.layout.email_list_item;
	
	private Context context;
	private List<String> vals;
	private ViewHolder holder;
	
	public ContactListAdapter(Context context,List<String> objects) {
		super(context, textViewResourceId, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);

			holder = new ViewHolder();
			holder.captionText = (TextView) v.findViewById(R.id.listLabel);
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
