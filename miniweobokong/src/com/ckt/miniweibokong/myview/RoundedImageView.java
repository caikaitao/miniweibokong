package com.ckt.miniweibokong.myview;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.ckt.miniweibokong.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView{
	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return; 
		}
		Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
		Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();


		Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
		canvas.drawBitmap(roundBitmap, 0,0, null);

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffa19774;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
				sbmp.getWidth() / 2+0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);


		return output;
	}

	public void asyncHttpImage(final Handler handler, final String profile_image_url) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(profile_image_url==null){
					RoundedImageView.this.setImageResource(R.drawable.head_default_yixin);
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
								RoundedImageView.this.setImageBitmap(map);
								
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
