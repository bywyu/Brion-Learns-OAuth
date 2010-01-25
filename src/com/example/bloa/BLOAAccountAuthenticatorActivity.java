package com.example.bloa;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.SignatureMethod;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class BLOAAccountAuthenticatorActivity extends AccountAuthenticatorActivity {
	private static final String TAG = "BLOAAccountAuthenticatorActivity";

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
 
	private static final Uri CALLBACK_URI = Uri.parse("bloa-app://twitt");
 
	private OAuthConsumer mConsumer = null;
	private OAuthProvider mProvider = null;

	AccountAuthenticatorResponse mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Intent startIntent = getIntent();
    	Bundle b = startIntent.getExtras();
    	mResponse = (AccountAuthenticatorResponse) b.get(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE);
    	
		mConsumer = new CommonsHttpOAuthConsumer(
				Keys.TWITTER_CONSUMER_KEY,
				Keys.TWITTER_CONSUMER_SECRET, 
				SignatureMethod.HMAC_SHA1);
		mProvider = new DefaultOAuthProvider(
				mConsumer,TWITTER_REQUEST_TOKEN_URL, 
				TWITTER_ACCESS_TOKEN_URL,
				TWITTER_AUTHORIZE_URL);

		String authUrl;
		try {
			authUrl = mProvider.retrieveRequestToken(CALLBACK_URI.toString());
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
			this.startActivity(i);
		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void onNewIntent(Intent i) {
	    Bundle b = new Bundle();
    	String verifier;
	    try {
	    	Uri data = i.getData();
	    	if(data == null || (verifier = data.getQueryParameter(OAuth.OAUTH_VERIFIER)) == null) {
				b.putInt(AccountManager.KEY_ERROR_CODE, -10000);
				b.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.auth_error));
				mResponse.onError(-10000, getString(R.string.auth_error));
	    	} else {
				mProvider.retrieveAccessToken(verifier);
			    String token = mConsumer.getToken();
			    String secret = mConsumer.getTokenSecret();
			    b.putString(AccountManager.KEY_ACCOUNT_NAME, token);
			    b.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.auth_type));
			    b.putString(AccountManager.KEY_AUTHTOKEN, secret);
			    mResponse.onResult(b);
	    	}
		} catch (OAuthMessageSignerException e) {
			b.putInt(AccountManager.KEY_ERROR_CODE, -1);
			b.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
			mResponse.onError(-1, e.getMessage());
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			b.putInt(AccountManager.KEY_ERROR_CODE, -2);
			b.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
			mResponse.onError(-2, e.getMessage());
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			b.putInt(AccountManager.KEY_ERROR_CODE, -3);
			b.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
			mResponse.onError(-3, e.getMessage());
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			b.putInt(AccountManager.KEY_ERROR_CODE, -4);
			b.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
			mResponse.onError(-4, e.getMessage());
			e.printStackTrace();
		}
	    setAccountAuthenticatorResult(b);
    	finish();
    }
}
