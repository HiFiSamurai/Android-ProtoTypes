package com.proto.ajax;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;

@SuppressWarnings({ "unused", "unchecked" })
public class AJAXHandler {

	private Context context;
	private Runnable callback;

	public JSONObject jsonPayload;
	public XmlDom xmlPayload;

	public AJAXHandler(Context context) {
		this.context = context;
	}

	public void fetchXML(String url, Runnable callback) {
		this.callback = callback;
		AQuery aq = new AQuery(context);
		aq.ajax(url, XmlDom.class, this, "XMLCallback");
	}

	public void XMLCallback(String url, XmlDom xml, AjaxStatus status) {
		xmlPayload = xml;
		new Handler().post(callback);
	}

	public void fetchJSON(String url, Runnable callback) {
		this.callback = callback;
		AQuery aq = new AQuery(context);
		aq.ajax(url, JSONObject.class, this, "JSONCallback");
	}

	public void JSONCallback(String url, JSONObject json, AjaxStatus status) {
		jsonPayload = json;
		new Handler().post(callback);
	}
	
	public static <T extends AJAXObject> List<T> getVals(Class<? extends AJAXObject> T, XmlDom xml) {
		List<T> l = new ArrayList<T>();
		
		try {
			String tags[] = T.newInstance().getTag();
			int last = tags.length-1;
			for (int i=0; i<last; i++)
				xml = xml.tag(tags[i]);
			
			List<XmlDom> entries = xml.tags(tags[last]);
			
			for (XmlDom entry: entries) {
				T item = (T) T.newInstance();
				item.buildFromXML(entry);
				l.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}
	
	public static <T extends AJAXObject> List<T> getVals(Class<? extends AJAXObject> T, JSONObject json) {
		List<T> l = new ArrayList<T>();
		
		try {
			String tags[] = T.newInstance().getTag();
			int last = tags.length-1;
			for (int i=0; i<last; i++)
				json = json.getJSONObject(tags[i]);
			
			JSONArray entries = json.getJSONArray(tags[last]);
			
			for (int i=0; i<entries.length(); i++) {
				T item = (T) T.newInstance();
				item.buildFromJSON(entries.getJSONObject(i));
				l.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return l;
	}
}
