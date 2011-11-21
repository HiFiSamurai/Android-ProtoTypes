package com.proto.activities.maps;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.proto.db.GeoBean;

public class GeoSpot extends GeoBean {

	public GeoSpot() {}
	
	public GeoSpot(String name, int lat, int lng) {
		setName(name);
		setX(lat);
		setY(lng);
	}

	public OverlayItem getMapPoint() {
		return new OverlayItem(new GeoPoint(getX(),getY()), getName(), "");
	}
	
	public GeoSpot(Cursor c) {
		int[] i = super.Gen_columnIndices(c);
		super.Gen_populate(c, i);
	}

	@Override
	public String toString() {
		return String.format("X: %s\tY: %s\tName: %s", getX(),getY(),getName());
	}

	@Override
	public ContentValues Gen_getValues() {
		ContentValues vals = super.Gen_getValues();
		
		if (vals.getAsLong(GEN_FIELD__ID) == 0)
			vals.remove(GEN_FIELD__ID);
		return vals;
	}
}
