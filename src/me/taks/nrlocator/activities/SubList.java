package me.taks.nrlocator.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;

import me.taks.nrlocator.App;
import me.taks.nrlocator.R;

public class SubList extends NRActivity implements SubListFragment.Callbacks {
	private static final String TAG = "GCMDemo";
	
	private boolean mTwoPane;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		App.cx = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_list_act);
	
		
	    if (findViewById(R.id.reporttracker_detail_container) != null) {
			mTwoPane = true;
			// give list items  'activated' state when touched in two-pane mode
			((SubListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.reporttracker_list))
					.setActivateOnItemClick(true);
		}
		
	    // Check device for Play Services APK and proceed with GCM registration.
	    if (checkPlayServices()) {
	    	App.gcm.register();
	    } else {
	        Log.i(TAG, "No valid Google Play Services APK found.");
	    }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    // Check device for Play Services APK.
	    checkPlayServices();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.register, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent detailIntent = new Intent(this,
				SubCreate.class);
		startActivity(detailIntent);
		return true;
	}
	
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(SubViewFragment.ARG_ITEM_ID, id);
			SubViewFragment fragment = new SubViewFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.reporttracker_detail_container, fragment)
					.commit();
	
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					SubView.class);
			detailIntent.putExtra(SubViewFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}
	
}
