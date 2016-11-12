package com.ckt.miniweibokong.activity;

import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.util.Constants;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PostWeiboActivity extends Activity{
	private final String IMAGE_TYPE = "image/*";
	 private final int IMAGE_CODE = 0;   //这里的IMAGE_CODE是自己任意定义的
	private EditText et_postweibo;
	private Button bt_postweibo_tijiao;
	private ImageButton ib_postweibo_jiatu;
	private ImageView iv_postweibo_showpic;
	private StatusesAPI mStatusesAPI;
	private Oauth2AccessToken mAccessToken;
	private Bitmap bitmap=null;
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
		tintManager.setTintColor(Color.parseColor("#FFA500"));//深蓝1f91d3
		 mAccessToken = com.ckt.miniweibokong.util.AccessTokenKeeper.readAccessToken(this);
		mStatusesAPI=new StatusesAPI(this, Constants.APP_KEY, mAccessToken);
		setContentView(R.layout.activity_postweibo);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		et_postweibo=(EditText) findViewById(R.id.et_postweibo);
		ib_postweibo_jiatu=(ImageButton) findViewById(R.id.ib_postweibo_jiatu);
		bt_postweibo_tijiao=(Button) findViewById(R.id.bt_postweibo_tijiao);
		iv_postweibo_showpic=(ImageView) findViewById(R.id.iv_postweibo_showpic);
		ib_postweibo_jiatu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setImage();
			}
		});
		
		bt_postweibo_tijiao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(bitmap==null&&et_postweibo.getText().equals("")){
					Toast.makeText(PostWeiboActivity.this, "内容不能为空", 0).show();
				}else if(bitmap==null){
					if(et_postweibo.getText().length()>140){
						Toast.makeText(PostWeiboActivity.this, "微博字数不能超过140个", 0).show();
					}else{
						mStatusesAPI.update(et_postweibo.getText().toString()+"", null, null, mListener);
					}
				}else if(bitmap!=null){
					mStatusesAPI.upload(et_postweibo.getText().toString()+"", bitmap, null, null, mListener);
				}
			}
		});
	}
	private void setImage() {
        // TODO Auto-generated method stub
         //使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片

         

        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);

        getAlbum.setType(IMAGE_TYPE);

        startActivityForResult(getAlbum, IMAGE_CODE);
        
        
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		  if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量

            Toast.makeText(PostWeiboActivity.this, "无法从相册获得数据", 0).show();

              return;

          }
		  ContentResolver resolver=getContentResolver();
		  if(requestCode==IMAGE_CODE){
			  
			  try {
				Uri uri=data.getData();
				bitmap=MediaStore.Images.Media.getBitmap(resolver, uri);
				iv_postweibo_showpic.setVisibility(View.VISIBLE);
				iv_postweibo_showpic.setImageBitmap(bitmap);
				  
			} catch (Exception e) {
				// TODO: handle exception
			}
		  }

	}
	
	 /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(PostWeiboActivity.this, 
                            "发送一送微博成功, id = " + status.id, 
                            Toast.LENGTH_LONG).show();
                    PostWeiboActivity.this.finish();
                } else {
                    Toast.makeText(PostWeiboActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
           
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(PostWeiboActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
          
}
