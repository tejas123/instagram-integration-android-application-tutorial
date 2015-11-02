package com.tag.instagramdemo.example;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.tag.instagramdemo.R;
import com.tag.instagramdemo.lazyload.ImageLoader;

public class MyGridListAdapter extends BaseAdapter {
	// private Context context;
	private ArrayList<String> imageThumbList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public MyGridListAdapter(Context context, ArrayList<String> imageThumbList) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageThumbList = imageThumbList;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return imageThumbList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.media_list_inflater, null);
		Holder holder = new Holder();
		holder.ivPhoto = (ImageView) view.findViewById(R.id.ivImage);
		imageLoader.DisplayImage(imageThumbList.get(position), holder.ivPhoto);
		return view;
	}

	private class Holder {
		private ImageView ivPhoto;
	}

}
