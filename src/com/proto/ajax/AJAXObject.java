package com.proto.ajax;

import org.json.JSONObject;

import com.androidquery.util.XmlDom;

public interface AJAXObject {
	
	public void buildFromJSON(JSONObject json);
	
	public void buildFromXML(XmlDom xml);
	
	public String[] getTag();
}
