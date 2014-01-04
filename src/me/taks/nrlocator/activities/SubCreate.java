package me.taks.nrlocator.activities;

import java.util.ArrayList;
import java.util.List;

import me.taks.nrlocator.App;
import me.taks.nrlocator.R;
import me.taks.nrlocator.entities.Location;
import me.taks.nrlocator.entities.Locations;
import me.taks.nrlocator.entities.Sub;
import me.taks.nrlocator.entities.Subs;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;

/**A fragment representing a single Report Tracker detail screen. This fragment
 * is either contained in a {@link ReportTrackerListActivity} in two-pane mode
 * (on tablets) or a {@link SubView} on handsets. */
public class SubCreate extends NRActivity implements OnClickListener {

	public SubCreate() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_create_act);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
	
		findViewById(R.id.button1).setOnClickListener(this);
		
		((AutoCompleteTextView)findViewById(R.id.station))
				.setAdapter(new LocAdapter(this));
	}
	
	public void onClick(View caller) { //this is always the subscribe btn
		String head = getTextViewText(R.id.headcode);
		String loc = getTextViewText(R.id.station);
		String filter=null;
		if (head.length()>0) {
			filter = "headcode="+head;
		} else if (loc.length()>=5 && 
					null!=Locations.instance().getByStanox(loc.substring(loc.length()-5))) {
			filter = "loc=" + loc.substring(loc.length()-5);
		}

	    if (filter!=null) {
	    	App.gcm.subscribe(filter);
	    	new Sub(Subs.instance(), filter);
	    }
	    
		NavUtils.navigateUpTo(this, new Intent(this, SubList.class));
	}
	
	class LocAdapter extends ArrayAdapter<String> {
		public LocAdapter(Context cx) {
			super(cx, android.R.layout.simple_dropdown_item_1line);
		}

		@Override
		public Filter getFilter() {
			return new Filter() {
				
				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					clear();
					if (results.count > 0) {
						for (Location l : (List<Location>) results.values) 
							if (l.getStanox()!=null && l.getStanox().length()>0)
								add(l.getDescription() + " ("+l.getTla() + ") " + l.getStanox());
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
				
				@Override
				protected FilterResults performFiltering(CharSequence start) {
					List<Location> list;
//					if (start==null || start.length()<2) {
//						list = new ArrayList<Location>();
//					} else {
						list = Locations.instance().startWith(start.toString());
//					}
					FilterResults res = new FilterResults();
					res.count = list.size();
					res.values = list;
					Log.i("Filter", "got "+list.size() +" results");
					return res;
				}
			};
		}
	}
}
