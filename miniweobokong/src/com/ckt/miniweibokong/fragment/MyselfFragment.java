package com.ckt.miniweibokong.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemLongClickListener;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.activity.MainActivity;
import com.ckt.miniweibokong.activity.WeiBoViewer;
import com.ckt.miniweibokong.adapter.WeiboAdapter;
import com.ckt.miniweibokong.myview.PullToRefreshListView;
import com.ckt.miniweibokong.myview.PullToRefreshListView.OnRefreshListener;
import com.ckt.miniweibokong.util.Constants;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FavoritesAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;


public class MyselfFragment extends Fragment{
	
	 /** 当前 Token 信息 */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
    private FavoritesAPI mFavoritesAPI;
    private static long sinceId=0L;
	private static long maxId=0L;
	private final static int ADD_UP=0;
	private final static int ADD_DOWN=1;
	private static int add_type=ADD_UP;
    private ArrayList<Status> list_weibo=new ArrayList<Status>();
    private WeiboAdapter adapter;
    private PullToRefreshListView weiboListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		  // 获取当前已保存过的 Token
        mAccessToken = com.ckt.miniweibokong.util.AccessTokenKeeper.readAccessToken(getActivity());
        // 对statusAPI实例化
        mStatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        mFavoritesAPI= new FavoritesAPI(getActivity(), Constants.APP_KEY, mAccessToken);
        Handler handler=new Handler();
        adapter=new WeiboAdapter(getActivity(), handler, list_weibo);
        mStatusesAPI.userTimeline(sinceId, maxId, 5, 1, false, 0, false, mListener);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragment_hotmsg, container, false); 
		 initView(view);
		return view;
	}
	private void initView(View view) {
		// TODO Auto-generated method stub
		weiboListView=(PullToRefreshListView) view.findViewById(R.id.listview_hotmsg);
		weiboListView.setOnRefreshListener(new OnRefreshListener() {
			

			@Override
			public void onLoadMore() {
				new GetDataTask(getActivity(), 1).execute();
			}

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new GetDataTask(getActivity(), 0).execute();
			}
		});
		
		weiboListView.setAdapter(adapter);
		weiboListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id ) {
				
				Intent it=new Intent(getActivity(),WeiBoViewer.class);
					Bundle bundle=new Bundle();
					bundle.putSerializable("status", list_weibo.get(position-1));
					
					
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
				.setTitle("是否删除")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
						mStatusesAPI.destroy(Long.parseLong(list_weibo.get(position-1).id), mListener);
					}
				})
				.setNegativeButton("否", null
				)
				.create();
				builder.show();
				
				
				return true;
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
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy年MM月dd日  HH:mm");
					String date = format.format(new Date());
					// Call onRefreshComplete when the list has been refreshed.
					weiboListView.onRefreshComplete(date);
					
					//mainActivity.getAdapter().reLoad();
					mStatusesAPI.userTimeline(sinceId, maxId, 5, 1, false, 0, false, mListener);
					adapter.setItemList(list_weibo);
					
					//adapter.notifyDataSetChanged();
					
				} else if (index == 1) {
					add_type=ADD_DOWN;
					mStatusesAPI.userTimeline(sinceId, maxId, 5, 1, false, 0, false, mListener);
					adapter.setItemList(list_weibo);
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
	                Log.i("myself", response);
	            	if (response.startsWith("{\"statuses\"")) {
	                    // 调用 StatusList#parse 解析字符串成微博列表对象
	                    StatusList statuses = StatusList.parse(response);
	                    if (statuses != null && statuses.total_number > 0) {
	                    	ArrayList<Status> newlist=new ArrayList<Status>();
	                    	try {
	                    		if(add_type==ADD_UP){
	                    			newlist.addAll(statuses.statusList);
	                            	newlist.addAll(list_weibo);
	                    		}
	                    		else if(add_type==ADD_DOWN){
	                    			
	                            	newlist.addAll(list_weibo);
	                            	newlist.addAll(statuses.statusList);
	                    		}
	                    		
	                    		list_weibo=newlist;
							
//	                        	Toast.makeText(getActivity(), 
//	  	                              "list_status的长度"+list_weibo.size(), 
//	  	                                Toast.LENGTH_LONG).show();
	                    	
//	                    	Log.i("list_status.size()", list_status.size()+"");
	                    	sinceId=Long.parseLong(list_weibo.get(0).id) ;
	                    	maxId=Long.parseLong(list_weibo.get(list_weibo.size()-1).id);
	                    	Log.i("onreload", 3333+"");
	                    	Log.i("myssinceId", sinceId+"");
	                    	Log.i("mysmaxId", maxId+"");
	                    	
	                    	adapter.setItemList(list_weibo);
//	                    	adapter=new WeiboAdapter(mainActivity, handler, statuses.statusList);
//	                    	MainSecondActivity a = (MainSecondActivity) getActivity();
//	                        a.getAdapter().reLoad();
	                    	Toast.makeText(getActivity(), 
	                                "获取微博信息流成功, 条数: " + statuses.statusList.size(), 
	                                Toast.LENGTH_LONG).show();
	                    	} catch (Exception e) {
								// TODO: handle exception
								Toast.makeText(getActivity(), 
		                              "这已经最新的数据了，无更新", 
		                                Toast.LENGTH_LONG).show();
							}
	                    }
	                }else if(response.startsWith("{\"created_at\"")){
	                	Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();
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
	
	 public void onDestroy() {
	    	sinceId=0L;
			maxId=0L;
			
		add_type=ADD_UP;
			super.onDestroy();
	    	
	    };
}

