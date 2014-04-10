package com.example.quori;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static final String TAG = "NFCReadTag";  
    private NfcAdapter mNfcAdapter;  
   // private IntentFilter[] mNdefExchangeFilters;  
    private PendingIntent mNfcPendingIntent;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		//NFC Stuff
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);  

        
        //NfcAdapter.NfcAdapter.ACTION_NDEF_DISCOVERED can be used see notes on android site
        //IntentFilter scanner = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		setIntent(getIntent());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
//
    protected void onResume() {  
        super.onResume();  
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,  
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);  
      } 
    protected void onNewIntent(Intent intent) {
    	  String action = intent.getAction();
    	  if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)){
    	    Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    	    NdefMessage[] messages;
    	    if (rawMsgs != null) {
    	      messages = new NdefMessage[rawMsgs.length];
    	      for (int i = 0; i < rawMsgs.length; i++) {
    	        messages[i] = (NdefMessage) rawMsgs[i];     
    	        // To get a NdefRecord and its different properties from a NdefMesssage   
    	     NdefRecord record = messages[i].getRecords()[i];
    	     byte[] id = record.getId();
    	     short tnf = record.getTnf();
    	     byte[] type = record.getType();
    	     String message = getTextData(record.getPayload());
    	     Toast.makeText(this, message, Toast.LENGTH_LONG);
    	      }
    	  
    	    }
    	  }
    	}
 // Decoding a payload containing text
    private String getTextData(byte[] payload) {
      if(payload == null) 
        return null;
      try {
        String encoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langageCodeLength = payload[0] & 0077;
        return new String(payload, langageCodeLength + 1, payload.length - langageCodeLength - 1, encoding);     
      } catch(Exception e) {
        e.printStackTrace();
      }
      return null;
    }
//   

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a StudentRosterFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			Bundle args = new Bundle();
			
			switch(position){
			case 0:
				fragment = new CheckInStudentFragment();
				args.putInt(StudentRosterFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			//	break;
			case 1:
				fragment = new StudentRosterFragment();
				args.putInt(StudentRosterFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
				//break;
			case 2:
				fragment = new StudentRegistrationFrament();
				args.putInt(StudentRosterFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
				//break;
			}
			
			return null;
			
			
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

}

class CheckInStudentFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public CheckInStudentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.check_attendance_layout,
				container, false);
		

		return rootView;
	}
	
}