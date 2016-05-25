package com.mimp.android.ch0;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainTab03Fragment extends Fragment {
	
	private FragmentActivity activity;
	
	
	
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// TODO Auto-generated method stub
//	
//	
//	if(text==null){
//        Bundle args=getArguments();
//        text=args.getString("text");
//    }
//    if (view == null) {
//        view = inflater.inflate(R.layout.hello, null);
//    }
	View view =  inflater.inflate(R.layout.activity_main_tab03, container,false);	
	return view;
	
}


@Override
public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	activity = getActivity();
	
}


}
