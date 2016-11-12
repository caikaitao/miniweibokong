package com.ckt.miniweibokong.activity;

import java.util.Calendar;
import java.util.Date;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.thread.AsyncImageLoader;
import com.ckt.miniweibokong.util.Constants;
import com.ckt.miniweibokong.util.DateUtilsDef;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class WeiBoViewer extends Activity{
	private Status status;
	private com.faizmalkani.floatingactionbutton.FloatingActionButton mFab;
	 private AsyncImageLoader imageLoader;
	private LinearLayout dialogView;
	private EditText et_dialog;
	 private Boolean fabOpened;
	 ScrollView parent;
	 private AlertDialog dialog;
//	 private Dialog zhuanfaDialog;
//	 private Dialog pinglunDialog;
	 private LinearLayout ll_viewer_commit;
	 /** 当前 Token 信息 */
	    private Oauth2AccessToken mAccessToken;
	    /** 用于获取微博信息流等操作的API */
	    private CommentsAPI mCommentsAPI;
	   private  StatusesAPI  mStatusesAPI;
	   private TextView tv_viewer_zhuanfa_msg;
	   private TextView tv_viewer_zhuanfa_name;
	   private ImageView iv_viewer_zhuanfa_msg;
	private ImageView iv_viewer_msg;
	private ImageView iv_viewer_user;
	private TextView tv_viewer_wuhua;
	private TextView tv_viewer_zhuanfa;
	private TextView tv_viewer_pinglun;
	private TextView tv_viewer_user;
	private TextView tv_viewer_text;
	private TextView tv_viewer_source;
	private TextView tv_viewer_time;
	private ImageView iv_viewer_zhuanfa;
	private ImageView iv_viewer_pinlun;
	private Handler handler;
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
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				 parent =  (ScrollView) inflater.inflate(R.layout.weibo_viewer, null);
		setContentView(parent);
		handler=new Handler();
		Intent intent = this.getIntent(); 
		imageLoader=new AsyncImageLoader(this);
		status=(Status)intent.getSerializableExtra("status");
		  // 获取当前已保存过的 Token
        mAccessToken = com.ckt.miniweibokong.util.AccessTokenKeeper.readAccessToken(this);
        // 对statusAPI实例化
        mCommentsAPI = new CommentsAPI(this, Constants.APP_KEY, mAccessToken);
        mStatusesAPI=new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
		initView();
	}
	private void initView() {
		// TODO Auto-generated method stub
		
		ll_viewer_commit=(LinearLayout) findViewById(R.id.ll_viewer_commit);
		iv_viewer_msg=(ImageView) findViewById(R.id.iv_viewer_msg);
		iv_viewer_user=(ImageView) findViewById(R.id.iv_viewer_user);
		tv_viewer_zhuanfa_msg=(TextView) findViewById(R.id.tv_item_zhuanfa_msg);
		tv_viewer_zhuanfa_name=(TextView) findViewById(R.id.tv_item_zhuanfa_name);
		iv_viewer_zhuanfa_msg=(ImageView) findViewById(R.id.iv_item_zhuanfa_msg);
		tv_viewer_user=(TextView) findViewById(R.id.tv_viewer_username);
		tv_viewer_source=(TextView) findViewById(R.id.tv_viewer_source);
		tv_viewer_text=(TextView) findViewById(R.id.tv_viewer_text);
		tv_viewer_time=(TextView) findViewById(R.id.tv_viewer_time);
		 dialogView=(LinearLayout) LayoutInflater.from(WeiBoViewer.this).inflate(R.layout.view_dialog, null);
		 et_dialog=(EditText) dialogView.findViewById(R.id.et_viewer_dialog);
		 final String imgUrl_user =status.user.profile_image_url ;
		    final String imgUrl_msg = status.original_pic;
		    String imgUrl_zhuanfa_msg="";
		    if(status.retweeted_status!=null){
		    	imgUrl_zhuanfa_msg=status.retweeted_status.original_pic;
		    }
		  
		    
		    
		   iv_viewer_user.setTag(imgUrl_user);
		    iv_viewer_msg.setTag(imgUrl_msg);
		    iv_viewer_zhuanfa_msg.setTag(imgUrl_zhuanfa_msg);
		    iv_viewer_zhuanfa_msg.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		   iv_viewer_user.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		   iv_viewer_msg.setImageResource(R.drawable.default_img);
//		    AsyncRequest.asyncHttpImage(holder.iv_item_user, handler, listStatus.get(position).user.profile_image_url);
//		    AsyncRequest.asyncHttpImage(holder.iv_item_msg, handler, listStatus.get(position).thumbnail_pic);
		    if (!TextUtils.isEmpty(imgUrl_user)) {
				Bitmap bitmap = imageLoader.loadImage(iv_viewer_user, imgUrl_user);
				if (bitmap != null) {
					iv_viewer_user.setImageBitmap(bitmap);
				}
			}
		    if(status.retweeted_status!=null){
		    	if(imgUrl_zhuanfa_msg.equals("")){
		    		iv_viewer_msg.setVisibility(View.GONE);
		    		iv_viewer_zhuanfa_msg.setVisibility(View.GONE);
		    		tv_viewer_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	tv_viewer_zhuanfa_name.setVisibility(View.VISIBLE);
			    	tv_viewer_zhuanfa_msg.setText(status.retweeted_status.text);
			    	tv_viewer_zhuanfa_name.setText("@"+status.retweeted_status.user.name+":");
		    	}else{
			    	iv_viewer_msg.setVisibility(View.GONE);
			    	iv_viewer_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	tv_viewer_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	tv_viewer_zhuanfa_name.setVisibility(View.VISIBLE);
			    	tv_viewer_zhuanfa_msg.setText(status.retweeted_status.text);
			    	tv_viewer_zhuanfa_name.setText("@"+status.retweeted_status.user.name+":");
			    	 if (!TextUtils.isEmpty(imgUrl_zhuanfa_msg)) {
							final Bitmap bitmap = imageLoader.loadImage(iv_viewer_zhuanfa_msg, imgUrl_zhuanfa_msg);
							if (bitmap != null) {
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										iv_viewer_zhuanfa_msg.setImageBitmap(bitmap);
									}
								});
								
							}
						}
			    	}
		    }else{
		    	if(imgUrl_msg.equals("")){
		    		iv_viewer_msg.setVisibility(View.GONE);
		    		iv_viewer_zhuanfa_msg.setVisibility(View.GONE);
			    	tv_viewer_zhuanfa_msg.setVisibility(View.GONE);
			    	tv_viewer_zhuanfa_name.setVisibility(View.GONE);
		    	}else{
		    	iv_viewer_msg.setVisibility(View.VISIBLE);
		    	iv_viewer_zhuanfa_msg.setVisibility(View.GONE);
		    	tv_viewer_zhuanfa_msg.setVisibility(View.GONE);
		    	tv_viewer_zhuanfa_name.setVisibility(View.GONE);
		    	 if (!TextUtils.isEmpty(imgUrl_msg)) {
						final Bitmap bitmap = imageLoader.loadImage(iv_viewer_msg, imgUrl_msg);
						if (bitmap != null) {
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									iv_viewer_msg.setImageBitmap(bitmap);
								}
							});
							
						}
					}
		    	}
		    }
//		    if (!TextUtils.isEmpty(imgUrl_msg)) {
//				Bitmap bitmap = imageLoader.loadImage(iv_viewer_msg, imgUrl_msg);
//				if (bitmap != null) {
//					iv_viewer_msg.setImageBitmap(bitmap);
//				}
//			}
		    String time=DateUtilsDef.getShortTime(status.created_at);
				  
		            
		            
		            
		            
	        tv_viewer_user.setText(status.user.name);
	        tv_viewer_source.setText(stringToSource(status.source));
	        tv_viewer_time.setText(time);
	      
	       tv_viewer_text.setText(status.text);
        
	       fabOpened=false;
			 mFab = (com.faizmalkani.floatingactionbutton.FloatingActionButton)findViewById(R.id.viewer_fabbutton);
				tv_viewer_pinglun=(TextView) findViewById(R.id.tv_viewer_pinglun);
				tv_viewer_zhuanfa=(TextView) findViewById(R.id.tv_viewer_zhuanfa);
				iv_viewer_zhuanfa=(ImageView)findViewById(R.id.iv_viewer_zhuanfa);
				iv_viewer_pinlun=(ImageView) findViewById(R.id.iv_viewer_pinglun);
				tv_viewer_wuhua=(TextView) findViewById(R.id.tv_viewer_wuhua);
				
				tv_viewer_pinglun.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						try {
							if(dialog!=null){
								dialog.dismiss();
								dialog=null;

							}
							if(dialog==null){ 
								
								dialog=new AlertDialog.Builder(WeiBoViewer.this)
							.setTitle("评论微博")
							.setPositiveButton("评论", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									closeMenu(mFab);
									mCommentsAPI.create(et_dialog.getText().toString().trim(), Long.parseLong(status.id), true, mListener);
									Toast.makeText(WeiBoViewer.this, et_dialog.getText(), 1).show();
									WeiBoViewer.this.finish();
									
								}

								
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									WeiBoViewer.this.finish();
								}
							})
							.setView(dialogView)
							.show();
							}else{
								dialog.show();
							}
						} catch (Exception e) {
							// TODO: handle exception
							WeiBoViewer.this.finish();
						}
						
						
						Toast.makeText(WeiBoViewer.this, "pinglun", 1).show();
					}
				});
				tv_viewer_zhuanfa.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								//ll_viewer_commit.setVisibility(View.VISIBLE);
								try {
									if(dialog!=null){
										dialog.dismiss();
										dialog=null;
//										removeDialog(pinglunDialog.getCurrentFocus().getId());
//										pinglunDialog.dismiss();pinglunDialog=null;
//										parent.removeAllViews();
									}
									if(dialog==null){
										
										dialog=new AlertDialog.Builder(WeiBoViewer.this)
									.setTitle("转发微博")
									.setPositiveButton("转发", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											closeMenu(mFab);
											mStatusesAPI.repost(Long.parseLong(status.id), et_dialog.getText().toString().trim(), 2, mListener);
											Toast.makeText(WeiBoViewer.this, et_dialog.getText(), 1).show();
											WeiBoViewer.this.finish();
											
										}

										
									})
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											
										}
									})
									.setView(dialogView)
									.show();
										
									}else{
										dialog.show();
									}
								} catch (Exception e) {
									// TODO: handle exception
									WeiBoViewer.this.finish();
								}
								
								Toast.makeText(WeiBoViewer.this, "tv_viewer_zhuanfa", 1).show();
							}
						});
				tv_viewer_wuhua.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						closeMenu(mFab);
					}
				});
				ListView weiboListView=(ListView) findViewById(R.id.lv_viewer);
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
			tv_viewer_pinglun.setVisibility(View.VISIBLE);
			tv_viewer_zhuanfa.setVisibility(View.VISIBLE);
			iv_viewer_pinlun.setVisibility(View.VISIBLE);
			iv_viewer_zhuanfa.setVisibility(View.VISIBLE);
			tv_viewer_wuhua.setVisibility(View.VISIBLE);
			AlphaAnimation alphaAnimation=new AlphaAnimation(0, 0.7f);
			alphaAnimation.setDuration(500);
			alphaAnimation.setFillAfter(true);
			tv_viewer_wuhua.startAnimation(alphaAnimation);
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
			tv_viewer_wuhua.startAnimation(alphaAnimation);
//			tv_heart.startAnimation(alphaAnimation);
//			tv_write.startAnimation(alphaAnimation);
//			iv_write.startAnimation(alphaAnimation);
//			iv_heart.startAnimation(alphaAnimation);
			tv_viewer_wuhua.setVisibility(View.GONE);
			tv_viewer_pinglun.setVisibility(View.GONE);
			tv_viewer_zhuanfa.setVisibility(View.GONE);
			iv_viewer_pinlun.setVisibility(View.GONE);
			iv_viewer_zhuanfa.setVisibility(View.GONE);
			fabOpened=false;
		}
	
	public String stringToSource(String text){
		String str="";
		
		if(text!=null && text!=""){
			int start=0;
			int t1=0;
			int t2=0;
		
			int end=0;
			for(int i=0;i<text.length();i++){
				if(text.charAt(i)=='>'){
					t1++;
					if(t1==1){
					start=i+1;
					}
				}
				if(text.charAt(i)=='<'){
					t2++;
					if(t2==2){
						end=i;
					}
					
				}
			}
			str=text.substring(start, end);
		}
		return str;
	}
	
	 /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            //LogUtil.i(TAG, response);
            if (!TextUtils.isEmpty(response)) {
               
               
                    Toast.makeText(WeiBoViewer.this,
                            "操作成功" + response, 
                            Toast.LENGTH_LONG).show();
                
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
           // LogUtil.e(TAG, e.getMessage());
        }
    };
}
