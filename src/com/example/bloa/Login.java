package com.example.bloa;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AccountAuthenticatorActivity {
	private static final String TAG = "Login";
	
	private EditText login;
	private EditText password;
	private Button button;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		login = (EditText) findViewById(R.id.login);
		password = (EditText) findViewById(R.id.password);
		button = (Button) findViewById(R.id.doIt);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle b = new Bundle();
				b.putString(AccountManager.KEY_ACCOUNT_NAME, login.getText().toString());
				b.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.auth_type));
				setAccountAuthenticatorResult(b);
				finish();
			}
		});
	}
}
