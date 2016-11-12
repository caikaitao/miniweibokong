package com.ckt.miniweibokong.adapter;

import java.util.ArrayList;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.thread.AsyncImageLoader;
import com.sina.weibo.sdk.openapi.models.User;

import android.content.Context;
import android.graphics.Bitmap;
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

public class FriendsAdapter extends BaseAdapter{
	private ArrayList<User> listStatus;
	private Context context;
	private Handler handler;
	  private LayoutInflater mInflater = null;
	  private AsyncImageLoader imageLoader;
	public FriendsAdapter(Context context,Handler handler,ArrayList<User> listStatus){
		this.listStatus=listStatus;
		this.context=context;
		imageLoader = new AsyncImageLoader(context);
		this.handler=handler;
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
		 Log.i("viewHolder", "viewHolder");
		    if(convertView == null)
		    {
		        holder = new ViewHolder();
		        convertView = mInflater.inflate(R.layout.item_friends, null);
		        holder.iv_item_user = (ImageView)convertView.findViewById(R.id.iv_item_friends);
		        holder.tv_item_username = (TextView)convertView.findViewById(R.id.tv_item_friends_user);
		        holder.tv_item_text = (TextView)convertView.findViewById(R.id.tv_item_friends_msg);
		        holder.tv_item_comef=(TextView) convertView.findViewById(R.id.tv_item_friends_comef);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		   
		    final String imgUrl_user =listStatus.get(position).avatar_hd ;
		    
		    
		    holder.iv_item_user.setTag(imgUrl_user);
		   
		    holder.iv_item_user.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		    holder.tv_item_comef.setText(Html.fromHtml(listStatus.get(position).location));

		    if (!TextUtils.isEmpty(imgUrl_user)) {
				Bitmap bitmap = imageLoader.loadImage(holder.iv_item_user, imgUrl_user);
				if (bitmap != null) {
					holder.iv_item_user.setImageBitmap(bitmap);
				}
			}
		    
		  
		   

		            
		            
	        holder.tv_item_username.setText(listStatus.get(position).name);
	       

	        
	        holder.tv_item_text.setText(Html.fromHtml(listStatus.get(position).description));
		    
		                                                                                                
		    return convertView;
	}
		//ViewHolder静态类
		static class ViewHolder
		{
		   public ImageView iv_item_user;
		  public TextView tv_item_comef;
		   public TextView tv_item_username;
		   public TextView tv_item_text;
		  
		}
		public void setItemList(ArrayList<User> list) {
			// TODO Auto-generated method stub
			//=list;
			this.listStatus.clear();
			this.listStatus.addAll(list);
			
			this.notifyDataSetChanged();
		}
		
		
		
		
		
}
