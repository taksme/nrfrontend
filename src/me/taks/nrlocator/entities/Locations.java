package me.taks.nrlocator.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import me.taks.nrlocator.App;
import me.taks.nrlocator.GCM;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

@SuppressWarnings("serial")
public class Locations extends ArrayList<Location> {
	protected class Index extends Hashtable<String, Location> {}
	protected Index byStanox = new Index();
	protected Index byTiploc = new Index();

	private static Locations locations;
    public static Locations instance() {
    	return locations==null ? locations = new Locations() : locations; 
    }
	
	public Locations() {
		final File locs = new File(App.cx.getFilesDir(), "loc");
		try {
			add(new JSONArray(new BufferedReader(new FileReader(locs)).readLine()));
			Log.i("Locations", "Read "+size()+" locations from file");
		} catch (Exception e) {
			new AsyncTask<Void, Void, String>() {
				protected String doInBackground(Void... params) {
			        HttpClient httpclient = new DefaultHttpClient();
			        HttpGet get = new HttpGet("http://"+GCM.SERVER+"/locations");

			        try {
			            String json = httpclient.execute(get, new BasicResponseHandler());
						add(new JSONArray(json));
						FileWriter f = new FileWriter(locs);
						f.write(json);
						f.close();
						Log.i("Locations", "Downloaded "+size()+" locations");
			            return "";

			        } catch (Exception e) {
			        	Log.e("Locations", "Downloading location data", e);
			        	return "";
			        }
				}
				protected void onPostExecute(String msg) {}
			}.execute(null, null, null);
		}
	}
	
	public Location getByStanox(String stanox) { 
		return byStanox.get(stanox); 
	}
	
	public Location getByTiploc(String tiploc) { 
		return byTiploc.get(tiploc); 
	}
	
	public void add(JSONArray array) {
		for (int len=array.length(), i=0; i<len; i++) {
			try {
				add(new Location(this, array.getJSONObject(i)));
			} catch (JSONException je) {
				Log.e("Locations", "Failed to parse a location "+je.getMessage());
			}
		}
	}
	
	public List<Location> startWith(String start) {
		start = start.toLowerCase();
		ArrayList<Location> out = new ArrayList<Location>();
		for (Location l : this) {
			if (l.getTla().toLowerCase().startsWith(start)
				|| l.getDescription().toLowerCase().startsWith(start)
				|| l.getStanox().startsWith(start)
			)
			out.add(l);	
		}
		return out;
	}
}
