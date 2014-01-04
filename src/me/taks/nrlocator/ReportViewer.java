package me.taks.nrlocator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.taks.nrlocator.entities.Location;
import me.taks.nrlocator.entities.Report;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

class ReportViewer {
	public static ReportViewer get(Report report) {
		if (report==null) return new ReportViewer();
		else return new RealReportViewer(report);
	}
	
	public static String titleCase(String in) {
		StringBuffer str = new StringBuffer(in.toLowerCase());
		for (int i=0,j=0; i<str.length() && j>=0; i=(j=str.indexOf(" ", i))+1) {
			if (i<=str.length()) str.setCharAt(i, Character.toUpperCase(str.charAt(i)));
		}
		return str.toString();
	}
	
	public String getLocation() { return "Unknown"; }
	
	public String getNext() { return "Unknown"; }
	
	public String getDirection() { return ""; }
	
	public String getEvent() { return ""; }
	
	public String getPerformance() {
		return getPerformance("early", "late", "on time");
	}
	public String getPerformance(String early, String late, String onTime) {
		return "";
	}
	
	public String getHeadcode() { return "xxxx"; }

	public String getExpectedTime() { return ""; }
	
	public String getSummary() {
		return String.format("%s\t%s%s %s at %s\n%s for %s",
				getExpectedTime(), getDirection(), getEvent(), 
				getPerformance("E", "L", ""), getLocation(),
				getHeadcode(), getNext());
	}
	
	public String getSummaryNoLoc() {
		return String.format("%s\t%s%s %s %s for %s",
				getExpectedTime(), getDirection(), getEvent(), 
				getPerformance("E", "L", ""),
				getHeadcode(), getNext());
	}
	
	protected static TextView getTextView(Context cx, TextView reuse, String text) {
		if (reuse==null) reuse = new TextView(cx);
		reuse.setText(text);
		reuse.setPadding(16, 16, 16, 16);
		reuse.setTextSize(24);
		reuse.setTextColor(Color.GRAY);
		return reuse;
	}
	
	public TextView getLocTextView(Context cx, TextView reuse) {
		return getTextView(cx, reuse, getSummary());
	}
	
	public TextView getTrainTextView(Context cx, TextView reuse) {
		return getTextView(cx, reuse, getSummaryNoLoc());
	}
}

class RealReportViewer extends ReportViewer {
	private Report report;
	
	public RealReportViewer(Report report) {
		this.report = report;
	}
	
	public String getLocation() { //should probably be a location renderer
		Location l = report.getLocation();
		return l!=null ? titleCase(l.getDescription()) : "";
	}
	
	public String getNext() { //should probably be a location renderer
		Location l = report.getNext();
		return l!=null ? titleCase(l.getDescription()) : "";
	}
	
	public String getDirection() {
		switch (report.getDirection()) {
		case UP: return "⬆";
		case DOWN: return "⬇";
		default: return "";
		}
	}
	
	public String getEvent() {
		switch (report.getEvent()) {
		case ARRIVAL: return "arr";
		case DEPARTURE: return "dep";
		default: return "";
		}
	}
	
	public String getPerformance(String early, String late, String onTime) {
		return report.getTimes().getMinutesAndQuarters(early, onTime, late);
	}
	
	public String getHeadcode() {
		return report.getTrainId()!=null ? report.getTrainId().substring(2,6) : "xxxx";
	}
	
    private static SimpleDateFormat dateForm = new SimpleDateFormat("HH:mm", Locale.UK);
	public String getExpectedTime() {
		return dateForm.format(new Date(report.getTimes().getStart()));
	}
}
