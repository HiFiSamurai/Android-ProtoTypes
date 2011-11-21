package com.proto.chart;

import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;

public class ChartUtils {

	public static final int[] SERIES_COLORS = {Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.YELLOW};
	
	public static final PointStyle[] SERIES_SHAPES = {PointStyle.CIRCLE, PointStyle.DIAMOND,
        PointStyle.TRIANGLE, PointStyle.SQUARE, PointStyle.POINT};
	
	public static XYSeries buildSeries(String name, List<Double> x, List<Double> y) {
		if (x.size() != y.size())
			throw new RuntimeException("Chart Error: List lengths do not match!");
		
		XYSeries series = new XYSeries(name);
		for (int i = 0; i < x.size(); i++)
			series.add(x.get(i), y.get(i));
		
		return series;
	}
	
	public static void configureAxes(XYMultipleSeriesRenderer renderer, List<XYSeries> series) {
		double xMin = Double.MAX_VALUE;
		double xMax = -Double.MAX_VALUE;
		double yMin = Double.MAX_VALUE;
		double yMax = -Double.MAX_VALUE;

		// Find boundaries of chart points
		for (XYSeries s : series) {
				xMin = Math.min(xMin, s.getMinX());
				xMax = Math.max(xMax, s.getMaxX());
				yMin = Math.min(yMin, s.getMinY());
				yMax = Math.max(yMax, s.getMaxY());
		}
		
		renderer.setXAxisMin(xMin);
	    renderer.setXAxisMax(xMax);
	    renderer.setYAxisMin(yMin);
	    renderer.setYAxisMax(yMax);
	    
	    // Set axis boundaries to max/min +/- 20% of difference
	    double deltaX = (xMax - xMin)*0.2;
	    double deltaY = (yMax - yMin)*0.2;
	    double[] boundaries = {(xMin-deltaX), (xMax+deltaX), (yMin-deltaY), (yMax+deltaY)};
	    
	    renderer.setPanLimits(boundaries);
	    renderer.setZoomLimits(boundaries);
	}
}
