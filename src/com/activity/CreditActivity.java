package com.activity;

import com.cardgame.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import android.app.Activity;
import android.os.Bundle;

public class CreditActivity extends Activity{
	private ShimmerTextView version = null;
	private Shimmer shimmer = new Shimmer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
		version = (ShimmerTextView) findViewById(R.id.version);
		shimmer.start(version);
	}
}
