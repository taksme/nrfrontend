package me.taks.nrlocator;

import android.app.Application;
import android.content.Context;

public class App extends Application {
	
    public static Context cx;
    public static GCM gcm = new GCM();

	@Override public void onCreate() {
        cx = getApplicationContext();
        super.onCreate();
    }
    
}
