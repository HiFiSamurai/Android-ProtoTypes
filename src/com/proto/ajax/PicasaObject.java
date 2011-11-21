package com.proto.ajax;

import org.json.JSONObject;

import com.androidquery.util.XmlDom;

public class PicasaObject implements AJAXObject {

	public String id;
	public String title;
	public String updated;
	public String link;
	
	@Override
	public void buildFromJSON(JSONObject json) {}

	@Override
	public void buildFromXML(XmlDom xml) {
		this.title = xml.text("title");
		this.id = xml.text("id");
		this.updated = xml.text("updated");	
	}

	@Override
	public String[] getTag() {
		return new String[]{"entry"};
	}
}
