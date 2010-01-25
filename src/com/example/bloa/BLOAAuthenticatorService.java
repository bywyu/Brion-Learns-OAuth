package com.example.bloa;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BLOAAuthenticatorService extends Service {
	public static final String TAG = "BLOAAuthenticatorService";
	BLOAAccountAuthenticator mAuth;
	
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
	}
	
    @Override
    public IBinder onBind(Intent intent) {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
    	IBinder ret = null;
        if (AccountManager.ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction()))
            ret = new BLOAAccountAuthenticator(this.getBaseContext()).getIBinder();
        return ret;
    }
}
