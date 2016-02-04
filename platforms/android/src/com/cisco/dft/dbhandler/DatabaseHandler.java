package com.cisco.dft.dbhandler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cisco.dft.dbObjects.CustomToolConfig;
import com.cisco.dft.dbObjects.RegistrarInfo;
import com.cisco.dft.dbObjects.ToolConfig;
import com.cisco.dft.dbObjects.UserInfo;

/**
 * This class is used for creating/handling the database operations
 * @author CKapoor
 *
 */
public class DatabaseHandler extends SQLiteOpenHelper
{	
	//Database Version
	private final static int DATABASE_VERSION = 1;

	//Database Name
	private final static String DATABASE_NAME = "CSGTools";

	private String USER_RECORDS;
	
	//Table name
	private final String USER_INFO = "user_info";

	//Table Columns names
	private final String USER_ID = "user_id";
	
	private String TOOL_RECORDS;
	//Table name
	private final String TOOL_CONFIG = "tool_config";

	//Columns names
	private final String ROW_ID = "row_id";
	private final String TOOL_NAME = "tool_name";
	private final String CONFIG = "config";
    private final String VERSION_NO = "version_no";
	private final String DATE = "date";

	private String CUSTOM_TOOL_RECORDS;
	//Table name
	private final String CUSTOM_TOOL_CONFIG = "custom_tool_config";

	//Columns names
	private final String CUSTOM_ROW_ID = "row_id";
	private final String CUSTOM_TOOL_NAME = "tool_name";
    private final String CUSTOM_VERSION_NO = "version_no";
	private final String CUSTOM_CONFIG = "custom_config";

	private String REGISTRAR_RECORDS;
	//Table name
	private final String REGISTRAR_INFO = "registrar_info";

	//Columns names
	private final String REG_ID = "reg_id";
	private final String SENDER_ID = "sender_id";

	/**
	 * Constructor
	 * @param context
	 */
	public DatabaseHandler(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creating Tables
	 */
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		USER_RECORDS = "CREATE TABLE " + USER_INFO + "("
				+  USER_ID + " TEXT " +")";
		System.out.println(USER_RECORDS);
		db.execSQL(USER_RECORDS);

		TOOL_RECORDS = "CREATE TABLE " + TOOL_CONFIG + "("
				+ ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOOL_NAME + " TEXT, " + CONFIG + " TEXT, " + VERSION_NO + " TEXT, " + DATE + " TEXT" + ")";
		//"PRIMARY KEY (" + ROW_ID + "," + TOOL_NAME + "," + VERSION_NO +
		System.out.println(TOOL_RECORDS);
		db.execSQL(TOOL_RECORDS);

		CUSTOM_TOOL_RECORDS = "CREATE TABLE " + CUSTOM_TOOL_CONFIG + "("
				+ CUSTOM_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_ID + " TEXT, " + CUSTOM_TOOL_NAME + " TEXT, " + CUSTOM_CONFIG + " TEXT, " + CUSTOM_VERSION_NO + " TEXT, " + DATE + " TEXT" +")";
		System.out.println(CUSTOM_TOOL_RECORDS);
		db.execSQL(CUSTOM_TOOL_RECORDS);

		REGISTRAR_RECORDS = "CREATE TABLE " + REGISTRAR_INFO + "("
				+  REG_ID + " TEXT, " + SENDER_ID + " TEXT" +")";
		System.out.println(REGISTRAR_RECORDS);
		db.execSQL(REGISTRAR_RECORDS);
	}

	/**
	 * Upgrading database
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		//Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + USER_INFO);
		db.execSQL("DROP TABLE IF EXISTS " + TOOL_CONFIG);
		db.execSQL("DROP TABLE IF EXISTS " + CUSTOM_TOOL_CONFIG);
		db.execSQL("DROP TABLE IF EXISTS " + REGISTRAR_INFO);
		//Create tables again
		onCreate(db);
	}

	/**
	 * Inserting or adding record
	 * @param userInfo
	 */
	public void addUserInfo(UserInfo userInfo)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_ID, userInfo.getUserId());
		db.insert(USER_INFO, null, values);
		db.close();
	}

	/**
	 * Inserting or adding record
	 * @param toolConfig
	 */
	public void addToolConfig(ToolConfig toolConfig)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(ROW_ID,toolConfig.getRowId());
        values.put(TOOL_NAME,toolConfig.getToolName());
		values.put(CONFIG,toolConfig.getConfig());
        values.put(VERSION_NO,toolConfig.getVersionNo());
        values.put(DATE,toolConfig.getDate());
		db.insert(TOOL_CONFIG, null, values);
		db.close();
	}

	/**
	 * Inserting or adding record
	 * @param customToolConfig
	 */
	public void addCustomToolConfig(CustomToolConfig customToolConfig)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		//values.put(CUSTOM_ROW_ID,customToolConfig.getRowId());
		values.put(USER_ID,customToolConfig.getUserId());
        values.put(CUSTOM_TOOL_NAME,customToolConfig.getToolName());
		values.put(CUSTOM_CONFIG,customToolConfig.getCustomConfig());
        values.put(CUSTOM_VERSION_NO,customToolConfig.getVersionNo());
        values.put(DATE,customToolConfig.getDate());
		db.insert(CUSTOM_TOOL_CONFIG, null, values);
		db.close();
	}

	/**
	 * Inserting or adding record
	 * @param registrarInfo
	 */
	public void addRegistrarInfo(RegistrarInfo registrarInfo)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(REG_ID,registrarInfo.getRegId());
		values.put(SENDER_ID,registrarInfo.getSenderId());
		db.insert(REGISTRAR_INFO, null, values);
		db.close();
	}
	
	/**
	 * Listing user records
	 * @return userRow
	 */
	public List<UserInfo> getUserList()
	{		
		SQLiteDatabase db = this.getWritableDatabase();
		List<UserInfo> userRow = new ArrayList<UserInfo>();
		String selectQuery = "SELECT  * FROM " + USER_INFO;
		Cursor cursor = db.rawQuery(selectQuery, null);	 
		if(cursor.moveToFirst()) 
		{
			do
			{
				UserInfo user = new UserInfo();
				user.setUserId(cursor.getString(0));
				userRow.add(user);
			} 
			while (cursor.moveToNext());
		}
		db.close();
		return userRow;
	}

	/**
	 * Listing Tool Config records
	 * @return toolConfigRow
	 */
	public List<ToolConfig> getToolConfigList()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		List<ToolConfig> toolConfigRow = new ArrayList<ToolConfig>();
		String selectQuery = "SELECT  * FROM " + TOOL_CONFIG;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
			do
			{
				ToolConfig toolConfig = new ToolConfig();
                toolConfig.setRowId(cursor.getInt(cursor.getColumnIndex(ROW_ID)));
                toolConfig.setToolName(cursor.getString(cursor.getColumnIndex(TOOL_NAME)));
                toolConfig.setConfig(cursor.getString(cursor.getColumnIndex(TOOL_CONFIG)));
                toolConfig.setVersionNo(cursor.getString(cursor.getColumnIndex(VERSION_NO)));
                toolConfig.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				/*toolConfig.setRowId(cursor.getInt(0));
                toolConfig.setToolName(cursor.getString(1));
				toolConfig.setConfig(cursor.getString(2));
                toolConfig.setVersionNo(cursor.getString(3));
                toolConfig.setDate(cursor.getString(4));*/
				toolConfigRow.add(toolConfig);
			}
			while (cursor.moveToNext());
		}
		db.close();
		return toolConfigRow;
	}

	/**
	 * Listing Custom Tool Config records
	 * @return customToolConfigRow
	 */
	public List<CustomToolConfig> getCustomToolConfigList()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		List<CustomToolConfig> customToolConfigRow = new ArrayList<CustomToolConfig>();
		String selectQuery = "SELECT  * FROM " + CUSTOM_TOOL_CONFIG;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
			do
			{
				// Check for a way to retrieve values by column name
                CustomToolConfig customToolConfig = new CustomToolConfig();
                customToolConfig.setRowId(cursor.getInt(cursor.getColumnIndex(CUSTOM_ROW_ID)));
                customToolConfig.setUserId(cursor.getString(cursor.getColumnIndex(USER_ID)));
                customToolConfig.setToolName(cursor.getString(cursor.getColumnIndex(CUSTOM_TOOL_NAME)));
                customToolConfig.setCustomConfig(cursor.getString(cursor.getColumnIndex(CUSTOM_CONFIG)));
                customToolConfig.setVersionNo(cursor.getString(cursor.getColumnIndex(CUSTOM_VERSION_NO)));
                customToolConfig.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
				/*customToolConfig.setRowId(cursor.getInt(0));
                customToolConfig.setToolName(cursor.getString(1));
				customToolConfig.setCustomConfig(cursor.getString(2));
                customToolConfig.setVersionNo(cursor.getString(3));
                customToolConfig.setDate(cursor.getString(4));*/
				customToolConfigRow.add(customToolConfig);
			}
			while (cursor.moveToNext());
		}
		db.close();
		return customToolConfigRow;
	}

	/**
	 * Listing Registrar Info records
	 * @return registrarInfoRow
	 */
	public List<RegistrarInfo> getRegistrarInfoList()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		List<RegistrarInfo> registrarInfoRow = new ArrayList<RegistrarInfo>();
		String selectQuery = "SELECT  * FROM " + REGISTRAR_INFO;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.moveToFirst())
		{
			do
			{
				RegistrarInfo registrarInfo = new RegistrarInfo();
				registrarInfo.setRegId(cursor.getString(0));
				registrarInfo.setSenderId(cursor.getString(1));
				registrarInfoRow.add(registrarInfo);
			}
			while (cursor.moveToNext());
		}
		db.close();
		return registrarInfoRow;
	}
}
