package com.listener;

import com.activity.CreditActivity;
import com.activity.GameActivity;
import com.cardgame.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;

public class MainListener implements android.view.View.OnClickListener {

	Activity activity = null;
	
	public MainListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.newgameBtn:{
				activity.startActivity(
						new Intent(activity.getBaseContext(), GameActivity.class));
				break;
			}
			case R.id.creditBtn:{
				activity.startActivity(
						new Intent(activity.getBaseContext(), CreditActivity.class));
				break;
			}
			case R.id.quitBtn:{
				new AlertDialog.Builder(activity).setTitle("Quit").setMessage("Are you sure to quit?").setPositiveButton("Yes", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());
						System.exit(0);
					}
				}).setNegativeButton("Cancel", null).show();
				break;
			}
		}
	}
}
