package me.taks.nrlocator;

import me.taks.nrlocator.activities.SubView;
import me.taks.nrlocator.activities.SubViewFragment;
import me.taks.nrlocator.entities.Report;
import me.taks.nrlocator.entities.Sub;
import me.taks.nrlocator.entities.Subs;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/** Handling the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock. */
public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	
	public GcmIntentService() {
	    super("GcmIntentService");
	}
	public static final String TAG = "GCM Demo";
	
	@Override
	protected void onHandleIntent(Intent intent) {
	    final Bundle extras = intent.getExtras();
	
	    if (!extras.isEmpty()) {
	        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        String type = gcm.getMessageType(intent);
	        
	        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(type)) {
	            Log.e("HandleGCM", "Send error: " + extras.toString());
	            
	        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(type)) {
	        	Log.e("HandleGCM", "Deleted on server: " + extras.toString());
	        	
	        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(type)) {
	        	//Handle on UI loop to avoid crashes on entity updates
	    		new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
				    	handleMessage(extras.getString("filter"), 
				    					extras.getString("report"));
					}
				});
	        }
	    }
	    // Release the wake lock provided by the WakefulBroadcastReceiver.
	    NRLBroadcastReceiver.completeWakefulIntent(intent);
	}
	
	private void handleMessage(String filter, String report) {
		if (App.cx==null) App.cx=GcmIntentService.this.getApplicationContext();
	
		Sub sub = Subs.instance().get(filter);
		
		if (sub==null) { 
			sub = new Sub(Subs.instance(), filter);
		}
	    // Post notification of received message.
		try {
			sub.push(new Report(sub, new JSONObject(report)));
	    	sendNotification(sub);
		} catch (JSONException je) {
	        Log.e(TAG, String.format("Couldn't parse for %s: %s", filter, report));
		}
	}
	
	private void sendNotification(Sub sub) {
		SubViewer sv = SubViewer.get(sub);
		String summary = null;
		StringBuffer bigMessage = new StringBuffer();
		
		for (Report r : sub.unseen()) {
			if (summary==null) {
				bigMessage.append(summary = sv.getSummary(r));
			} else {
				bigMessage.append("\n"+sv.getSummary(r));
			}
		}
		
		Intent intent = new Intent(this, SubView.class);
		intent.putExtra(SubViewFragment.ARG_ITEM_ID, sub.getFilter());
		
		sendNotification(
				sub.getFilter().hashCode(), 
				sv.getTitle(), summary, bigMessage.toString(), 
				PendingIntent.getActivity(this, 0,
						intent, PendingIntent.FLAG_CANCEL_CURRENT
				)
		);
	}
	
	private void sendNotification(int id, String title, String msg, String big, PendingIntent intent) {
		long[] vib = {50,50,50};
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
		.notify(
			id,
			new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_stat_gcm)
			.setContentTitle(title)
			.setStyle(new NotificationCompat.BigTextStyle().bigText(big))
			.setContentText(msg)
			.setAutoCancel(true)
			.setTicker(title + "\n" + msg)
			.setVibrate(vib)
			.setContentIntent(intent)
			.build()
		);
	
}
}