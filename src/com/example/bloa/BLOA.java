package com.example.bloa;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

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

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class BLOA extends Activity implements OnCheckedChangeListener, OnClickListener, AccountManagerCallback<Bundle> {
	public static final String TAG = "BLOA";

	public static final String TWITTER_REQUEST_TOKEN_URL = "http://twitter.com/oauth/request_token";
	public static final String TWITTER_ACCESS_TOKEN_URL = "http://twitter.com/oauth/access_token";
	public static final String TWITTER_AUTHORIZE_URL = "http://twitter.com/oauth/authorize";
	
	private static final Uri CALLBACK_URI = Uri.parse("bloa-app://twitt");
	
	private static final String PREFS = "MyPrefsFile";
	
	AccountManager mMgr;
	
	private OAuthConsumer mConsumer;
	private OAuthProvider mProvider;
	
	private DefaultHttpClient mClient;
	
	private CheckBox mCB;
	private EditText mEditor;
	private Button mButton;
	private TextView mDisplay;
	private TextView mUser;
	
	private static final String TOKEN_STRING = "token";
	private static final String SECRET_STRING = "secret";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mMgr = AccountManager.get(this);
        Account[] accounts = mMgr.getAccountsByType(getString(R.string.auth_type));
        AuthenticatorDescription[] authers = mMgr.getAuthenticatorTypes();
        
        
		SharedPreferences settings = this.getSharedPreferences(PREFS, 0);
/*		if (settings.contains(TOKEN_STRING)) {
			Account a = settings.get
			String token = settings.getString(TOKEN_STRING, "");
			if(!token.equals("")) {
				mConsumer.setTokenWithSecret(token, secret);
				mProvider.setConsumer(mConsumer);
				new GetCredentialsTask().execute();
			}
		}
*/ 
		AccountManagerFuture<Bundle> acct = mMgr.addAccount(getString(R.string.auth_type), 
				null, null, null, null, this, null);
		Log.d(TAG, "Account toString(): " + acct.toString());
        
        mCB = (CheckBox) this.findViewById(R.id.enable);
        mCB.setChecked(false);
        mEditor = (EditText) this.findViewById(R.id.editor);
        mButton = (Button) this.findViewById(R.id.post);
        mDisplay = (TextView) this.findViewById(R.id.last);
        mUser = (TextView) this.findViewById(R.id.user);
        
        mConsumer = new CommonsHttpOAuthConsumer(
        		Keys.TWITTER_CONSUMER_KEY,
        		Keys.TWITTER_CONSUMER_SECRET,
        		SignatureMethod.HMAC_SHA1);
 	    
		mProvider = new DefaultOAuthProvider(mConsumer,
				TWITTER_REQUEST_TOKEN_URL,
				TWITTER_ACCESS_TOKEN_URL,
				TWITTER_AUTHORIZE_URL);
		
        mClient = new DefaultHttpClient();
        
        mButton.setOnClickListener(this);
        mCB.setOnCheckedChangeListener(this);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle b) {
    	// b.putSerializable("provider", mProvider);
    	// b.putSerializable("consumer", mConsumer);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
		mClient.getConnectionManager().shutdown();
    }
    
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
 		String authUrl = null;
 		
 		if(arg1) {
 	 		try {
				authUrl = mProvider.retrieveRequestToken(CALLBACK_URI.toString());
				Log.d(TAG, "AuthUrl: " + authUrl);
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
				this.startActivity(i);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
 		} else {
    	    SharedPreferences settings = this.getSharedPreferences(PREFS, 0);
    	    SharedPreferences.Editor editor = settings.edit();
    	    editor.remove(TOKEN_STRING);
    	    editor.remove(SECRET_STRING);
    	    editor.commit();
    	    mUser.setText("");
        	mButton.setEnabled(false);
        	mEditor.setEnabled(false);
		}
	}

    public HttpParams getParams() { 
        //Tweak further as needed for your app 
        HttpParams params = new BasicHttpParams(); 
        // set this to false, or else you'll get an Expectation Failed: error 
        HttpProtocolParams.setUseExpectContinue(params, false); 
        return params; 
    }
    
    public JSONObject getCredentials() {
    	try {
			String response;
			HttpContext c = new BasicHttpContext();
			// First let's verify our credentials
			HttpGet get = new HttpGet("http://twitter.com/account/verify_credentials.json");

			// sign the request to authenticate
			mConsumer.sign(get);
			
	        response = mClient.execute(get, new BasicResponseHandler(), c);
	        JSONObject respJSO = new JSONObject(response);
	        Log.d(TAG, "Credentials: " + respJSO.toString(2));
			
    		return respJSO;
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    	} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
       	return null;
    }
    
    public JSONObject postTweet() {

    	String response;
		HttpContext c = new BasicHttpContext();
		// create a request that requires authentication
        HttpPost post = new HttpPost("http://twitter.com/statuses/update.json");
        LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
        
        // 'status' here is the update value you collect from UI
        out.add(new BasicNameValuePair("status", mEditor.getText().toString()));
        HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(out, HTTP.UTF_8);
			post.setEntity(entity);
			post.setParams(this.getParams());
        
			// sign the request to authenticate
			mConsumer.sign(post);
		
			response = mClient.execute(post, new BasicResponseHandler(), c);
			mEditor.setText("");

			return new JSONObject(response);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
	@Override
	public void onClick(View v) {
		JSONObject jso = postTweet();
        String status;
        status = jso == null ? "Null Value" : jso.optString("text", "Bad Value");
        mDisplay.setText(status);
	}

	@Override
	public void run(AccountManagerFuture<Bundle> arg0) {
		// TODO Auto-generated method stub
		if(arg0.isCancelled()) {
			
		} else {
			try {
				Bundle b = arg0.getResult();
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}