package com.cisco.dft.dbObjects;

/**
 * This is the simple Plain object class 
 * for storing the user information in
 * database
 * @author CKapoor
 */
public class RegistrarInfo
{
	private String regId;
	private String senderId;

	public RegistrarInfo() {}

	public String getRegId()
	{
		return regId;
	}

	public void setRegId(String regId)
	{
		this.regId = regId;
	}

	public String getSenderId()
	{
		return senderId;
	}

	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}
}