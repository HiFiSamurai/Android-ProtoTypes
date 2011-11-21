package com.proto.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;

public class Chart extends AbstractChartBase {

	private String name;
	private String xAxis;
	private String yAxis;
	
	private List<XYSeries> series;
	
	public String getName() {
		return name;
	}

	public String getXLabel() {
		return xAxis;
	}

	public String getYLabel() {
		return yAxis;
	}
	
	public Chart(String name, String xAxis, String yAxis) {
		super();
		this.name = name;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		
		series = new ArrayList<XYSeries>();
	}

	public void addSeries(String name, List<Double> x, List<Double> y) {
		series.add(ChartUtils.buildSeries(name,x, y));
	}
	
	public Intent buildLineChart(Context context) {
		int n = series.size();
		int[] colors = new int[n];
		PointStyle[] styles = new PointStyle[n];
		
		for (int i = 0; i < n; i++) {
			colors[i] = ChartUtils.SERIES_COLORS[i % ChartUtils.SERIES_COLORS.length];
			styles[i] = ChartUtils.SERIES_SHAPES[i % ChartUtils.SERIES_SHAPES.length];
		}
		
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    
	    renderer.setChartTitle(name);
	    renderer.setXTitle(xAxis);
	    renderer.setYTitle(yAxis);
	    ChartUtils.configureAxes(renderer, series);

	    XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
	    for (XYSeries s : series)
	    	data.addSeries(s);

	    Intent intent = ChartFactory.getLineChartIntent(context, data, renderer, name);
		return intent;
	}
	
	public Intent buildScatterChart(Context context) {
		int n = series.size();
		int[] colors = new int[n];
		PointStyle[] styles = new PointStyle[n];
		
		for (int i = 0; i < n; i++) {
			colors[i] = ChartUtils.SERIES_COLORS[i % ChartUtils.SERIES_COLORS.length];
			styles[i] = ChartUtils.SERIES_SHAPES[i % ChartUtils.SERIES_SHAPES.length];
		}
		
	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	    
	    renderer.setChartTitle(name);
	    renderer.setXTitle(xAxis);
	    renderer.setYTitle(yAxis);
	    ChartUtils.configureAxes(renderer, series);

	    XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
	    for (XYSeries s : series)
	    	data.addSeries(s);

	    Intent intent = ChartFactory.getScatterChartIntent(context, data, renderer, name);
		return intent;
	}
}
