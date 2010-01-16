package com.example.bloa;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyAuthenticatorService extends Service {

	public MyAuthenticatorService() {
		// TODO Auto-generated constructor stub
	}

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        MyAuthenticatorService getService() {
            return MyAuthenticatorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Select the interface to return.  If your service only implements
        // a single interface, you can just return it here without checking
        // the Intent.
        if (AccountManager.ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction())) {
            return null;
        }
        return null;
    }

}
