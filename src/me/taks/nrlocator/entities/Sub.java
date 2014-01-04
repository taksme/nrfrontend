package me.taks.nrlocator.entities;

import java.util.ArrayList;
import java.util.Iterator;


import org.json.JSONArray;
import org.json.JSONException;

import android.database.DataSetObservable;
import android.util.Log;

public class Sub extends DataSetObservable implements Iterable<Report> {
	private static final int MAX_SIZE = 50;
	
	private Subs rs;
	private String filter;
	private ArrayList<Report> list = new ArrayList<Report>();
	
	public Locations getLocations() {
		return rs.getLocations();
	}

	public Sub(Subs rs, String filter, JSONArray data) {
		this(rs, filter);
		for (int len = data.length(), i=0; i<len; i++) try {
			list.add(new Report(this, data.getJSONObject(i)));
		} catch (JSONException e) {
			Log.e("Reports", "Failed to load a report", e);
		}
	}
	
	public Sub(Subs rs, String filter) {
		this.rs = rs;
		this.filter = filter;
		rs.put(filter, this);
	}
	
	public void push(Report report){
		list.add(0, report);
		while (list.size()>MAX_SIZE) list.remove(list.size()-1);
		updated();
	}
	
	public String toJSONString() {
		if (list.size()==0) return "[]";
		StringBuffer out = new StringBuffer("[");
		for (Report r : list) {
			out.append(r.toJSONString()+",");
		}
		out.replace(out.length()-1, out.length(), "]");
		return out.toString();
	}

	public void updated() {
		rs.updated();
		notifyChanged();
	}

	public Iterator<Report> iterator() {
		return list.iterator();
	}
	
	public Iterable<Report> unseen() {
		ArrayList<Report> out = new ArrayList<Report>();
		for (Report r : list) if (!r.isSeen()) out.add(r);
		return out;
	}
	
	public void seenAll() {
		for (Report r : this) {
			r.seen();
		}
	}
	
	public String getFilter() {
		return filter;
	}
	
	public int size() {
		return list.size();
	}
	
	public Report get(int index) {
		return list.get(index);
	}
}
