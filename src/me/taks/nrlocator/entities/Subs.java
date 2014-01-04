package me.taks.nrlocator.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import me.taks.nrlocator.App;

import org.json.JSONObject;

import android.database.DataSetObservable;
import android.util.Log;

public class Subs extends DataSetObservable implements Iterable<Entry<String, Sub>> {
	private Locations locs;
	private Hashtable<String, Sub> table = new Hashtable<String, Sub>();
	private ArrayList<Sub> list = new ArrayList<Sub>();
	
	public Locations getLocations() {
		return locs;
	}
	
	private static Subs rs;
    public static Subs instance() {
    	return rs==null ? rs = new Subs(Locations.instance()) : rs; 
    }
	
	public Subs(Locations locs) {
		this.locs = locs;
		inTransaction = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader(getFile()));
			JSONObject o = new JSONObject(br.readLine());
			br.close();
			@SuppressWarnings("rawtypes")
			Iterator i = o.keys();
			while (i.hasNext()) {
				String key = (String)(i.next());
				new Sub(this, key, o.getJSONArray(key));
			}
			Log.i("Reports", "Read "+table.size()+" reports from file "+getFile().getAbsolutePath());
		} catch (Exception e) {
			Log.e("Reports", "Reading from file", e);
		}
		inTransaction = false;
	}
	
	public void put(String filter, Sub sub) {
		table.put(filter, sub);
		list.add(sub);
		updated();
	}
	
	public void remove(String filter) {
		list.remove(table.remove(filter));
		updated();
	}
	
	public Sub get(String filter) {
		return table.get(filter);
	}
	
	public Sub get(int index) {
		return list.get(index);
	}
	
	public List<Sub> list() {
		return list;
	}
	
	private File getFile() {
		return new File(App.cx.getFilesDir(), "rep");
	}
	
	private void store() {
		String str = toJSONString();
		try {
			FileWriter f = new FileWriter(getFile());
			f.write(str);
			Log.i("Reports", "Storing "+str.length()+" chars to "+getFile().getAbsolutePath());
			f.close();
		} catch (IOException ie) {
			Log.e("ReportsSet", "Failed to store", ie);
		}
	}
	
	public String toJSONString() {
		if (list.size()==0) return "{}";
		StringBuffer out = new StringBuffer("{");
		for (Sub sub : list) {
			out.append(String.format(
				"%s:%s,",
				JSONObject.quote(sub.getFilter()),
				sub.toJSONString()
			));
		}
		out.replace(out.length()-1, out.length(), "}");
		return out.toString(); 
	}

	private boolean inTransaction = false;
	
	public void updated() {
		if (!inTransaction) store();
		notifyChanged();
	}

	public void seenAll() {
		inTransaction = true;
		for (Entry<String, Sub> e : this) e.getValue().seenAll();
		inTransaction = false;
		store();
	}
	
	public Iterator<Entry<String, Sub>> iterator() {
		return table.entrySet().iterator();
	}
}
