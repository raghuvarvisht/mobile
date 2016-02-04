package com.cisco.dft.dbObjects;

/**
 * This is the simple Plain object class 
 * for storing the user information in
 * database
 * @author CKapoor
 */
public class ToolConfig
{
	private Integer rowId;
	private String toolName;
	private String config;
    private String versionNo;
    private String date;

	public ToolConfig() {}

	public Integer getRowId()
	{
		return rowId;
	}

	public void setRowId(Integer rowId)
	{
		this.rowId = rowId;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public String getConfig()
	{
		return config;
	}

    public void setConfig(String toolConfig)
    {
        this.config = config;
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
