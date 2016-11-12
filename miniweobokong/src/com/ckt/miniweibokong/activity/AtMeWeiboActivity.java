package com.ckt.miniweibokong.activity;

import java.util.ArrayList;
import java.util.List;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.adapter.AtMePaperAdapter;
import com.ckt.miniweibokong.adapter.FriendShipAdapter;
import com.ckt.miniweibokong.fragment.AtMeCommentFragment;
import com.ckt.miniweibokong.fragment.AtMeWeiBoFragment;
import com.ckt.miniweibokong.fragment.FollowerFragment;
import com.ckt.miniweibokong.fragment.FriendsFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class AtMeWeiboActivity extends FragmentActivity{
	private AtMePaperAdapter adapter;
	private List<Fragment> fragments=new ArrayList<Fragment>();
	private FragmentTransaction ftransaction;  
	private ImageView iv_menu;
	private TextView tv_head;
	private ImageView iv_logout;
	private long uid;
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
				tintManager.setTintColor(Color.parseColor("#3d9df6"));//深蓝1f91d3
				setContentView(R.layout.activity_friendship);
				   Intent it=getIntent();
			        int select=it.getIntExtra("select", 0);
			        uid=it.getLongExtra("uid", 0);
				//初始化控件
				initView();		
				 adapter = new AtMePaperAdapter(getSupportFragmentManager(),fragments);
			        
					
			        ViewPager pager = (ViewPager)findViewById(R.id.pager_friend);
			        pager.setAdapter(adapter);
			        
			        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator_friend);
			        indicator.setViewPager(pager);
			     
			        
			        if(select==0){
			        	Bundle bundle=new Bundle();
			        	bundle.putLong("uid", uid);
			        	fragments.get(0).setArguments(bundle);
			        	pager.setCurrentItem(0);
			        }else{
			        	Bundle bundle=new Bundle();
			        	bundle.putLong("uid", uid);
			        	fragments.get(1).setArguments(bundle);
			        	pager.setCurrentItem(1);
			        }
	}

	private void initView() {
		// TODO Auto-generated method stub
		iv_menu=(ImageView) findViewById(R.id.iv_menu);
		iv_logout=(ImageView) findViewById(R.id.iv_logout);
		tv_head=(TextView) findViewById(R.id.tv_head);
		tv_head.setText("与我有关");
		iv_menu.setVisibility(View.GONE);
		iv_logout.setVisibility(View.GONE);
		 fragments.add(new AtMeWeiBoFragment());
		 	fragments.add(new AtMeCommentFragment());
		 	Bundle bundle=new Bundle();
        	bundle.putLong("uid", uid);
        	fragments.get(0).setArguments(bundle);
        	fragments.get(1).setArguments(bundle);
	}
	
	
	
	
	
}
