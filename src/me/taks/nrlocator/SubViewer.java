package me.taks.nrlocator;

import me.taks.nrlocator.entities.Location;
import me.taks.nrlocator.entities.Report;
import me.taks.nrlocator.entities.Sub;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SubViewer {
	public static SubViewer get(Sub sub) {
		if (sub==null) return new SubViewer();
		else if (sub.getFilter().startsWith("loc=")) 
			return new LocSubscriptionViewer(sub);
		else
			return new TrainSubscriptionViewer(sub);
	}

	public String getTitle() { return "Unknown Filter"; }
	
	public String getSummary(Report report) { return "Unknown Filter"; }
	
	public ListAdapter getReportsAdapter(final Context cx) { return null; }
}

class RealSubscriptionViewer extends SubViewer {
	protected Sub sub;
	
	public RealSubscriptionViewer(Sub sub) {
		this.sub = sub;
	}

	protected ListAdapter getReportsAdapter(final Context cx, final boolean locView) {
		return new ListAdapter() {
			public int getCount() { return sub.size(); }
			public Object getItem(int index) { return sub.get(index); }

			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return 0;
			}
	
			public int getItemViewType(int arg0) { return 0; }
	
			@Override
			public View getView(int index, View view, ViewGroup parent) {
				ReportViewer rv = ReportViewer.get(sub.get(index));
				return locView ? rv.getLocTextView(cx, (TextView)view)
								: rv.getTrainTextView(cx, (TextView)view);
			}
	
			public int getViewTypeCount() { return 1; }
			public boolean hasStableIds() { return false; }
			public boolean isEmpty() { return sub.size()==0; }
	
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				sub.registerObserver(observer);
			}
	
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				sub.unregisterObserver(observer);
			}

			public boolean areAllItemsEnabled() { return false; }
			public boolean isEnabled(int position) { return false; }
			
		};
	}
}
class TrainSubscriptionViewer extends RealSubscriptionViewer {
	public TrainSubscriptionViewer(Sub sub) {
		super(sub);
	}

	public String getTitle() {
		String[] filterParts = sub.getFilter().split("=");
		return "Train "+filterParts[1];
	}
	
	public String getSummary(Report report) {
		return ReportViewer.get(report).getSummaryNoLoc();
	}
	
	public ListAdapter getReportsAdapter(final Context cx) {
		return getReportsAdapter(cx, true);
	}
}

class LocSubscriptionViewer extends RealSubscriptionViewer {
	public LocSubscriptionViewer(Sub sub) {
		super(sub);
	}

	public String getTitle() {
		String[] filterParts = sub.getFilter().split("=");
		Location loc = filterParts.length>1 
				? sub.getLocations().getByStanox(filterParts[1])
				: null;
		return loc!=null ? ReportViewer.titleCase(loc.getDescription()) : "Unknown";
	}
	
	public String getSummary(Report report) {
		return ReportViewer.get(report).getSummary();
	}
	
	public ListAdapter getReportsAdapter(final Context cx) {
		return getReportsAdapter(cx, false);
	}
}
