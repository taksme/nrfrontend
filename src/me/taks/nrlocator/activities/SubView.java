package me.taks.nrlocator.activities;

import me.taks.nrlocator.App;
import me.taks.nrlocator.R;
import me.taks.nrlocator.entities.Subs;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

/** An activity to hold the detail fragment on handset devices */
public class SubView extends NRActivity {
	String filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		filter = getIntent().getStringExtra(SubViewFragment.ARG_ITEM_ID);
		
		setContentView(R.layout.subscription_view_act);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Where savedInstanceState is non-null there is fragment state from previous 
		// configurations of this activity (e.g. when on rotating the screen).
		// Here the fragment will automatically be re-added so we don't need to 
		// manually add it.
		if (savedInstanceState == null) {
			// Create the detail fragment and add it using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(SubViewFragment.ARG_ITEM_ID, filter);
			SubViewFragment fragment = new SubViewFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.reporttracker_detail_container, fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
	    	App.gcm.unSubscribe(filter);
	    	Subs.instance().remove(filter);
			//dff
		case android.R.id.home:
			// Up button. Using NavUtils makes the back button logic work nice. 
			NavUtils.navigateUpTo(this, new Intent(this, SubList.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }
}
