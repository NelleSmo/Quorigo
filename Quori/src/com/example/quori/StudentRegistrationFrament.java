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

public class StudentRegistrationFrament extends Fragment {

	
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.student_profile_fragment,
				container, false);
	
		

		return rootView;
	}
	
	 
	
}