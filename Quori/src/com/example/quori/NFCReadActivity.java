package com.example.quori;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
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
	private TextView mTextView;
	private static String MIME_TEXT_PLAIN = "text/plain";
	private String Result;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTextView= (TextView) findViewById(R.id.sp_nameTextView);
		
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
	           // mTextView.setText(result);
	            setResult(result);
	        }
	    }
		

	}
}




// http://x.oudar.free.fr/nfc/NFCTagReadingActivity.java
// http://tapintonfc.blogspot.com/2012/07/nfc-workshop-series-how-to-read-nfc-tag.html