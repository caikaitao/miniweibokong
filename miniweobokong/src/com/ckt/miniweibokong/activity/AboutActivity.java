package com.ckt.miniweibokong.activity;

import com.ckt.miniweibokong.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class AboutActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//沉溺式标题栏设置
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				SystemBarTintManager tintManager = new SystemBarTintManager(this);
				tintManager.setStatusBarTintEnabled(true);
				tintManager.setNavigationBarTintEnabled(true);
				tintManager.setTintColor(Color.parseColor("#1f91d3"));//深蓝1f91d3
				setContentView(R.layout.activity_about);
				
	}
}
