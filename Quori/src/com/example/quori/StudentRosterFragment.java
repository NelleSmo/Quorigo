package com.example.quori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class StudentRosterFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public StudentRosterFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_main,
				container, false);
		

		return rootView;
	}
	
	

}