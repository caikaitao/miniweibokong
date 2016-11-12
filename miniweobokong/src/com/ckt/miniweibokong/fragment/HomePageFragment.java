package com.ckt.miniweibokong.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;












import com.ckt.miniweibokong.R;




import com.ckt.miniweibokong.activity.AboutActivity;
import com.ckt.miniweibokong.activity.MainActivity;
import com.ckt.miniweibokong.activity.PostWeiboActivity;
import com.ckt.miniweibokong.activity.WeiBoViewer;
import com.ckt.miniweibokong.adapter.WeiboAdapter;
import com.ckt.miniweibokong.myview.PullToRefreshListView;


import com.ckt.miniweibokong.myview.PullToRefreshListView.OnRefreshListener;
import com.ckt.miniweibokong.util.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;












import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomePageFragment extends Fragment{
	
	
	private PullToRefreshListView weiboListView;
	//Floating Action Button控件
	private com.faizmalkani.floatingactionbutton.FloatingActionButton mFab;
	private TextView tv_heart;
	private TextView tv_write;
	private TextView tv_wuhua;
	private ImageView iv_heart;
	private ImageView iv_write;
	private Boolean fabOpened;
	private SharedPreferences mySharedPreferences;
	private MainActivity mainActivity ;
	private WeiboAdapter adapter;
	private static long sinceId=0L;
	private static long maxId=0L;
	private final static int ADD_UP=0;
	private final static int ADD_DOWN=1;
	private static int add_type=ADD_UP;
	private FavoritesAPI mFavoritesAPI;
	public  ArrayList<Status> list_status=new ArrayList<Status>();
	private Handler handler;
	 /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mainActivity= (MainActivity) getActivity();
		handler = new Handler();
		  // 获取当前已保存过的 Token
        mAccessToken = com.ckt.miniweibokong.util.AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        mFavoritesAPI=new FavoritesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        mySharedPreferences = getActivity().getSharedPreferences("weibo", 
    			Activity.MODE_PRIVATE); 
        
        adapter=new WeiboAdapter(mainActivity, handler, list_status);
        mStatusesAPI.friendsTimeline(sinceId, maxId, 5, 1, false, 0, false, mListener);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragment_homepage, container, false); 
		 initView(view);
		return view;
	}
	
	 private void initView(View view) {
		// TODO Auto-generated method stub
		 fabOpened=false;
		 mFab = (com.faizmalkani.floatingactionbutton.FloatingActionButton) view.findViewById(R.id.fabbutton);
			tv_heart=(TextView) view.findViewById(R.id.tv_like);
			tv_write=(TextView) view.findViewById(R.id.tv_write);
			iv_heart=(ImageView)view.findViewById(R.id.iv_head);
			iv_write=(ImageView) view.findViewById(R.id.iv_write);
			tv_wuhua=(TextView) view.findViewById(R.id.tv_wuhua);
			tv_heart.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent it=new Intent(getActivity(),AboutActivity.class);
					startActivity(it);
				}
			});
			tv_write.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent it=new Intent(getActivity(),PostWeiboActivity.class);
							startActivity(it);
							
						}
					});
			tv_wuhua.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					closeMenu(mFab);
				}
			});
			weiboListView=(PullToRefreshListView) view.findViewById(R.id.weibolist);
			// 设置要刷新列表时要调用的监听器。
			weiboListView.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					// Do work to refresh the list here.
					new GetDataTask(getActivity(), 0).execute();
				}

				@Override
				public void onLoadMore() {
					new GetDataTask(getActivity(), 1).execute();
				}
			});
//			mListItems = new LinkedList<String>();
//			mListItems.addAll(Arrays.asList(mStrings));
//
//			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//					android.R.layout.simple_list_item_1, mListItems);

			weiboListView.setAdapter(adapter);
			//listview点击item事件
			weiboListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id ) {
					
					Intent it=new Intent(mainActivity,WeiBoViewer.class);
						Bundle bundle=new Bundle();
						bundle.putSerializable("status", list_status.get(position-1));
						
						
						it.putExtras(bundle);
						startActivity(it);
					
				}
			});
			
			weiboListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						final int position, long arg3) {
					// TODO Auto-generated method stub
					AlertDialog builder=new AlertDialog.Builder(getActivity())
					.setTitle("是否收藏")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							mFavoritesAPI.create(Long.parseLong(list_status.get(position-1).id), mListener);
						}
					})
					.setNegativeButton("否", null
					)
					.create();
					builder.show();
					
					
					return true;
				}
			});
			mFab.listenTo(weiboListView);
			mFab.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!fabOpened){
						openMenu(v);
					}else{
						closeMenu(v);
					}
					//Toast.makeText(MainActivity.this, "������������������", 1).show();
				}
			});
	}

	 public void openMenu(View v){
			com.nineoldandroids.animation.ObjectAnimator animator=com.nineoldandroids.animation.ObjectAnimator.ofFloat(v, "rotation", 0,-155,-135);
			animator.setDuration(500);
			animator.start();
			tv_heart.setVisibility(View.VISIBLE);
			tv_write.setVisibility(View.VISIBLE);
			iv_write.setVisibility(View.VISIBLE);
			iv_heart.setVisibility(View.VISIBLE);
			tv_wuhua.setVisibility(View.VISIBLE);
			AlphaAnimation alphaAnimation=new AlphaAnimation(0, 0.7f);
			alphaAnimation.setDuration(500);
			alphaAnimation.setFillAfter(true);
			tv_wuhua.startAnimation(alphaAnimation);
//			tv_heart.startAnimation(alphaAnimation);
//			tv_write.startAnimation(alphaAnimation);
//			iv_write.startAnimation(alphaAnimation);
//			iv_heart.startAnimation(alphaAnimation);
			fabOpened=true;
		}
		public void closeMenu(View v){
			com.nineoldandroids.animation.ObjectAnimator animator=com.nineoldandroids.animation.ObjectAnimator.ofFloat(v, "rotation", -135,20,0);
			animator.setDuration(500);
			animator.start();
			AlphaAnimation alphaAnimation=new AlphaAnimation(0.7f, 0);
			alphaAnimation.setDuration(500);
			tv_wuhua.startAnimation(alphaAnimation);
//			tv_heart.startAnimation(alphaAnimation);
//			tv_write.startAnimation(alphaAnimation);
//			iv_write.startAnimation(alphaAnimation);
//			iv_heart.startAnimation(alphaAnimation);
			tv_wuhua.setVisibility(View.GONE);
			tv_heart.setVisibility(View.GONE);
			tv_write.setVisibility(View.GONE);
			iv_write.setVisibility(View.GONE);
			iv_heart.setVisibility(View.GONE);
			fabOpened=false;
		}
	@Override  
	    public void onActivityCreated(Bundle savedInstanceState)  
	    {  
	        super.onActivityCreated(savedInstanceState);  
	    }  
	
	//异步处理返回数据
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private Context context;
		private int index;

		public GetDataTask(Context context, int index) {
			this.context = context;
			this.index = index;
		}
		
		 
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				
				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (index == 0) {
				// 将字符串“Added after refresh”添加到顶部
				//mListItems.addFirst("Added after refresh...");
				add_type=ADD_UP;
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy年MM月dd日  HH:mm");
				String date = format.format(new Date());
				// Call onRefreshComplete when the list has been refreshed.
				weiboListView.onRefreshComplete(date);
				
				//mainActivity.getAdapter().reLoad();
				mStatusesAPI.friendsTimeline(sinceId, 0L, 5, 1, false, 0, false, mListener);
				adapter.setItemList(list_status);
				
				//adapter.notifyDataSetChanged();
				
			} else if (index == 1) {
				add_type=ADD_DOWN;
				mStatusesAPI.friendsTimeline(0L, maxId-1, 5, 1, false, 0, false, mListener);
				adapter.setItemList(list_status);
				//mListItems.addLast("Added after loadmore...");
				weiboListView.onLoadMoreComplete();
			}

			super.onPostExecute(result);
		}
	}
	
	/**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                //LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                    	ArrayList<Status> newlist=new ArrayList<Status>();
                    	try {
                    		if(add_type==ADD_UP){
                    			newlist.addAll(statuses.statusList);
                            	newlist.addAll(list_status);
                    		}
                    		else if(add_type==ADD_DOWN){
                    			
                            	newlist.addAll(list_status);
                            	newlist.addAll(statuses.statusList);
                    		}
                    		
                        	list_status=newlist;
						
//                        	Toast.makeText(mainActivity, 
//  	                              "list_status的长度"+list_status.size(), 
//  	                                Toast.LENGTH_LONG).show();
                    	
//                    	Log.i("list_status.size()", list_status.size()+"");
                    	sinceId=Long.parseLong(list_status.get(0).id) ;
                    	maxId=Long.parseLong(list_status.get(list_status.size()-1).id);
                    	Log.i("onreload", 3333+"");
                    	Log.i("sinceId", sinceId+"");
                    	Log.i("maxId", maxId+"");
                    	
                    	adapter.setItemList(newlist);
//                    	adapter=new WeiboAdapter(mainActivity, handler, statuses.statusList);
//                    	MainSecondActivity a = (MainSecondActivity) getActivity();
//                        a.getAdapter().reLoad();
                    	Toast.makeText(mainActivity, 
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(), 
                                Toast.LENGTH_LONG).show();
                    	} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(mainActivity, 
	                              "这已经最新的数据了，无更新", 
	                                Toast.LENGTH_LONG).show();
						}
                    }
                }else if(response.startsWith("{\"status\"")){
                	  Toast.makeText(mainActivity, "收藏成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mainActivity, response, Toast.LENGTH_LONG).show();
                }
            }
        }
        
        @Override
        public void onWeiboException(WeiboException e) {
          //  LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(getActivity(), info.toString(), Toast.LENGTH_LONG).show();
        }
    };
    
   
    public void onDestroy() {
    	sinceId=0L;
		maxId=0L;
		
	add_type=ADD_UP;
		super.onDestroy();
    	
    };
	
}
