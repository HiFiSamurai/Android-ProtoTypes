package com.proto.activities.maps;

import hifi.db.DBAdapter;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.proto.ProtoCore;
import com.proto.util.StaticUtils;

/**
 * The Class ProtoOverlay.
 */
public class ProtoOverlay extends ItemizedOverlay<OverlayItem> {

	/** The m overlays. */
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	/** The marker. */
	private Drawable marker;
	
	/**
	 * Instantiates a new proto overlay.
	 *
	 * @param defaultMarker the default marker
	 */
	public ProtoOverlay(Drawable defaultMarker) {		
		super(boundCenterBottom(defaultMarker));
		marker = defaultMarker;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#createItem(int)
	 */
	@Override
	protected OverlayItem createItem(int i) {
		  return mOverlays.get(i);
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#size()
	 */
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	/**
	 * Adds the overlay.
	 *
	 * @param overlay the overlay
	 */
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
        	//mapItemHit(mapView, event);
        	storePoint(event);
        }
		
		return super.onTouchEvent(event, mapView);
	}

	private void storePoint(MotionEvent event) {
		ProtoCore app = ProtoCore.getContext();
		GeoSpot spot = new GeoSpot("Sceen Touch", Math.round(event.getX()), Math.round(event.getY()));
		
		DBAdapter db = new DBAdapter(app);
		db.replace(spot);
		
		StaticUtils.toaster(app, "Poke!");
	}
	
	/**
	 * Determines if a map click was centered on an over-laid item.
	 * Uses the mapView dimensions, the center GeoPoint, and the OverLayItem coordinates
	 * to math-e-magically calculate the X,Y screen coordinates for the item, and then determines 
	 * whether the screen touch was close enough to this point to have hit the icon.  
	 *
	 * @param mapView The map being clicked.
	 * @param event The event triggered by the touch.
	 */
	protected void mapItemHit(MapView mapView, MotionEvent event) {
    	double mapW = mapView.getWidth();
    	double mapLong = mapView.getLongitudeSpan();
    	double xRatio = mapW/mapLong;
    	
    	double mapH = mapView.getHeight();
    	double mapLat = mapView.getLatitudeSpan();
    	double yRatio = mapH/mapLat;
    	
    	GeoPoint center = mapView.getMapCenter();
		
		for (OverlayItem i : mOverlays) {
			GeoPoint g = i.getPoint();
			
			/*	Gets the X,Y coordinates for the over-laid item from its' Geo coordinates.
			 * 	Note that the Y position is in the opposite direction from the center than 
			 * 	the X position, due to differences in 'positive' direction for lat and long */
			double itemX = (mapW/2) + (g.getLongitudeE6()-center.getLongitudeE6())*xRatio;
			double itemY = (mapH/2) - (g.getLatitudeE6()-center.getLatitudeE6())*yRatio;

			double iconRangeX = marker.getIntrinsicWidth()/2;
			double iconRangeY = marker.getIntrinsicHeight()/2;

			/* TODO: Y hit range is imprecise. Icon is centered horizontally at x coordinate,
			 * but the y coordinate represents the bottom of the image. Comparison logic/iconRangeY
			 * needs to be adjusted accordingly, or the icon placement setting will need to be changed. */
			 
			if (Math.abs(itemX-event.getX()) < iconRangeX && Math.abs(itemY-event.getY()) < iconRangeY) {
				Log.d("Maps", "Bullseye: " + i.getTitle());
				break;
			}
		}
	}
	
}
