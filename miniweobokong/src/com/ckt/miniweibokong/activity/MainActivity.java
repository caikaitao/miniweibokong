package com.ckt.miniweibokong.activity;

import java.util.ArrayList;
import java.util.List;

import com.ckt.miniweibokong.R;


import com.ckt.miniweibokong.adapter.MyPagerAdapter;
import com.ckt.miniweibokong.adapter.MyPagerAdapter.OnReloadListener;
import com.ckt.miniweibokong.fragment.FavoritesFragment;
import com.ckt.miniweibokong.fragment.HomePageFragment;
import com.ckt.miniweibokong.fragment.MyselfFragment;
import com.ckt.miniweibokong.fragment.MoreFragment;
import com.ckt.miniweibokong.model.UserInfo;
import com.ckt.miniweibokong.myview.RoundedImageView;
import com.ckt.miniweibokong.sqlite.DataHelper;
import com.ckt.miniweibokong.util.AccessTokenKeeper;
import com.ckt.miniweibokong.util.Constants;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{
	private UsersAPI mUsersAPI;
	 private Oauth2AccessToken mAccessToken;
	private List<Fragment> fragments=new ArrayList<Fragment>();
	private long uid;
	private  ViewPager pager;
	private DataHelper dataHelper;
	//头部控件
	private TextView tv_head;
	private ImageView iv_menu;
	private ImageView iv_logout;
	private MyPagerAdapter adapter;
	//侧边菜单及其控件
	private SlidingMenu menu;
	private TextView tv_nickname;
	private TextView tv_guanzhushu;
	private TextView tv_fensishu;
	private TextView tv_weiboshu;
	private TextView tv_menu_friends;
	private TextView tv_menu_follower;
	private TextView tv_menu_myweibo;
	private TextView tv_menu_atme_weibo;
	private TextView tv_menu_atme_pinglun;
	//private TextView tv_menu_
	private RoundedImageView  headImageView;
	private Handler handler;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		//沉溺式标题栏设置
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setNavigationBarTintEnabled(true);
		tintManager.setTintColor(Color.parseColor("#3d9df6"));//深蓝1f91d3
		setContentView(R.layout.main);
		 handler=new Handler();
		 //初始化数据库操作类
		 dataHelper=DataHelper.getInstance(this);
		 Log.i("dataHelper", dataHelper.toString());
		//处理用户信息
			handlerUserInfo();
		//初始化控件
		initView();
		
		
		 adapter = new MyPagerAdapter(getSupportFragmentManager(),fragments);
        adapter.setOnReloadListener(new OnReloadListener()
		{
			@Override
			public void onReload()
			{
				fragments = null;
				List<Fragment> list = new ArrayList<Fragment>();
				 list.add(new HomePageFragment());
				   list.add(new MyselfFragment());
				 	list.add(new MoreFragment());
				 	
				adapter.setPagerItems(list);
				
			}
		});
		
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        
        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        tv_menu_myweibo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pager.setCurrentItem(1);
			}
		});
	}
	
	
	public void handlerUserInfo(){
		mAccessToken=AccessTokenKeeper.readAccessToken(this);
		mUsersAPI = new UsersAPI(this,Constants.APP_KEY , mAccessToken);
		   uid = Long.parseLong(mAccessToken.getUid());
          mUsersAPI.show(uid, mListener);
          
          long[] uids = { Long.parseLong(mAccessToken.getUid()) };
          mUsersAPI.counts(uids, mListener);
	}
	 /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
               // LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                	tv_nickname.setText(user.screen_name);
                	tv_head.setText(user.screen_name);
                    tv_fensishu.setText("粉丝:"+user.followers_count+"");
                    tv_guanzhushu.setText("关注:"+user.friends_count+"");
                    tv_weiboshu.setText("微博:"+user.statuses_count+"");
                    
                    headImageView.asyncHttpImage(handler,user.avatar_hd);
                    UserInfo userInfo=new UserInfo();
                    userInfo.setUserId(user.id);
                    userInfo.setUserName(user.screen_name);
                    userInfo.setFollowers_count(user.followers_count);
                    userInfo.setFriends_count(user.friends_count);
                    userInfo.setStatuses_count(user.statuses_count);
                    userInfo.setUserIcon(headImageView.getDrawable());
                    dataHelper.UpdateUserInfo(userInfo);
                    Log.i("userinfo",user.screen_name );
                } else {
                	//Toast.makeText(MainSecondActivity.this, "网络异常", 0).show();
                   
                }
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {
            //LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            
            Toast.makeText(MainActivity.this, "无网络连接", Toast.LENGTH_LONG).show();
        }
    };
        
	 public void initView() {
		
		
		 
		 tv_head=(TextView) findViewById(R.id.tv_head);
		 
		 iv_menu=(ImageView) findViewById(R.id.iv_menu);
		 
		 iv_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "showmenu", 0).show();
				menu.showMenu();
			}
		});
		 iv_logout=(ImageView) findViewById(R.id.iv_logout);
		 iv_logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//清除用户token
				 AccessTokenKeeper.clear(getApplicationContext());
				 Intent it=new Intent(MainActivity.this,WBAuthActivity.class);
				 startActivity(it);
				 MainActivity.this.finish();
				
			}
		});
		 iv_logout=(ImageView) findViewById(R.id.iv_logout);
		 //设置侧边菜单
		 // configure the SlidingMenu  
	        menu = new SlidingMenu(this);  
	        menu.setMode(SlidingMenu.LEFT);  
	        // 设置触摸屏幕的模式  
	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
	        menu.setShadowWidthRes(R.dimen.shadow_width);  
	        menu.setShadowDrawable(R.drawable.shadow);  
	  
	        // 设置滑动菜单视图的宽度  
	        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
	        // 设置渐入渐出效果的值  
	        menu.setFadeDegree(0.35f);  
	        menu.setBehindScrollScale(0.333f);
	        /** 
	         * SLIDING_WINDOW will include the Title/ActionBar in the content 
	         * section of the SlidingMenu, while SLIDING_CONTENT does not. 
	         */  
	        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
	        //为侧滑菜单设置布局  
	        menu.setMenu(R.layout.leftmenu);  
	        tv_nickname=(TextView) findViewById(R.id.nickNameTextView);
	        tv_fensishu=(TextView) findViewById(R.id.tv_fensishu);
	        tv_guanzhushu=(TextView) findViewById(R.id.tv_guanzhushu);
	        tv_weiboshu=(TextView) findViewById(R.id.tv_weiboshu);
	        tv_menu_myweibo=(TextView) findViewById(R.id.toolbox_title_myweibo);
	        tv_menu_follower=(TextView) findViewById(R.id.toolbox_title_follower);
	        tv_menu_friends=(TextView) findViewById(R.id.toolbox_title_friend);
	        tv_menu_atme_weibo=(TextView) findViewById(R.id.toolbox_title_atmeweibo);
	        tv_menu_atme_pinglun=(TextView) findViewById(R.id.toolbox_title_atmepinglun);
	        
	        
	        tv_menu_friends.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent it=new Intent(MainActivity.this,FriendshipsActivity.class);
					it.putExtra("select", 0);
					startActivity(it);
					
				}
			});
	        
	        tv_menu_follower.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent it=new Intent(MainActivity.this,FriendshipsActivity.class);
				
					
					
					it.putExtra("select", 1);
				
					startActivity(it);
				}
			});
	        tv_menu_atme_weibo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent it=new Intent(MainActivity.this,AtMeWeiboActivity.class);
				
					it.putExtra("select", 0);
				
					startActivity(it);
				}
			});
	        
	        tv_menu_atme_pinglun.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent it=new Intent(MainActivity.this,AtMeWeiboActivity.class);
				
					it.putExtra("select", 1);
				
					startActivity(it);
				}
			});
	        headImageView=(RoundedImageView) findViewById(R.id.headImageView);
	        if(!isNetworkConnected()){
	        	
	        	Toast.makeText(this, "网络异常", 0).show();
	        	List<UserInfo> userlist=dataHelper.GetUserList(false);
	        	Log.i("networkConnected", ""+userlist.size()+"+"+userlist.toString());
	        	Toast.makeText(this, ""+userlist.size()+"+"+userlist.toString(), 0).show();
	        	
	        	if(userlist!=null&&userlist.size()!=0){
	        	UserInfo userInfo=userlist.get(userlist.size()-1);
	        	tv_nickname.setText(userInfo.getUserName());
            	tv_head.setText(userInfo.getUserName());
                tv_fensishu.setText("粉丝:"+userInfo.getFollowers_count()+"");
                tv_guanzhushu.setText("关注:"+userInfo.getFriends_count()+"");
                tv_weiboshu.setText("微博:"+userInfo.getStatuses_count()+"");
	        	}
	        }
	        fragments.add(new HomePageFragment());
		 	fragments.add(new MyselfFragment());
		 	fragments.add(new FavoritesFragment());
	}
	 /**
	     * 检测网络是否可用
	     * @return
	     */
	    public boolean isNetworkConnected() {
	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo ni = cm.getActiveNetworkInfo();
	        return ni != null && ni.isConnectedOrConnecting();
	    }
	    
	    /**
		 * 获取适配器
		 * @return
		 */
		public MyPagerAdapter getAdapter()
		{
			return adapter;
		}
		
	
	
}
