package com.ckt.miniweibokong.sqlite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.ckt.miniweibokong.model.UserInfo;
import com.sina.weibo.sdk.openapi.models.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;


public class DataHelper {
	
	private volatile static DataHelper dataHelper;
	  //数据库名称
    private static String DB_NAME = "mysinaweibo.db";
    //数据库版本
    private static int DB_VERSION = 2;
    private SQLiteDatabase db;
    private SqliteHelper dbHelper;
    private Context context;
    private DataHelper(Context context){
    	this.context=context;
        dbHelper=new SqliteHelper(context,DB_NAME, null, DB_VERSION);
        db= dbHelper.getWritableDatabase();
    }
    public static DataHelper getInstance(Context context){
    	if(dataHelper==null){
    		synchronized (DataHelper.class) {
				if(dataHelper==null){
					dataHelper=new DataHelper(context);
				}
			}
    	}
    	return dataHelper;
    }
    public void Close()
    {
        db.close();
        dbHelper.close();
    }
    //获取users表中的UserID、Access Token、Access Secret的记录 
    public List<UserInfo> GetUserList(Boolean isSimple)
    {
    	 db= dbHelper.getWritableDatabase();
        List<UserInfo> userList = new ArrayList<UserInfo>();
//        Cursor cursor=db.query(SqliteHelper.TB_NAME, null, null, null, null, null, SqliteHelper.ID+" DESC");
        Cursor cursor=db.rawQuery("select * from "+SqliteHelper.TB_NAME+" order by "+SqliteHelper.ID+" desc",null);
        cursor.moveToFirst(); 
        try {
        while(!cursor.isAfterLast()&& (cursor.getString(1)!=null)){
        	
			
            UserInfo user=new UserInfo();
            user.setId(cursor.getString(0));
            user.setUserId(cursor.getString(1));
            
            user.setFollowers_count(cursor.getInt(2));
            user.setFriends_count(cursor.getInt(3));
            user.setStatuses_count(cursor.getInt(4));
            user.setUserName(cursor.getString(5));
            Log.i("Cursor", cursor.getString(5));
           // user.setUserIcon();
            
           // Log.i("userInfo",cursor.getString(0)+"!!!"+cursor.getString(1)+"!!!"+cursor.getString(2)+"!!!"+cursor.getString(3)+"!!" );
//            if(!isSimple){
//            	if(cursor.getString(4)==null){
//            		 user.setUserName("000");
//            	}else{
//            		 user.setUserName(cursor.getString(4));
//            	}
        
            //Log.i("userIbfo",cursor.getString(4)+ "");
            ByteArrayInputStream stream = null; 
            if(cursor.getBlob(6)==null){
            	Toast.makeText(context, "获取不到数据库用户头像数据，请检查网络", 0).show();
            }else{
            	stream = new ByteArrayInputStream(cursor.getBlob(6)); 
            }
          
            Drawable icon= Drawable.createFromStream(stream, "image");
            user.setUserIcon(icon);
            
            userList.add(user);
            cursor.moveToNext();
        }
    
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, "err", Toast.LENGTH_SHORT).show();
		}finally{
			 cursor.close();
		}
       
        return userList;
    }
    
    //判断users表中的是否包含某个UserID的记录
    public Boolean HaveUserInfo(String UserId)
    {
        Boolean b=false;
        Cursor cursor=db.query(SqliteHelper.TB_NAME, null, UserInfo.USERID + "=" + UserId, null, null, null,null);
        b=cursor.moveToFirst();
       
        Log.e("HaveUserInfo",b.toString());
       
        cursor.close();
        return b;
    }
    
    //更新users表的记录，根据UserId更新用户昵称和用户图标
    public int UpdateUserInfo(String userName,Bitmap userIcon,String UserId)
    {
        ContentValues values = new ContentValues();
        values.put(UserInfo.USERNAME, userName);
        // BLOB类型  
        final ByteArrayOutputStream os = new ByteArrayOutputStream();  
        // 将Bitmap压缩成PNG编码，质量为100%存储          
        userIcon.compress(Bitmap.CompressFormat.PNG, 100, os);   
        // 构造SQLite的Content对象，这里也可以使用raw  
        values.put(UserInfo.USERICON, os.toByteArray());
        int id= db.update(SqliteHelper.TB_NAME, values, UserInfo.USERID + "=" + UserId, null);
        Log.e("UpdateUserInfo2",id+"");
        return id;
    }
    
    //更新users表的记录
    public int UpdateUserInfo(UserInfo user)
    {	
    	Log.i("update","update1" );
        ContentValues values = new ContentValues();
        values.put(UserInfo.USERID, user.getUserId());
        values.put(UserInfo.USERNAME, user.getUserName());
        values.put(UserInfo.FOLLOWERS_COUNT, user.getFollowers_count());
        values.put(UserInfo.FRIENDS_COUNT, user.getFriends_count());
        values.put(UserInfo.STATUSES_COUNT, user.getStatuses_count());
        Drawable drawable= user.getUserIcon();
        //Drawable to bitmap 
        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                                : Bitmap.Config.RGB_565);

			Canvas canvas = new Canvas(bitmap);
			
			//canvas.setBitmap(bitmap);
			
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			
			drawable.draw(canvas);

        // BLOB类型  
        final ByteArrayOutputStream os = new ByteArrayOutputStream();  
        // 将Bitmap压缩成PNG编码，质量为100%存储          
        bitmap .compress(Bitmap.CompressFormat.PNG, 100, os);   
        // 构造SQLite的Content对象，这里也可以使用raw  
        values.put(UserInfo.USERICON, os.toByteArray());
      
       // int id=  db.insert(SqliteHelper.TB_NAME, UserInfo.ID, values);
        Log.i("update","update2" );
        int id= db.update(SqliteHelper.TB_NAME, values, UserInfo.USERID + "=" + user.getUserId(), null);
        Log.e("UpdateUserInfo",id+""); 
        return id;
    }
    
    //添加users表的记录
    public Long SaveUserInfo(UserInfo user)
    {
        ContentValues values = new ContentValues();
        values.put(UserInfo.USERID, user.getUserId());
       
        Long uid = db.insert(SqliteHelper.TB_NAME, UserInfo.ID, values);
        Log.e("SaveUserInfo",uid+"");
        return uid;
    }
    
    //删除users表的记录
    public int DelUserInfo(String UserId){
        int id=  db.delete(SqliteHelper.TB_NAME, UserInfo.USERID +"="+UserId, null);
        Log.e("DelUserInfo",id+"");
        return id;
    }
}
