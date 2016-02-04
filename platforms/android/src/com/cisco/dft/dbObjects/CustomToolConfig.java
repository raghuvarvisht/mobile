package com.cisco.dft.dbObjects;

/**
 * This is the simple Plain object class 
 * for storing the user information in
 * database
 * @author CKapoor
 */
public class CustomToolConfig
{
	private Integer rowId;
	private String userId;
	private String toolName;
	private String customConfig;
	private String versionNo;
	private String date;

	public CustomToolConfig() {}

	public Integer getRowId()
	{
		return rowId;
	}

	public void setRowId(Integer rowId)
	{
		this.rowId = rowId;
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getCustomConfig()
	{
		return customConfig;
	}

	public void setCustomConfig(String customConfig)
	{
		this.customConfig = customConfig;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}