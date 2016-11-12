package com.ckt.miniweibokong.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.activity.WeiBoViewer;
import com.ckt.miniweibokong.adapter.FriendsAdapter;
import com.ckt.miniweibokong.myview.PullToRefreshListView;
import com.ckt.miniweibokong.myview.PullToRefreshListView.OnRefreshListener;
import com.ckt.miniweibokong.util.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.FriendsList;
import com.sina.weibo.sdk.openapi.models.User;

public class FollowerFragment extends Fragment{
	 /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private FriendshipsAPI mFriendshipsAPI;
    private static long sinceId=0L;
	private static long maxId=0L;
	private final static int ADD_UP=0;
	private final static int ADD_DOWN=1;
	private static int add_type=ADD_UP;
	private static long UID=0;
	private static int next_cursor=0;
	private static int curror=0;
	private static int previous_cursor=0;
    private ArrayList<User> list_friends=new ArrayList<User>();
    private FriendsAdapter adapter;
    private PullToRefreshListView weiboListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Bundle bundle=FollowerFragment.this.getArguments();
		if(bundle!=null){
		UID=bundle.getLong("uid");
		}
		  // 获取当前已保存过的 Token
        mAccessToken = com.ckt.miniweibokong.util.AccessTokenKeeper.readAccessToken(getActivity());
        UID=Long.parseLong(mAccessToken.getUid());
        // 对statusAPI实例化
        mFriendshipsAPI = new FriendshipsAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        Handler handler=new Handler();
        adapter=new FriendsAdapter(getActivity(), handler, list_friends);
        mFriendshipsAPI.followers(UID, 10, curror, false, mListener);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragment_friendship, container, false); 
		 initView(view);
		return view;
	}
	private void initView(View view) {
		// TODO Auto-generated method stub
		weiboListView=(PullToRefreshListView) view.findViewById(R.id.listview_friends);
		weiboListView.setOnRefreshListener(new OnRefreshListener() {
			

			@Override
			public void onLoadMore() {
				new GetDataTask(getActivity(), 1).execute();
			}

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				//new GetDataTask(getActivity(), 0).execute();
				weiboListView.onRefreshComplete(null);
				Toast.makeText(getActivity(), "这是最新的了", 0).show();
			}
		});
		
		weiboListView.setAdapter(adapter);
		weiboListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id ) {
				
//				Intent it=new Intent(getActivity(),WeiBoViewer.class);
//					Bundle bundle=new Bundle();
//					bundle.putSerializable("status", list_friends.get(position-1));
//					
//					
//					it.putExtras(bundle);
//					startActivity(it);
				
			}
		});
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
					curror=previous_cursor;
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy年MM月dd日  HH:mm");
					String date = format.format(new Date());
					// Call onRefreshComplete when the list has been refreshed.
					weiboListView.onRefreshComplete(date);
					
					//mainActivity.getAdapter().reLoad();
					if(curror!=0){
						 mFriendshipsAPI.followers(UID,  10, curror, false, mListener);
						 }else{
							 Toast.makeText(getActivity(), "已经是最新数据了", 0).show();
						 }
					
					adapter.setItemList(list_friends);
					
					//adapter.notifyDataSetChanged();
					
				} else if (index == 1) {
					add_type=ADD_DOWN;
					curror=next_cursor;
					 mFriendshipsAPI.followers(UID,  10, curror, false, mListener);
					adapter.setItemList(list_friends);
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
	            	if (response.startsWith("{\"users\"")) {
	            		Log.i("response+follower", response);
	                    // 调用 StatusList#parse 解析字符串成微博列表对象
	                    FriendsList userlist = FriendsList.parse(response);
	                    if (userlist != null && userlist.total_number > 0) {
	                    	ArrayList<User> newlist=new ArrayList<User>();
	                    	try {
	                    		next_cursor=Integer.parseInt(userlist.next_cursor);
	                    		previous_cursor=Integer.parseInt(userlist.previous_cursor);
	                    		if(add_type==ADD_UP){
	                    			newlist.addAll(userlist.userList);
	                            	newlist.addAll(list_friends);
	                    		}
	                    		else if(add_type==ADD_DOWN){
	                    			
	                            	newlist.addAll(list_friends);
	                            	newlist.addAll(userlist.userList);
	                    		}
	                    		
	                    		list_friends=newlist;
							
	                        	Toast.makeText(getActivity(), 
	  	                              "list_user的长度"+list_friends.size(), 
	  	                                Toast.LENGTH_LONG).show();
	                    	
//	                    	Log.i("list_status.size()", list_status.size()+"");
	                    	sinceId=Long.parseLong(list_friends.get(0).id) ;
	                    	maxId=Long.parseLong(list_friends.get(list_friends.size()-1).id);
	                    	Log.i("onreload", 3333+"");
	                    	Log.i("sinceId", sinceId+"");
	                    	Log.i("maxId", maxId+"");
	                    	
	                    	adapter.setItemList(newlist);
//	                    	adapter=new WeiboAdapter(mainActivity, handler, statuses.statusList);
//	                    	MainSecondActivity a = (MainSecondActivity) getActivity();
//	                        a.getAdapter().reLoad();
	                    	
	                    	} catch (Exception e) {
								// TODO: handle exception
								Toast.makeText(getActivity(), 
		                              "这已经最新的数据了，无更新", 
		                                Toast.LENGTH_LONG).show();
							}
	                    }
	                }else {
	                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
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
		
	@Override  
    public void onActivityCreated(Bundle savedInstanceState)  
    {  
        super.onActivityCreated(savedInstanceState);  
    }  
}
