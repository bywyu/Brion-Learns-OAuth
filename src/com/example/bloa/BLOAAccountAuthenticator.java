package com.example.bloa;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.SignatureMethod;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class BLOAAccountAuthenticator extends AbstractAccountAuthenticator {
	public static final String TAG = "MyAccountAuthenticator";

	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;
	private Context mContext;

	private static final Uri CALLBACK_URI = Uri.parse("bloa-app://twitt");

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
	
	public BLOAAccountAuthenticator(Context context) {
		super(context);
		mContext = context;
        mConsumer = new CommonsHttpOAuthConsumer(
        		Keys.TWITTER_CONSUMER_KEY,
        		Keys.TWITTER_CONSUMER_SECRET,
        		SignatureMethod.HMAC_SHA1);
		mProvider = new DefaultOAuthProvider(mConsumer,TWITTER_REQUEST_TOKEN_URL,TWITTER_ACCESS_TOKEN_URL,TWITTER_AUTHORIZE_URL);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse arg0, String arg1,
			String arg2, String[] arg3, Bundle arg4)
			throws NetworkErrorException {
 		String authUrl = null;
 		
 		try {
			authUrl = mProvider.retrieveRequestToken(CALLBACK_URI.toString());
			Log.d(TAG, "AuthUrl: " + authUrl);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
			mContext.startActivity(i);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse arg0, Account arg1,
			String arg2, Bundle arg3) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthTokenLabel(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1,
			String[] arg2) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, String arg2, Bundle arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
