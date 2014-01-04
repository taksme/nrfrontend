package me.taks.nrlocator.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

public class NRActivity extends FragmentActivity {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	/** Has the device got the Google Play Services APK? If not, display a 
	 * download/enable dialog */
	public boolean checkPlayServices() {
	    int res = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (res != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(res)) {
	            GooglePlayServicesUtil.getErrorDialog(res, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("GooglePlayServicesCheck", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	public String getTextViewText(int id) {
		return ((TextView)findViewById(id)).getText().toString();
	}
}
