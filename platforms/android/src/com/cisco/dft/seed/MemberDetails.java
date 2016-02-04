package com.cisco.dft.seed;

import java.util.HashMap;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cisco.dft.oauth.passcode.PassCodeActivity;
import com.cisco.dft.utils.AppUtils;
import com.ionicframework.csgtools667618.R;

/**
 * Gives the detailed view of the team members from the list view
 *
 * @author tchodey
 *
 */

public class MemberDetails extends PassCodeActivity
{
	private ImageView mImage;
	private TextView mtvTitle, mTvDesg, mtvOrg, mtvWork, mtvEmail;
	private ActionBar ab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memberdetails);

		ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setTitle("Team");
		ab.setDisplayUseLogoEnabled(false);

		mImage = (ImageView) findViewById(R.id.ivImage);
		mtvTitle = (TextView) findViewById(R.id.tvtitlee);
		mTvDesg = (TextView) findViewById(R.id.tvdesgg);
		mtvOrg = (TextView) findViewById(R.id.tvorg);
		mtvWork = (TextView) findViewById(R.id.tvwork);
		mtvEmail = (TextView) findViewById(R.id.tvEmail);
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("map");

		String image = map.get("profileImage");
		Bitmap bm = AppUtils.decodeBase64(image);
		mImage.setImageBitmap(bm);
		mtvTitle.setText(map.get("firstName") + " " + map.get("lastName"));
		mTvDesg.setText(map.get("jobTitle"));
		mtvOrg.setText(map.get("organization"));
		mtvWork.setText(" Work :" + map.get("contactPhone"));
		mtvEmail.setText(" Email :" + map.get("contactEmail"));

	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
}
