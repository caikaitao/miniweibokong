package com.ckt.miniweibokong.model;

import android.graphics.drawable.Drawable;

public class UserInfo {
	public static final String TB_NAME = "users";
	public static final String ID="_id";
	public static final String USERID="userId";
	public static final String FOLLOWERS_COUNT="followers_count";
	public static final String FRIENDS_COUNT="friends_count";
	public static final String STATUSES_COUNT="statuses_count";
	
	public static final String USERNAME="userName";
	public static final String USERICON="userIcon";
	private String id;
	private String userId;//用户id
	private int followers_count;
	private int friends_count;
	private int statuses_count;
	private String userName;
	private Drawable userIcon;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getFollowers_count() {
		return followers_count;
	}
	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}
	public int getFriends_count() {
		return friends_count;
	}
	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}
	public int getStatuses_count() {
		return statuses_count;
	}
	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Drawable getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(Drawable userIcon) {
		this.userIcon = userIcon;
	}
	
	
}
