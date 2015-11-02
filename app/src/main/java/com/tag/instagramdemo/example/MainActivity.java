package com.tag.instagramdemo.example;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tag.instagramdemo.R;
import com.tag.instagramdemo.example.InstagramApp.OAuthAuthenticationListener;
import com.tag.instagramdemo.lazyload.ImageLoader;

public class MainActivity extends Activity implements OnClickListener {

	private InstagramApp mApp;
	private Button btnConnect, btnViewInfo, btnGetAllImages, btnFollowers,
			btnFollwing;
	private LinearLayout llAfterLoginView;
	private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == InstagramApp.WHAT_FINALIZE) {
				userInfoHashmap = mApp.getUserInfo();
			} else if (msg.what == InstagramApp.WHAT_FINALIZE) {
				Toast.makeText(MainActivity.this, "Check your network.",
						Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
		mApp.setListener(new OAuthAuthenticationListener() {

			@Override
			public void onSuccess() {
				// tvSummary.setText("Connected as " + mApp.getUserName());
				btnConnect.setText("Disconnect");
				llAfterLoginView.setVisibility(View.VISIBLE);
				// userInfoHashmap = mApp.
				mApp.fetchUserName(handler);
			}

			@Override
			public void onFail(String error) {
				Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT)
						.show();
			}
		});
		setWidgetReference();
		bindEventHandlers();

		if (mApp.hasAccessToken()) {
			// tvSummary.setText("Connected as " + mApp.getUserName());
			btnConnect.setText("Disconnect");
			llAfterLoginView.setVisibility(View.VISIBLE);
			mApp.fetchUserName(handler);

		}

	}

	private void bindEventHandlers() {
		btnConnect.setOnClickListener(this);
		btnViewInfo.setOnClickListener(this);
		btnGetAllImages.setOnClickListener(this);
		btnFollwing.setOnClickListener(this);
		btnFollowers.setOnClickListener(this);
	}

	private void setWidgetReference() {
		llAfterLoginView = (LinearLayout) findViewById(R.id.llAfterLoginView);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnViewInfo = (Button) findViewById(R.id.btnViewInfo);
		btnGetAllImages = (Button) findViewById(R.id.btnGetAllImages);
		btnFollowers = (Button) findViewById(R.id.btnFollows);
		btnFollwing = (Button) findViewById(R.id.btnFollowing);
	}

	// OAuthAuthenticationListener listener ;

	@Override
	public void onClick(View v) {
		if (v == btnConnect) {
			connectOrDisconnectUser();
		} else if (v == btnViewInfo) {
			displayInfoDialogView();
		} else if (v == btnGetAllImages) {
			startActivity(new Intent(MainActivity.this, AllMediaFiles.class)
					.putExtra("userInfo", userInfoHashmap));
		} else {
			String url = "";
			if (v == btnFollowers) {
				url = "https://api.instagram.com/v1/users/"
						+ userInfoHashmap.get(InstagramApp.TAG_ID)
						+ "/follows?access_token=" + mApp.getTOken();
			} else if (v == btnFollwing) {
				url = "https://api.instagram.com/v1/users/"
						+ userInfoHashmap.get(InstagramApp.TAG_ID)
						+ "/followed-by?access_token=" + mApp.getTOken();
			}
			startActivity(new Intent(MainActivity.this, Relationship.class)
					.putExtra("userInfo", url));
		}
	}

	private void connectOrDisconnectUser() {
		if (mApp.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setMessage("Disconnect from Instagram?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mApp.resetAccessToken();
									// btnConnect.setVisibility(View.VISIBLE);
									llAfterLoginView.setVisibility(View.GONE);
									btnConnect.setText("Connect");
									// tvSummary.setText("Not connected");
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		} else {
			mApp.authorize();
		}
	}

	private void displayInfoDialogView() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		alertDialog.setTitle("Profile Info");

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.profile_view, null);
		alertDialog.setView(view);
		ImageView ivProfile = (ImageView) view
				.findViewById(R.id.ivProfileImage);
		TextView tvName = (TextView) view.findViewById(R.id.tvUserName);
		TextView tvNoOfFollwers = (TextView) view
				.findViewById(R.id.tvNoOfFollowers);
		TextView tvNoOfFollowing = (TextView) view
				.findViewById(R.id.tvNoOfFollowing);
		new ImageLoader(MainActivity.this).DisplayImage(
				userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE),
				ivProfile);
		tvName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
		tvNoOfFollowing.setText(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
		tvNoOfFollwers.setText(userInfoHashmap
				.get(InstagramApp.TAG_FOLLOWED_BY));
		alertDialog.create().show();
	}
}