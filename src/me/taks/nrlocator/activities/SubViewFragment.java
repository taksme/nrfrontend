package me.taks.nrlocator.activities;

import me.taks.nrlocator.App;
import me.taks.nrlocator.R;
import me.taks.nrlocator.SubViewer;
import me.taks.nrlocator.entities.Sub;
import me.taks.nrlocator.entities.Subs;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A fragment representing a single Report Tracker detail screen. This fragment
 * is either contained in a {@link ReportTrackerListActivity} in two-pane mode
 * (on tablets) or a {@link SubView} on handsets.
 */
public class SubViewFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Sub sub;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SubViewFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			sub = Subs.instance().get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.subscription_view_frag, container, false);
		
		if (sub != null) {
			SubViewer rv = SubViewer.get(sub);
			getActivity().getActionBar().setTitle(rv.getTitle());
			((ListView)(rootView.findViewById(R.id.list)))
			.setAdapter(rv.getReportsAdapter(App.cx));
			
			sub.seenAll();
			
			NotificationManager notMgr = (NotificationManager)
	                getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			notMgr.cancel(sub.getFilter().hashCode());
		}

		return rootView;
	}
}
