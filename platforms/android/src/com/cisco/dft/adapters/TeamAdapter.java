package com.cisco.dft.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ionicframework.csgtools667618.R;
import com.cisco.dft.utils.AppUtils;

/**
 * Sets the data to the adapter for the list view.
 *
 * @author tchodey
 *
 */
public class TeamAdapter extends BaseAdapter {
	ArrayList<HashMap<String, String>> mList;
	LayoutInflater mInflater;
	private Context mCtx;

	public TeamAdapter(LayoutInflater in,
			ArrayList<HashMap<String, String>> list, Context ctx) {
		mInflater = in;
		mList = list;
		mCtx = ctx;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private class ViewHolder {
		protected TextView title, desg;
		protected ImageView thumbnail;

	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View v, ViewGroup parent) {

		ViewHolder holder = null;
		if (v == null) {
			v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.teamrow, null);
			holder = new ViewHolder();
			holder.title = (TextView) v.findViewById(R.id.tvtitle);
			holder.desg = (TextView) v.findViewById(R.id.tvdesg);

			holder.thumbnail = (ImageView) v.findViewById(R.id.videoThumbnail);

			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		// For alternate colors of the rows in the ListView

		if (position % 2 == 0) {

			v.setBackgroundColor(mCtx.getResources().getColor(R.color.white));

		} else {
			v.setBackgroundColor(mCtx.getResources()
					.getColor(R.color.lightblue));

		}

		holder.title.setText(mList.get(position).get("firstName") + " "
				+ mList.get(position).get("lastName"));
		holder.desg.setText(mList.get(position).get("jobTitle"));
		String imageData = mList.get(position).get("profileImage");

		// Decoding the image and setting it to the ThumbNail
		try {

			Bitmap decodedByte = AppUtils.decodeBase64(imageData);
			holder.thumbnail.setImageBitmap(decodedByte);

		} catch (Exception e) {
		}

		return v;
	}
}
