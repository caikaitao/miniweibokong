package com.ckt.miniweibokong.fragment;

import com.ckt.miniweibokong.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MoreFragment extends Fragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragment_more, container, false); 
		// initView(view);
		return view;
	}
	@Override  
    public void onActivityCreated(Bundle savedInstanceState)  
    {  
        super.onActivityCreated(savedInstanceState);  
    }  
}
