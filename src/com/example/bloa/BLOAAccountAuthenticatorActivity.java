package com.example.bloa;

import oauth.signpost.OAuth;
import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;

public class BLOAAccountAuthenticatorActivity extends AccountAuthenticatorActivity {
	private static final String TAG = "BLOAAccountAuthenticatorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
	    String verifier = getIntent().getData().getQueryParameter(OAuth.OAUTH_VERIFIER);
	    Bundle b = new Bundle();
	    b.putString(getString(R.string.token), verifier);
	    setAccountAuthenticatorResult(b);
    	finish();
    }
}
