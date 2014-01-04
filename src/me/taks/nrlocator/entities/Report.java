package me.taks.nrlocator.entities;


import org.json.JSONException;
import org.json.JSONObject;

public class Report {
	private Sub sub;
	
	public Report(Sub sub) {
		this.sub = sub;
	}
	
	public Report(Sub sub, JSONObject o) throws JSONException {
		this(sub);
		trainId = o.getString("train");
		direction = Dir.valueOf(o.getString("dir"));
		event = Event.valueOf(o.getString("evt"));
		times = new TimeRange(o.getLong("plannedTs"), o.getLong("ts"));
		setLocationStanox(o.getString("loc"));
		setNextStanox(o.getString("next"));
		seen = o.has("seen") ? o.getBoolean("seen") : false;
	}
	
	private Locations getLocations() { return sub.getLocations(); }
	public enum Dir { NONE, UP, DOWN };
	public enum Event { NONE, PASS, ARRIVAL, DEPARTURE };
	
	private Location location;
	private Location next;
	private TimeRange times = new TimeRange(0, 0);
	private String trainId;
	private Dir direction = Dir.NONE;
	private Event event = Event.NONE;
	private boolean seen = false;
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public void setLocationStanox(String stanox) {
		setLocation(getLocations().byStanox.get(stanox));
	}
	public Location getNext() {
		return next;
	}
	public void setNextStanox(String stanox) {
		this.next = getLocations().byStanox.get(stanox);
	}
	public void setNext(Location next) {
		this.next = next;
	}
	public TimeRange getTimes() {
		return times;
	}
	public void setTimes(TimeRange times) {
		this.times = times;
	}
	public void setTimes(long expected, long actual) {
		this.times = new TimeRange(expected, actual);
	}
	public String getTrainId() {
		return trainId;
	}
	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}
	public Dir getDirection() {
		return direction;
	}
	public void setDirection(Dir direction) {
		this.direction = direction;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public String toJSONString() {
		return String.format(
			"{\"train\":%s,\"dir\":%s,\"evt\":%s," +
				"\"ts\":%s,\"plannedTs\":%s," +
				"\"loc\":%s,\"next\":%s,\"seen\":%s" +
			"}",
			JSONObject.quote(trainId),
			JSONObject.quote(direction.toString()),
			JSONObject.quote(event.toString()),
			times.getEnd(),
			times.getStart(),
			JSONObject.quote(location==null ? "" : location.getStanox()),
			JSONObject.quote(next==null ? "" : next.getStanox()),
			seen
		);
	}
	
	public boolean isSeen() {
		return this.seen;
	}
	
	public void seen() {
		boolean changed = !this.seen;
		this.seen = true;
		if (changed) updated();
	}
	
	public void updated() {
		sub.updated();
	}
}
