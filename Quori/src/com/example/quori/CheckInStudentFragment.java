package com.example.quori;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quori.R;

public class CheckInStudentFragment extends Fragment implements OnClickListener  {
	
	
	private Button inbutton;
	private Button outbutton;
	private Intent myIntent;
	private static final String checkin = "http://152.8.115.221/Quorigo/checkin.php?UID=";
	private static final String checkout = "http://152.8.115.221/Quorigo/checkout.php?UID=";
	public CheckInStudentFragment() {

	}

	public static final String ARG_SECTION_NUMBER = "section_number";
	private TextView mTextView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.check_attendance_layout,
				container, false);
		
		 inbutton = (Button) rootView.findViewById(R.id.checkinbtn);
	     inbutton.setOnClickListener(this);     
	     outbutton = (Button) rootView.findViewById(R.id.checkoutbtn);
	     outbutton.setOnClickListener(this);   
	     mTextView = (TextView) rootView.findViewById(R.id.TextView01);
	     
		
		return rootView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		
		super.onCreate(savedInstanceState);
		
		
		//myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		
  	  myIntent = new Intent(getActivity(), NFCReadActivity.class);
  	 

 	 
	  startActivity(myIntent);

	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 
		
		 
		  switch (v.getId()) {
	      case R.id.checkinbtn:
	    	
	    	  myIntent = new Intent(getActivity(), NFCReadActivity.class);
	    	  myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	  myIntent.putExtra ("check", checkin);
	    	 
	    	  Toast.makeText(this.getActivity(), 
	    			 
			            "Check In Mode", Toast.LENGTH_LONG).show();
	    	  startActivity(myIntent);
	        break;
	      case R.id.checkoutbtn:
	    	  myIntent = new Intent(getActivity(), NFCReadActivity.class);
	    	  myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	  myIntent.putExtra ("check", checkout);
	    	  Toast.makeText(this.getActivity(), 
			            "Check Out Mode", Toast.LENGTH_LONG).show();
	    	  startActivity(myIntent);
	        break;
	      }
	}

}