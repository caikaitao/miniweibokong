package com.ckt.miniweibokong.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.ckt.miniweibokong.R;


public class AsyncRequest {
	public static void asyncHttpImage(final ImageView iv,final Handler handler, final String profile_image_url) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(profile_image_url==null){
					iv.setImageResource(R.drawable.head_default_yixin);
				}else{
					try {
						URL httpurl = new URL(profile_image_url);
						HttpURLConnection conn = (HttpURLConnection) httpurl
								.openConnection();
						conn.setReadTimeout(5000);
						conn.setRequestMethod("GET");
						InputStream in = conn.getInputStream();
						final Bitmap map = BitmapFactory.decodeStream(in);
						
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								iv.setImageBitmap(map);
								
							}
						});
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}).start();
	}
}
