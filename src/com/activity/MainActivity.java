package com.activity;

import com.cardgame.R;
import com.listener.MainListener;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
import com.romainpiel.shimmer.ShimmerTextView;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity{

	private ShimmerButton newgameBtn, creditBtn, quitBtn;
	private ShimmerTextView nameTxt;
	private Shimmer shimmer = new Shimmer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		newgameBtn = (ShimmerButton) findViewById(R.id.newgameBtn);
		creditBtn = (ShimmerButton) findViewById(R.id.creditBtn);
		quitBtn = (ShimmerButton) findViewById(R.id.quitBtn);
		
		nameTxt = (ShimmerTextView) findViewById(R.id.nameTxt);
		MainListener listener = new MainListener(this);
		newgameBtn.setOnClickListener(listener);
		creditBtn.setOnClickListener(listener);
		quitBtn.setOnClickListener(listener);
		shimmer.setDuration(1000).start(newgameBtn);
		shimmer.setDuration(1000).start(nameTxt);
	}

}
