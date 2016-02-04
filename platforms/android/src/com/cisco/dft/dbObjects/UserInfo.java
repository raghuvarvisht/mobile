package com.cisco.dft.dbObjects;

/**
 * This is the simple Plain object class 
 * for storing the user information in
 * database
 * @author CKapoor
 */
public class UserInfo
{
	private String userId;

	public UserInfo() {
	}

	public String getUserId()
    {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
