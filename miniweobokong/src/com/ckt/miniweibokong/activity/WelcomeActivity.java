package com.ckt.miniweibokong.activity;


import com.ckt.miniweibokong.R;
import com.ckt.miniweibokong.activity.WBAuthActivity.AuthListener;
import com.ckt.miniweibokong.util.AccessTokenKeeper;
import com.ckt.miniweibokong.util.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity{
	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    private static final String TAG = "weibosdk";

    /** 显示认证后的信息，如 AccessToken */
    private TextView mTokenText;
    
    private AuthInfo mAuthInfo;
    

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private static final int WELCOME_VIEW=1;
    private static final int LOGIN_VIEW=2;
    
	private Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==WELCOME_VIEW){
				setContentView(R.layout.welcome);
				mAccessToken = AccessTokenKeeper.readAccessToken(WelcomeActivity.this);
				  //查找登陆过的账号信息
			        if (!mAccessToken.isSessionValid()) {
			          handlerToken();
			        }else{
			        	Toast.makeText(WelcomeActivity.this, "你已经登陆过了", 0).show();			     
			        	handler.sendEmptyMessageDelayed(LOGIN_VIEW, 2000);
			        }
				
			}else if(msg.what==LOGIN_VIEW){
				Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		}

		private void handlerToken() {
			// TODO Auto-generated method stub
			 // 创建微博实例
	        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
	        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
	        mAuthInfo = new AuthInfo(WelcomeActivity.this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
	        mSsoHandler = new SsoHandler(WelcomeActivity.this, mAuthInfo);
	        mSsoHandler.authorize(new AuthListener());
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		  
		handler.sendEmptyMessage(WELCOME_VIEW);
		
	}
	 /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
    }
    
class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息 
         //  String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
               // updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WelcomeActivity.this, mAccessToken);
                Intent it=new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(it);
                WelcomeActivity.this.finish();
                Toast.makeText(WelcomeActivity.this, 
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
        
            Toast.makeText(WelcomeActivity.this, 
                   R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
            WelcomeActivity.this.finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WelcomeActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
