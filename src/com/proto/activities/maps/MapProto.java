package com.proto.activities.maps;

import java.util.List;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.proto.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;

public class MapProto extends MapActivity {

	protected LocationManager location;
	protected MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_proto);

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Add zoom controls to map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		// Configures map to accept overlayed icons, of different types
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable ninja = this.getResources().getDrawable(R.drawable.droidninja);
		ProtoOverlay ninjaOverLay = new ProtoOverlay(ninja);

		ninjaOverLay.addOverlay(new GeoSpot("Somewhere", 23428, 18909).getMapPoint());
		ninjaOverLay.addOverlay(new GeoSpot("Unknown", 4323, 88990).getMapPoint());
		mapOverlays.add(ninjaOverLay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		MapController mc = mapView.getController(); 
		switch (keyCode) 
		{
		case KeyEvent.KEYCODE_3:
			mc.zoomIn();
			break;
		case KeyEvent.KEYCODE_1:
			mc.zoomOut();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
