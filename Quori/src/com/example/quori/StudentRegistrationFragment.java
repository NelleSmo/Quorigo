package com.example.quori;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */

public class StudentRegistrationFragment extends Fragment {

	
	public static final String ARG_SECTION_NUMBER = "section_number";
	private Intent myIntent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_student_registration,
				container, false);
	
		



		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		myIntent = new Intent(getActivity(), NFCWriteActivity.class);
		startActivity(myIntent);
	}
	
	 
	
}