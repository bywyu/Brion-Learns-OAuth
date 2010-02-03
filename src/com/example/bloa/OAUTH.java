package com.example.bloa;

import junit.framework.Assert;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class OAUTH extends AccountAuthenticatorActivity {

	private static final String TAG = "OAUTH";

	public static final String USER_TOKEN = "user_token";
	public static final String USER_SECRET = "user_secret";
	public static final String REQUEST_TOKEN = "request_token";
	public static final String REQUEST_SECRET = "request_secret";

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";

	private static final Uri CALLBACK_URI = Uri.parse("bloa-app://twitt");

	public static final String PREFS = "MyPrefsFile";

	private OAuthConsumer mConsumer = null;
	private OAuthProvider mProvider = null;
	
	SharedPreferences mSettings;
	AccountAuthenticatorResponse mResponse;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	
    	Intent startIntent = getIntent();
    	Bundle b = startIntent.getExtras();
    	mResponse = (AccountAuthenticatorResponse) b.get(AccountManager.KEY_ACCOUNT_MANAGER_RESPONSE);

    	// We don't need to worry about any saved states: we can reconstruct the state
		mConsumer = new CommonsHttpOAuthConsumer(
				Keys.TWITTER_CONSUMER_KEY, 
				Keys.TWITTER_CONSUMER_SECRET);
		
		mProvider = new DefaultOAuthProvider(
				TWITTER_REQUEST_TOKEN_URL, 
				TWITTER_ACCESS_TOKEN_URL,
				TWITTER_AUTHORIZE_URL);
		
		// It turns out this was the missing thing to making standard Activity launch mode work
		mProvider.setOAuth10a(true);
		
		mSettings = this.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

		Intent i = this.getIntent();
		if (i.getData() == null) {
			try {
				String authUrl = mProvider.retrieveRequestToken(mConsumer, CALLBACK_URI.toString());
				saveRequestInformation(mSettings, mConsumer.getToken(), mConsumer.getTokenSecret());
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	    Bundle b = new Bundle();

	    Uri uri = getIntent().getData();
		if (uri != null && CALLBACK_URI.getScheme().equals(uri.getScheme())) {
			String token = mSettings.getString(OAUTH.REQUEST_TOKEN, null);
			String secret = mSettings.getString(OAUTH.REQUEST_SECRET, null);
			Intent i = new Intent(this, BLOA.class); // Currently, how we get back to main activity.
			
			try {
				if(!(token == null || secret == null)) {
					mConsumer.setTokenWithSecret(token, secret);
				}
				String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
				String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

				// We send out and save the request token, but the secret is not the same as the verifier
				// Apparently, the verifier is decoded to get the secret, which is then compared - crafty
				// This is a sanity check which should never fail - hence the assertion
				Assert.assertEquals(otoken, mConsumer.getToken());

				// This is the moment of truth - we could throw here
				mProvider.retrieveAccessToken(mConsumer, verifier);
				// Now we can retrieve the goodies
				token = mConsumer.getToken();
				secret = mConsumer.getTokenSecret();
				// Clear the request stuff, now that we have the real thing
				OAUTH.saveRequestInformation(mSettings, null, null);
				OAUTH.saveAuthInformation(mSettings, token, secret);
			    b.putString(AccountManager.KEY_ACCOUNT_NAME, token);
			    b.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.auth_type));
			    b.putString(AccountManager.KEY_AUTHTOKEN, secret);
			    mResponse.onResult(b);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			} finally {
			    setAccountAuthenticatorResult(b);
				finish();
			}
		}
	}
	
	public static void saveRequestInformation(SharedPreferences settings, String token, String secret) {
		// null means to clear the old values
		SharedPreferences.Editor editor = settings.edit();
		if(token == null) {
			editor.remove(OAUTH.REQUEST_TOKEN);
			Log.d(TAG, "Clearing Request Token");
		}
		else {
			editor.putString(OAUTH.REQUEST_TOKEN, token);
			Log.d(TAG, "Saving Request Token: " + token);
		}
		if (secret == null) {
			editor.remove(OAUTH.REQUEST_SECRET);
			Log.d(TAG, "Clearing Request Secret");
		}
		else {
			editor.putString(OAUTH.REQUEST_SECRET, secret);
			Log.d(TAG, "Saving Request Secret: " + secret);
		}
		editor.commit();
		
	}
	
	public static void saveAuthInformation(SharedPreferences settings, String token, String secret) {
		// null means to clear the old values
		SharedPreferences.Editor editor = settings.edit();
		if(token == null) {
			editor.remove(OAUTH.USER_TOKEN);
			Log.d(TAG, "Clearing OAuth Token");
		}
		else {
			editor.putString(OAUTH.USER_TOKEN, token);
			Log.d(TAG, "Saving OAuth Token: " + token);
		}
		if (secret == null) {
			editor.remove(OAUTH.USER_SECRET);
			Log.d(TAG, "Clearing OAuth Secret");
		}
		else {
			editor.putString(OAUTH.USER_SECRET, secret);
			Log.d(TAG, "Saving OAuth Secret: " + secret);
		}
		editor.commit();
		
	}
	
}
