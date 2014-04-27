package com.example.quori;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

@SuppressLint("ShowToast")
public class Login_Activity extends Activity implements OnClickListener{

	EditText user;
	EditText pass;
	TextView error;
	Button submit;
	Button cancel;
	String result = null;
	InputStream is = null;
	StringBuilder sb = null;
	private String username;
	private String password;

	JsonParser jsonParser = new JsonParser();
	private ProgressDialog pDialog;
	private static final String LOGIN_URL = "http://152.8.115.221/Quorigo/quorigologin.php?username=";
	private static final int DB_DATA_MAX_FIELDS = 3;
	private attemptLogin mAuthTask = null;
	private final String[] DB_FIELDS = { "InstructorID", "username", "password" };
	String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		user = (EditText) findViewById(R.id.edituser);
		pass = (EditText) findViewById(R.id.editpass);
		submit = (Button) findViewById(R.id.submit);
		cancel = (Button) findViewById(R.id.cancel);
		error = (TextView) findViewById(R.id.error);
		submit.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
		return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.submit:
			Log.d("user set?", username + "");
			username = user.getText().toString();
			password = pass.getText().toString();
			new attemptLogin().execute();
			break;

		case R.id.cancel:
			user.setText("");
			pass.setText("");

			break;

		default:
			break;
		}
	}

	//
	 // Represents an asynchronous login task used to authenticate the user.
	 //s
	class attemptLogin extends AsyncTask<Void,Void,Boolean> {
		
	//	 Verify user login credentials
		public boolean verifyUser() {
		// Read data from php script output
		//LOGIN_URL;
			InputStream is = null;

			// HTTP Post
			try {
				// Create HttpClient to handle connection
				HttpClient http_client = new DefaultHttpClient();
				// Create HttpPost to connect to php file
				HttpPost http_post = new HttpPost(LOGIN_URL + username);

				// Create response to read in information sent
				HttpResponse response = http_client.execute(http_post); // Execute
																		// script
				// Store the sent information
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

				// Handle exception
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());

			}
			// Convert response to string
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				if (Looper.myLooper() == null) {
					Looper.prepare();
				}
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
			}
				is.close();
				Context context = getApplicationContext();
				Toast.makeText(context, sb, Toast.LENGTH_LONG);
				result = sb.toString();
				if (result.substring(0, 2).equals("[]")) {
					return false;
				}
				if (result.contains("cannot connect")) {
					Log.d("cannot connect to db", "connection error");
					Looper.myLooper().quit();
					cancel(true);
					return false;
				}
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}

			String[] dbData = new String[DB_DATA_MAX_FIELDS];

			Log.d("result", result);
			// Parse JSON data
			try {
				// Create JSON array to store results from DB
				JSONArray userArray = new JSONArray(result);
				JSONObject json_data = userArray.getJSONObject(0);
				// Iterate through JSON array
				for (int i = 0; i < DB_FIELDS.length; i++) {
					// Store user field data in array to compare
					dbData[i] = json_data.getString(DB_FIELDS[i]);
				}
				// Handle exception
			} catch (JSONException e) {
				Log.e("log_tag", "Error parsing data here " + e.toString());
			}

			Log.d("user", dbData[0] + " " + dbData[1] + " " + dbData[2]);
			// Check password stored in database against password entered

			Log.d("authentication", "authenticating user with database...");
			Context context = getApplicationContext();
			Toast.makeText(context, password, Toast.LENGTH_LONG);//Used to check to see if the password is being read
			Toast.makeText(context, dbData[2], Toast.LENGTH_LONG);//Used to check to see if the password is in the database array
			if (dbData[2].equals(password)) {
				Looper.myLooper().quit();
				return true;
			}
			Looper.myLooper().quit();
			return false;
		}

		// AsyncTask background thread activity
		@Override
	protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				// Simulate network access.
				Thread.sleep(2000);
		} catch (InterruptedException e) {
				return false;
			}
Log.d("user", verifyUser() + "");
			return verifyUser(); // Return if user is verified or not
		}

		// After background thread finishes...what do to?
		@Override
		protected void onPostExecute(final Boolean success) {
		mAuthTask = null;

		if (success) {
				Log.d("authentication",
						"user authentication success...starting activity");
				startActivity(new Intent(Login_Activity.this,
						MainActivity.class));
			} else {
				Log.d("authentication", "user authentication failed...");
				Toast.makeText(getApplicationContext(), "The username or password is wrong. . ", Toast.LENGTH_SHORT).show();
		}
	}

		// Handle onCancelled
		@Override
		protected void onCancelled() {
			mAuthTask = null;
	//	 showProgress(false);
			Toast.makeText(Login_Activity.this, "DB Connection Error",
					Toast.LENGTH_LONG).show();
		}
//		public void onBackPressed() {
//		     AlertDialog.Builder(this)
//		        .setTitle("Really Exit?")
//		        .setMessage("Are you sure you want to exit?")
//		        .setNegativeButton(android.R.string.no, null)
//		        .setPositiveButton(android.R.string.yes, new OnClickListener() {
//
//		            public void onClick(DialogInterface arg0, int arg1) {
//		                Login_Activity.super.onBackPressed();
//		            }
//		        }).create().show();
//		}
	}

}