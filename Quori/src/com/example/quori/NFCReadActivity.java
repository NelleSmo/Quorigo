package com.example.quori;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.quori.Login_Activity.attemptLogin;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class NFCReadActivity extends Activity {
	private NfcAdapter mNFCAdapter = null;
	private PendingIntent mNFCPendingIntent;
	private static final String TAG = "StudentTAG";
	private TextView nameTextView;
	private static String MIME_TEXT_PLAIN = "text/plain";
	private String Result;
	JsonParser jsonParser = new JsonParser();
	private ProgressDialog pDialog;
	private static String checkValue;
	private String[] dbData = {"","","",""}; 

	private static final int DB_DATA_MAX_FIELDS = 4;
	
	private final String[] DB_FIELDS = { "Lname", "Fname", "Absences", "LastCheck"};
	String userId;
	private TextView currentAbsence;
	private TextView LastAttended;


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_profile_fragment);
		Intent intent = getIntent();
		if (intent.hasExtra("check")) {
			checkValue =intent.getStringExtra("check");
			}
		else{}

		
		Log.d("check", checkValue+"");
		nameTextView= (TextView) findViewById(R.id.sp_nameTextView);
		currentAbsence = (TextView)findViewById(R.id.currentAbsencesTextView);
		LastAttended = (TextView)findViewById(R.id.LastAttendedLabelTextView);
		
				
		
		mNFCAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (mNFCAdapter == null){
			//Checks if device supports NFC
			Toast.makeText(this, "This device doesn't support NFC", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		if(!mNFCAdapter.isEnabled()){
			Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show();
		}
		else{
			//mTextView.setText("NFC tag ready!");
			
		}
		
		handleIntent(getIntent());
		
	}
	@Override
    protected void onResume() {
        super.onResume();
         
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown. 
         */
        setupForegroundDispatch(this, mNFCAdapter);
    }

	private void setupForegroundDispatch(final Activity activity,
			NfcAdapter mNFCAdapter2) {
		// TODO Auto-generated method stub
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		mNFCPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
		
		IntentFilter[] ifilters = new IntentFilter[1]; //filters out the types of NFC actions to look for
        String[][] techList = new String[][]{}; //the tech we are looking for
        
        ifilters[0] = new IntentFilter();
        ifilters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ifilters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            ifilters[0].addDataType("text/plain");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
     mNFCAdapter2.enableForegroundDispatch(activity, mNFCPendingIntent, ifilters, techList);
	}
	
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}
	
	
	private void handleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) { //TODO: Be able to explain this code
	         
	        String type = intent.getType();
	        if (MIME_TEXT_PLAIN.equals(type)) {
	 
	            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	            new NdefReaderTask().execute(tag);
	             
	        } else {
	            Log.d(TAG, "Wrong mime type: " + type);
	        }
	    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
	         
	        // In case we would still use the Tech Discovered Intent
	        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	        String[] techList = tag.getTechList();
	        String searchedTech = Ndef.class.getName();
	         
	        for (String tech : techList) {
	            if (searchedTech.equals(tech)) {
	                new NdefReaderTask().execute(tag);
	              
	                break;
	            }
	        }
	    }
       
		
	}
	public String getResult() {
		return Result;
	}
	private void setResult(String result) {
		Result = result;
	}
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		

		@Override
		protected String doInBackground(Tag... params) {
			// TODO Auto-generated method stub
			  Tag tag = params[0];
		         
		        Ndef ndef = Ndef.get(tag);
		        if (ndef == null) {
		            // NDEF is not supported by this Tag. 
		            return null;
		        }
		 
		        NdefMessage ndefMessage = ndef.getCachedNdefMessage(); //TODO: look up
		 
		        NdefRecord[] records = ndefMessage.getRecords();
		        for (NdefRecord ndefRecord : records) {
		            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
		                return readText(ndefRecord);
		            }
		        }
			return null;
		}

		private String readText(NdefRecord record) {
			/*
	         * See NFC forum specification for "Text Record Type Definition" at 3.2.1 
	         * 
	         * http://www.nfc-forum.org/specs/
	         * 
	         * bit_7 defines encoding
	         * bit_6 reserved for future use, must be 0
	         * bit_5..0 length of IANA language code
	         */
			String msg ="";
	        byte[] payload = record.getPayload();
	 
	        // Get the Text Encoding
	        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
	 
	        // Get the Language Code
	        int languageCodeLength = payload[0] & 0063; //TODO: look up
	         
	         
	        // Get the Text
	    
	        try {
	        	msg = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	        	//mTextView.setText(msg);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return msg;
		}
		
		
		@Override
	    protected void onPostExecute(String result) {
	        if (result != null) {
	           
	            setResult(result);
	         final SendUIDTask uidTask = new SendUIDTask();
	         new Thread(new Runnable(){
	        	 public void run(){
	        		 try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		 uidTask.execute();
	        		
	        	 }
	         }).start();  
		
	       
	        }
	      

		
		}


	 
	private class SendUIDTask extends AsyncTask<Void, Void, Boolean> {

		private InputStream is;
		private String result;
		private String name;
		private String absences;
		private String lastcheck;


		public boolean sendUID(){
		 String UID = getResult();
		 if (UID !=null){
		 Toast.makeText(getApplicationContext(), getResult(),Toast.LENGTH_LONG).show();
		  
			// HTTP Post
			try {
				// Create HttpClient to handle connection
				HttpClient http_client = new DefaultHttpClient();
				// Create HttpPost to connect to php file
				HttpPost http_post;
				

				
			

				// Create response to read in information sent
				
				
																		// script
				// Store the sent information
				

	            http_post = new HttpPost(checkValue + UID);
	            http_client.execute(http_post); 
				Log.d("posting URL"+ http_post + " ", UID);
				Log.d("posting URL", checkValue + UID);
				
	           
				// Execute
				// Handle exception
				// Create response to read in information sent
				HttpResponse response = http_client.execute(http_post); // Execute
																		// script
				// Store the sent information
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

				
		
			} catch (Exception e) {
				Log.e("log_tag", "Error in http connection " + e.toString());
				return false;
			}
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

			Log.d("student", dbData[0] + " " + dbData[1] + " " + dbData[2]+ " " + dbData[3]);
			// Check password stored in database against password entered
			name = dbData[1] + " " + dbData[0];
			absences = dbData[2];
			lastcheck = dbData[3];
			
			return true;
		 }
		 return false;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			if (Looper.myLooper() == null) {
				Looper.prepare();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("user", sendUID() + "");
			return true; // Return if user is verified or not
		
		         
		}
	
	
@Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
        	nameTextView.setText(name);
	         currentAbsence.setText(absences);
	          LastAttended.setText(lastcheck);

        Toast.makeText(getApplicationContext(), "You have successfully logged student in attendance", Toast.LENGTH_LONG).show();
        }

    
	

}
}
	}

	
}
	
//	protected void sendJson(final String result) {
//        Thread t = new Thread() {
//
//            public void run() {
//                Looper.prepare(); //For Preparing Message Pool for the child Thread
//                HttpClient client = new DefaultHttpClient();
//                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
//                HttpResponse response;
//                JSONObject json = new JSONObject();
//                Context context = getApplicationContext();
//                try {
//                    HttpPost post = new HttpPost(LOGIN_URL + result);
//                    client.execute(post);
//                    Log.d("posting", "posting to the URL");
//                  
//                    Toast.makeText(context, "posting", Toast.LENGTH_SHORT).show();
//                   
//                    StringEntity se = new StringEntity( json.toString());  
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(se);
//                    response = client.execute(post);
//                  
//
//                    /*Checking response */
//                    if(response!=null){
//                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
//                       // Toast.makeText(context, in.toString(), Toast.LENGTH_SHORT).show();
//
//                        //if(in==1){
//                        //	Toast.makeText(context, "The name was successfully read", Toast.LENGTH_SHORT);
//                        //}
//                    }
//
//                } catch(Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(context,"Error Cannot Estabilish Connection", Toast.LENGTH_SHORT).show();
//                }
//
//             //   Looper.loop(); //Loop in the message queue
//            }
//        };
//
//        t.start();      
//    }
//}




// http://x.oudar.free.fr/nfc/NFCTagReadingActivity.java
// http://tapintonfc.blogspot.com/2012/07/nfc-workshop-series-how-to-read-nfc-tag.html