package com.ckt.miniweibokong.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;







import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.myview.RoundedImageView;
import com.ckt.miniweibokong.thread.AsyncImageLoader;
import com.ckt.miniweibokong.util.AsyncRequest;
import com.ckt.miniweibokong.util.DateUtilsDef;
import com.sina.weibo.sdk.openapi.models.Favorite;
import com.sina.weibo.sdk.openapi.models.Status;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeiboAdapter extends BaseAdapter{
	private ArrayList<Status> listStatus;
	private Context context;
	private Handler handler;
	  private LayoutInflater mInflater = null;
	  public AsyncImageLoader imageLoader;
	public WeiboAdapter(Context context,Handler handler,ArrayList<Status> listStatus){
		this.listStatus=listStatus;
		this.context=context;
		imageLoader = new AsyncImageLoader(context);
		this.handler=handler;
		 this.mInflater = LayoutInflater.from(context);
	}
	public WeiboAdapter(Context context,ArrayList<Favorite> listFavorite){
		if(listFavorite==null)listFavorite=new ArrayList<Favorite>();
		if(listStatus==null)listStatus=new ArrayList<Status>();
		for(int i=0;i<listFavorite.size();i++){
			listStatus.add(listFavorite.get(i).status);
		}
		
		this.context=context;
		imageLoader = new AsyncImageLoader(context);
		
		 this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listStatus.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listStatus.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		 ViewHolder holder=null;
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.weibo_item, null);
		        holder.iv_item_msg = (ImageView)convertView.findViewById(R.id.iv_item_msg);
		        holder.iv_item_user = (ImageView)convertView.findViewById(R.id.iv_item_user);
		        holder.tv_item_username = (TextView)convertView.findViewById(R.id.tv_item_username);
		        holder.tv_item_source = (TextView)convertView.findViewById(R.id.tv_item_source);
		        holder.tv_item_time = (TextView)convertView.findViewById(R.id.tv_item_time);
		        holder.tv_item_text = (TextView)convertView.findViewById(R.id.tv_item_text);
		        holder.tv_item_zhuanfa_name = (TextView)convertView.findViewById(R.id.tv_item_zhuanfa_name);
		        holder.tv_item_zhuanfa_msg = (TextView)convertView.findViewById(R.id.tv_item_zhuanfa_msg);
		        holder.iv_item_zhuanfa_msg = (ImageView)convertView.findViewById(R.id.iv_item_zhuanfa_msg);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		     String imgUrl_user="";
		   if(listStatus.get(position).user!=null){
			   imgUrl_user=listStatus.get(position).user.profile_image_url ;
		   }
		   
		    final String imgUrl_msg = listStatus.get(position).thumbnail_pic;
		    String imgUrl_zhuanfa_msg="";
		    if(listStatus.get(position).retweeted_status!=null){
		    	imgUrl_zhuanfa_msg=listStatus.get(position).retweeted_status.thumbnail_pic;
		    }
		  
		    holder.iv_item_user.setTag(imgUrl_user);
		    holder.iv_item_msg.setTag(imgUrl_msg);
		    holder.iv_item_zhuanfa_msg.setTag(imgUrl_zhuanfa_msg);
		    holder.iv_item_user.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		    holder.iv_item_msg.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		    holder.iv_item_zhuanfa_msg.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		    if (!TextUtils.isEmpty(imgUrl_user)) {
				Bitmap bitmap = imageLoader.loadImage(holder.iv_item_user, imgUrl_user);
				if (bitmap != null) {
					holder.iv_item_user.setImageBitmap(bitmap);
				}
			}
		    if(listStatus.get(position).retweeted_status!=null){
		    	if(imgUrl_zhuanfa_msg.equals("")){
		    		holder.iv_item_msg.setVisibility(View.GONE);
		    		holder.iv_item_zhuanfa_msg.setVisibility(View.GONE);
		    		holder.tv_item_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	holder.tv_item_zhuanfa_name.setVisibility(View.VISIBLE);
			    	holder.tv_item_zhuanfa_msg.setText(listStatus.get(position).retweeted_status.text);
			    	if(listStatus.get(position).retweeted_status.user!=null){
			    	holder.tv_item_zhuanfa_name.setText("@"+listStatus.get(position).retweeted_status.user.name+":");
			    	}
		    	}else{
			    	holder.iv_item_msg.setVisibility(View.GONE);
			    	holder.iv_item_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	holder.tv_item_zhuanfa_msg.setVisibility(View.VISIBLE);
			    	holder.tv_item_zhuanfa_name.setVisibility(View.VISIBLE);
			    	holder.tv_item_zhuanfa_msg.setText(listStatus.get(position).retweeted_status.text);
			    	holder.tv_item_zhuanfa_name.setText("@"+listStatus.get(position).retweeted_status.user.name+":");
			    	 if (!TextUtils.isEmpty(imgUrl_zhuanfa_msg)) {
							Bitmap bitmap = imageLoader.loadImage(holder.iv_item_zhuanfa_msg, imgUrl_zhuanfa_msg);
							if (bitmap != null) {
								holder.iv_item_zhuanfa_msg.setImageBitmap(bitmap);
							}
						}
			    	}
		    }else{
		    	if(imgUrl_msg.equals("")){
		    		
		    		holder.iv_item_msg.setVisibility(View.GONE);
		    		holder.iv_item_zhuanfa_msg.setVisibility(View.GONE);
			    	holder.tv_item_zhuanfa_msg.setVisibility(View.GONE);
			    	holder.tv_item_zhuanfa_name.setVisibility(View.GONE);
		    	}else{
		    	holder.iv_item_msg.setVisibility(View.VISIBLE);
		    	holder.iv_item_zhuanfa_msg.setVisibility(View.GONE);
		    	holder.tv_item_zhuanfa_msg.setVisibility(View.GONE);
		    	holder.tv_item_zhuanfa_name.setVisibility(View.GONE);
		    	 if (!TextUtils.isEmpty(imgUrl_msg)) {
						Bitmap bitmap = imageLoader.loadImage(holder.iv_item_msg, imgUrl_msg);
						if (bitmap != null) {
							holder.iv_item_msg.setImageBitmap(bitmap);
						}else{
							holder.iv_item_msg.setVisibility(View.GONE);
						}
					}
		    	}
		    }
		    
		    if (!TextUtils.isEmpty(imgUrl_msg)) {
				Bitmap bitmap = imageLoader.loadImage(holder.iv_item_msg, imgUrl_msg);
				if (bitmap != null) {
					holder.iv_item_msg.setImageBitmap(bitmap);
				}
			}
		    String time=DateUtilsDef.getShortTime(listStatus.get(position).created_at);

		            
		            if(listStatus.get(position).user!=null){
	        holder.tv_item_username.setText(listStatus.get(position).user.name);
		            }
	        holder.tv_item_source.setText(stringToSource(listStatus.get(position).source));
	        holder.tv_item_time.setText(time);

	        
	        holder.tv_item_text.setText(Html.fromHtml(listStatus.get(position).text));
		    
		                                                                                                
		    return convertView;
	}
		//ViewHolder静态类
		static class ViewHolder
		{
		   public ImageView iv_item_user;
		   public ImageView iv_item_msg;
		   public TextView tv_item_username;
		   public TextView tv_item_text;
		   public TextView tv_item_time;
		   public TextView tv_item_source;
		   public TextView tv_item_zhuanfa_name;
		   public TextView tv_item_zhuanfa_msg;
		   public ImageView iv_item_zhuanfa_msg;
		}
		public void setItemList(ArrayList<Status> list) {
			// TODO Auto-generated method stub
			//=list;
			this.listStatus.clear();
			this.listStatus.addAll(list);
			
			this.notifyDataSetChanged();
		}
		
		public void setFavoriteItem(ArrayList<Favorite> list){
			this.listStatus.clear();
			ArrayList<Status> newList=new ArrayList<Status>();
			for(int i=0;i<list.size();i++){
				newList.add(list.get(i).status);
			}
			this.listStatus.addAll(newList);
			
			this.notifyDataSetChanged();
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
		
		public ArrayList<Status> getStatusList(){
			return this.listStatus;
		}
		
		
		
}
