package com.example.quori;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckInStudentFragment extends Fragment {

	public CheckInStudentFragment() {
	}

	public static final String ARG_SECTION_NUMBER = "section_number";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.check_attendance_layout,
				container, false);
		

		return rootView;
	}
}
