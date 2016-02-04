package com.cisco.dft.seed;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cisco.dft.adapters.TeamAdapter;
import com.cisco.dft.oauth.activities.LoginActivity;
import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.oauth.utils.AuthConstants;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Displays the details of the team in a customized list view by parsing the
 * JSON array.
 *
 * @author tchodey
 *
 */
public class TeamListActivity extends PassCodeActivity implements OnItemClickListener {

	/** ListView for displaying the Team data on UI **/

	private ListView mListView;
	private Context mCtx;
	private boolean isNetWork = false;
	private ProgressBar mProgress;
	private TeamAdapter mAdapter;

	/** Called when the activity is first created. */

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teamlist);
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mCtx = getApplicationContext();
		new LoadTeamList().execute();
	}

	// This is called when the refresh button on the action bar is clicked.

	public void call() {
		new LoadTeamList().execute();

	}

	/** Aysnc task created to fetch and parse the data **/

	private class LoadTeamList extends AsyncTask<Void, Boolean, Boolean> {
		@Override
		protected void onPreExecute() {
			isNetWork = AppUtils.isNetAvaliable(mCtx);
			super.onPreExecute();
			mProgress.setVisibility(ProgressBar.VISIBLE);

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			// Check if the Internet connection is available to fetch the data
			if (isNetWork) {

				String response = loadData();
				if (response.equalsIgnoreCase(AuthConstants.SHOWLOGIN)) {
					return false;
				} else {

					// Parse the response
					parseData(response);
					return true;
				}
			} else {
				return false;

			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mProgress.setVisibility(ProgressBar.GONE);

			// To set to the adapter after the response from the server comes.
			if (result)
				setAdapter();
			else {
				startlogin();
			}
		}
	}

	/**
	 * Shows the login activity
	 */
	private void startlogin()
	{
		Intent intent = new Intent(TeamListActivity.this, LoginActivity.class);
		intent.putExtra(AuthConstants.KEY_NEXT_ACTIVITY, MainActivity.class);
		intent.putExtra(AuthConstants.KEY_APP_TITLE, getResources().getString(R.string.app_name));
		startActivity(intent);
		finish();
	}

	/**
	 * Gets the response from the server for the given URL
	 *
	 * @return
	 */
	private String loadData() {

		String response = "";

		if (AuthConstants.AUTHENTICATION_TYPE.equalsIgnoreCase(AuthConstants.KEY_TYPE_OAM))
		{
			String url = "https://wsgx.cisco.com/dft/api/teamdata";
			response = AppUtils.getDataBasedOnCookie(url, mCtx);
		}
		else
		{
			response = AppUtils.getData(AppUtils.URL_METADATA, mCtx);
		}
		return response;
	}

	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	/**
	 * Parse the data and Putting it in a Hash-map
	 *
	 * @param response
	 */
	private void parseData(String response) {

		try {
			JSONArray arr = new JSONArray(response);
			if (list != null)
				list.clear();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject ob = arr.getJSONObject(i);
				String userid = ob.getString("userId");
				String firstName = ob.getString("firstName");
				String lastName = ob.getString("lastName");
				String jobTitle = ob.getString("jobTitle");
				String organization = ob.getString("organization");
				String contactPhone = ob.getString("contactPhone");
				String contactEmail = ob.getString("contactEmail");
				String profileImage = ob.getString("profileImage");

				HashMap<String, String> keys = new HashMap<String, String>();

				keys.put("userid", userid);
				keys.put("firstName", firstName);
				keys.put("lastName", lastName);
				keys.put("jobTitle", jobTitle);
				keys.put("organization", organization);
				keys.put("contactPhone", contactPhone);
				keys.put("contactEmail", contactEmail);
				keys.put("profileImage", profileImage);
				list.add(keys);
			}

		} catch (JSONException e) {

			Log.w(e.toString(), " Error ");
		}
	}

	/**
	 * Set to the adapter for the ListView
	 */
	private void setAdapter() {
		mAdapter = new TeamAdapter(getLayoutInflater(), list,
				getApplicationContext());
		mListView.setAdapter(mAdapter);

	}

	/**
	 * Intent to take to the detailed view of the team members.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) mAdapter
				.getItem(position);

		Intent intent = new Intent(TeamListActivity.this, MemberDetails.class);
		intent.putExtra("map", map);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}

}
