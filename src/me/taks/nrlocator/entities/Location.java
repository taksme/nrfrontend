package me.taks.nrlocator.entities;


import org.json.JSONException;
import org.json.JSONObject;

public class Location {
	/**
	 * 
	 */
	private final Locations locations;

	/**
	 * @param locations
	 */
	Location(Locations locations) {
		this.locations = locations;
	}
	
	Location(Locations locations, JSONObject o) throws JSONException {
		this(locations);
		stanox = o.getString("id");
		tla = o.getString("tla");
		tiploc = o.getString("tiploc");
		shortDescription = o.getString("sDesc");
		description = o.getString("desc");
		pos = new Point(o.getInt("e"), o.getInt("n"));
		locations.byStanox.put(stanox, this);
		locations.byTiploc.put(tiploc, this);
	}

	public String getStanox() {
		return stanox;
	}
	
	public void setStanox(String stanox) {
		if (null!=this.stanox) {
			this.locations.byStanox.remove(this.stanox);
		}
		this.stanox = stanox;
		this.locations.byStanox.put(this.stanox, this);
	}
	
	public String getTla() {
		return tla;
	}
	
	public void setTla(String tla) {
		this.tla = tla;
	}
	
	public String getTiploc() {
		return tiploc;
	}
	
	public String getAtco() {
		return "9100"+tiploc;
	}
	
	public void setTiploc(String tiploc) {
		if (null!=this.tiploc) {
			this.locations.byTiploc.remove(this.tiploc);
		}
		this.tiploc = tiploc;
		this.locations.byTiploc.put(tiploc, this);
	}
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Point getLocation() {
		return pos;
	}
	
	public void setLocation(int northing, int easting) {
		pos = new Point(easting, northing);
	}
	
	private String stanox;
	private String tla;
	private String tiploc;
	private String shortDescription;
	private String description;
	private Point pos;

	public String toJSONString() {
		return String.format(
			"{\"id\":%s,\"tla\":%s,\"tiploc\":%s,\"sDesc\":%s,\"desc\":%s," +
				"\"e\":%s,\"n\":%s" +
			"}",
			JSONObject.quote(stanox),
			JSONObject.quote(tla),
			JSONObject.quote(tiploc),
			JSONObject.quote(shortDescription),
			JSONObject.quote(description),
			pos==null ? 0 : pos.easting,
			pos==null ? 0 : pos.northing
		);
	}
}