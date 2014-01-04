package me.taks.nrlocator;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCM {
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	

	private String regid;
	
	private SharedPreferences getPrefs() {
	    // persist the registration ID in shared preferences
	    return App.cx.getSharedPreferences(GCM.class.getName(), Context.MODE_PRIVATE);
	}
	
	/** Gets the current registration ID for application on GCM service, if there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * @return registration ID, or empty string if there isn't one. */
	private String getRegistrationId() {
	    final SharedPreferences prefs = getPrefs();
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i("App", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion();
	    if (registeredVersion != currentVersion) {
	        Log.i("App", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	public void register() {
	    if (getRegistrationId().isEmpty()) {
	        registerInBackground();
	    }
	}
	
	/** Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences. */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
            try {
                regid = GoogleCloudMessaging.getInstance(App.cx).register(SENDER_ID);

        	    getPrefs().edit().putString(PROPERTY_REG_ID, regid)
		    		    .putInt(PROPERTY_APP_VERSION, getAppVersion())
		    		    .commit();
                return "Device registered, registration ID=" + regid;
            } catch (IOException ex) {
                return "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
            }
	        }
	
	        @Override
	        protected void onPostExecute(String msg) {
	            Log.i("SubList", msg);
	        }
	    }.execute(null, null, null);
	}
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion() {
		try {
			PackageInfo packageInfo = App.cx.getPackageManager()
					.getPackageInfo(App.cx.getPackageName(), 0);
		    return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
	public void subscribe(String filter) {
		subscribe(filter, false);
	}
	
	public void unSubscribe(String filter) {
		subscribe(filter, true);
	}
	
	private void subscribe(final String filter, final boolean unsub) {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            sendSubscription(filter, unsub);
	            return "";
	        }
	    }.execute(null, null, null);
	}
	
	private void sendSubscription(String filter, boolean unsub) {
	    try {
	    	HttpEntityEnclosingRequestBase http = 
	    			unsub
	    			? new HttpPut("http://"+SERVER+"/sub/delete")
	        		: new HttpPost("http://"+SERVER+"/sub");
	        http.setEntity(new StringEntity(String.format(
	            	"{\"method\":\"android\",\"clientId\":%s,\"subscription\":%s}",
	            	JSONObject.quote(getRegistrationId()),
	            	JSONObject.quote("report."+filter)
	        )));
	        HttpResponse response = new DefaultHttpClient().execute(http);
	        //TODO: should alert the user to a failure or store it on the sub
            Log.i("GCM "+(unsub?"un":"")+"sub "+filter, SERVER+" responded "+response.getStatusLine());
	    } catch (IOException e) {
            Log.e("GCM "+(unsub?"un":"")+"sub on "+filter, "Exception "+e.getMessage());
	    }
	}
}
