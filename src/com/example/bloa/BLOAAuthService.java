package com.example.bloa;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BLOAAuthService extends Service {
	public static final String TAG = "BLOAAuthenticatorService";

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
	

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


    private class BLOAAccountAuthenticator extends AbstractAccountAuthenticator {
    	public static final String TAG = "MyAccountAuthenticator";

    	private OAuthConsumer mConsumer;
    	private OAuthProvider mProvider;
    	private Context mContext;

    	public BLOAAccountAuthenticator(Context context) {
    		super(context);

    		mContext = context;

    		mConsumer = new CommonsHttpOAuthConsumer(
            		Keys.TWITTER_CONSUMER_KEY,
            		Keys.TWITTER_CONSUMER_SECRET);
            
    		mProvider = new DefaultOAuthProvider(
    				TWITTER_REQUEST_TOKEN_URL,
    				TWITTER_ACCESS_TOKEN_URL,
    				TWITTER_AUTHORIZE_URL);
    	}

    	@Override
    	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
    			String AuthTokenType, String[] requiredFeatures, Bundle options)
    			throws NetworkErrorException {
    		Bundle ret = new Bundle();
    		Intent i = new Intent(mContext, OAUTH.class);
    		i.putExtra(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE, response);
    		ret.putParcelable(AccountManager.KEY_INTENT, i);
    		return ret;
    	}

    	@Override
    	public Bundle confirmCredentials(AccountAuthenticatorResponse response,
    			Account accountType, Bundle options) {
    		return null;
    	}

    	@Override
    	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String AuthTokenType, Bundle options) 
    			throws NetworkErrorException {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public String getAuthTokenLabel(String response) {
    		return mContext.getString(R.string.secret);
    	}

    	@Override
    	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account accountType,
    			String[] features) throws NetworkErrorException {
    		Bundle b = new Bundle();
    		b.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
    		return b;
    	}

    	@Override
    	public Bundle updateCredentials(AccountAuthenticatorResponse response,
    			Account accountType, String AuthTokenType, Bundle options) {
    		// TODO Auto-generated method stub
    		return null;
    	}

    }
}
