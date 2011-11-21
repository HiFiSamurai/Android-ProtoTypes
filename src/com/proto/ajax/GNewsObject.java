package com.proto.ajax;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.util.XmlDom;

public class GNewsObject implements AJAXObject {

	public String title;
	public String url;
	
	@Override
	public void buildFromJSON(JSONObject json) {
		try {
			this.title = json.getString("titleNoFormatting");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void buildFromXML(XmlDom xml) {

	}

	@Override
	public String[] getTag() {
		return new String[]{"responseData","results"};
	}
}
