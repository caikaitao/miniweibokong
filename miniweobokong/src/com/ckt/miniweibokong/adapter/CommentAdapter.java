package com.ckt.miniweibokong.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.adapter.WeiboAdapter.ViewHolder;
import com.ckt.miniweibokong.thread.AsyncImageLoader;
import com.ckt.miniweibokong.util.DateUtilsDef;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;

public class CommentAdapter extends BaseAdapter{
	private ArrayList<Comment> listComment;
	private Context context;
	private Handler handler;
	  private LayoutInflater mInflater = null;
	  private AsyncImageLoader imageLoader;
	public CommentAdapter(Context context,Handler handler,ArrayList<Comment> listComment){
		this.listComment=listComment;
		this.context=context;
		imageLoader = new AsyncImageLoader(context);
		this.handler=handler;
		 this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listComment.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listComment.get(position);
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
		        convertView = mInflater.inflate(R.layout.item_comment, null);
		        holder.iv_item_msg = (ImageView)convertView.findViewById(R.id.iv_item__comment_msg);
		        holder.iv_item_user = (ImageView)convertView.findViewById(R.id.iv_item_comment_user);
		        holder.tv_item_username = (TextView)convertView.findViewById(R.id.tv_item_comment_username);
		        holder.tv_item_source = (TextView)convertView.findViewById(R.id.tv_item_comment_source);
		        holder.tv_item_time = (TextView)convertView.findViewById(R.id.tv_item__comment_time);
		        holder.tv_item_text1 = (TextView)convertView.findViewById(R.id.tv_item_comment_text1);
		        holder.tv_item_text2 = (TextView)convertView.findViewById(R.id.tv_item_comment_text2);
		        holder.tv_item_writer = (TextView)convertView.findViewById(R.id.tv_item_comment_writer);
		        holder.tv_item_msg = (TextView)convertView.findViewById(R.id.tv_item_comment_writer_msg);
		        convertView.setTag(holder);
		    }else
		    {
		        holder = (ViewHolder)convertView.getTag();
		    }
		   
		    final String imgUrl_user =listComment.get(position).user.profile_image_url ;
		    final String imgUrl_msg = listComment.get(position).status.bmiddle_pic;
		    
		    holder.iv_item_user.setTag(imgUrl_user);
		    holder.iv_item_msg.setTag(imgUrl_msg);
		    holder.iv_item_user.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);
		    holder.iv_item_msg.setImageResource(R.drawable.abs__ab_bottom_solid_dark_holo);

		    if (!TextUtils.isEmpty(imgUrl_user)) {
				Bitmap bitmap = imageLoader.loadImage(holder.iv_item_user, imgUrl_user);
				if (bitmap != null) {
					holder.iv_item_user.setImageBitmap(bitmap);
				}
			}
		    
		    if (!TextUtils.isEmpty(imgUrl_msg)) {
				Bitmap bitmap = imageLoader.loadImage(holder.iv_item_msg, imgUrl_msg);
				if (bitmap != null) {
					holder.iv_item_msg.setImageBitmap(bitmap);
				}
			}
		    String time=DateUtilsDef.getShortTime(listComment.get(position).created_at);

		            
		            
	        holder.tv_item_username.setText(listComment.get(position).user.name);
	        holder.tv_item_source.setText(stringToSource(listComment.get(position).source));
	        holder.tv_item_time.setText(time);
	        
	        holder.tv_item_msg.setText(Html.fromHtml(listComment.get(position).status.text));
	        
	        holder.tv_item_writer.setText(listComment.get(position).status.user.name);
	        holder.tv_item_text1.setText(Html.fromHtml(listComment.get(position).text));
		    
	        if(listComment.get(position).reply_comment!=null){
	        	 holder.tv_item_text2.setText(Html.fromHtml(listComment.get(position).reply_comment.text));    
	        }
	        else{
	        	 holder.tv_item_text2.setVisibility(View.GONE);
	        }
	                                                                                             
		    return convertView;
	}
		//ViewHolder静态类
		static class ViewHolder
		{
		   public ImageView iv_item_user;
		   public ImageView iv_item_msg;
		   public TextView tv_item_text1;
		   public TextView tv_item_text2;
		   public TextView tv_item_username;
		   public TextView tv_item_msg;
		   public TextView tv_item_writer;
		   
		   public TextView tv_item_time;
		   public TextView tv_item_source;
		}
		public void setItemList(ArrayList<Comment> list) {
			// TODO Auto-generated method stub
			//=list;
			this.listComment.clear();
			this.listComment.addAll(list);
			
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
		
		
		
}
