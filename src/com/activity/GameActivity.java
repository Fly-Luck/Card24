package com.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cardgame.R;
import com.entity.Card;
import com.util.ArthUtil;
import com.util.CardUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements OnClickListener, OnItemClickListener, OnTouchListener{
	private int wins = 0;
	private int loses = 0;
	private int[] cardImages = new int[4];
	private SimpleAdapter gridAdapter;
	private GridView gridView;
	private ImageButton addBtn,minusBtn,multiplyBtn,divideBtn,opbrktBtn,clsbrkBtn,calcBtn;
	private Button restartBtn,backspaceBtn,clearBtn,testBtn;
	private TextView arthTxt,resultTxt,opbrkTxt,clsbrkTxt,winTxt,loseTxt,totalTxt;
	private ArthUtil arthUtil = ArthUtil.getInstance();
	private CardUtil cardUtil = CardUtil.getInstance();
	private Toast toast;
	private boolean justLost = false;
	private int[][] impossibles;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		bindViews();
		loadImpossibles();
		refreshGrid(false);
	}
	
	@Override
	public void onClick(View v) {
		String symbol = "";
		switch(v.getId()){
			case R.id.addBtn:symbol="+";break;
			case R.id.minusBtn:symbol="-";break;
			case R.id.mtplBtn:symbol="*";break;
			case R.id.divideBtn:symbol="/";break;
			case R.id.opbrktBtn:{
				symbol="(";
				if(arthUtil.getOpbrkLeft() == 0){
					showHintMsg("Sorry, no open bracket left");
					return;
				}
				break;
			}
			case R.id.clsbrktBtn:{
				symbol=")";
				if(arthUtil.getClsbrkLeft() == 0){
					showHintMsg("Sorry, no close bracket left");
					return;
				}
				break;
			}
			case R.id.calcBtn:{
				if(justLost){
					refreshGrid(false);
					justLost = false;
				} else calcResult();
				break;
			}
			case R.id.restartBtn:refreshGrid(false);break;
			case R.id.backspaceBtn:{
				if(arthUtil.getTypes().size() == 0)
					return;
				int lastType = arthUtil.getTypes().get(arthUtil.getTypes().size()-1);
				arthUtil.remove();
				if(lastType == ArthUtil.INPUT_TYPE_NUM){
					updateGridData();
				}
				if(justLost){
					calcBtn.setImageResource(R.drawable.r_equal);
					resultTxt.setText("");
					justLost = false;
				}
				break;
			}
			case R.id.clearBtn:{
				arthUtil.refresh();
				updateGridData();
				if(justLost){
					calcBtn.setImageResource(R.drawable.r_equal);
					resultTxt.setText("");
					justLost = false;
				}
				break;
			}
			case R.id.testBtn:{
				refreshGrid(true);
				justLost = false;
			}
		}
		if(!symbol.equals(""))
			if(!arthUtil.put(null, symbol))
				showHintMsg("Oops, an illegal input");
		updateTxt();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(cardUtil.getCardStatus(position)){
			showHintMsg("Sorry but that card's in use");
			return;
		} else if(!arthUtil.put(position,"")){
			showHintMsg("Sorry but that's incorrect");
			return;
		} else{
			resultTxt.setText("");
			calcBtn.setImageResource(R.drawable.r_equal);
			updateTxt();
			updateGridData();
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			v.getBackground().setAlpha(50);
			break;
		case MotionEvent.ACTION_UP:
			v.getBackground().setAlpha(255);
			break;
		}
		return false;
	}
	public ArthUtil getArthUtil() {
		return arthUtil;
	}
	public CardUtil getCardUtil() {
		return cardUtil;
	}

	private void calcResult() {
		Double result = arthUtil.calculate();
		if(result == null){
			resultTxt.setText("ERR");
			showResult(false);
		}
		else if(result.equals(24.0)){
			resultTxt.setText(24+"");
			boolean[] chooses = cardUtil.getCardChooses();
			for(int i=0;i<chooses.length;i++)
				if(!chooses[i]){
					showResult(false);
				}
			showResult(true);
		}else{
			String resultStr = result+"";
			resultTxt.setText(resultStr.substring(0, resultStr.indexOf(".")));
			showResult(false);
		}
	}
	private void showResult(boolean result){
		if(result){
			calcBtn.setImageResource(R.drawable.r_correct);
			wins++;
			new AlertDialog.Builder(this).setTitle("Win").setMessage("You win! Looks like you are good at this!").
				setPositiveButton("Sure", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						refreshGrid(false);
					}
				}).show();
		} else{
			calcBtn.setImageResource(R.drawable.r_wrong);
			loses++;
			showHintMsg("You lose...hard game right?");	
			updateTxt();
			justLost = true;
		}
	}

	private void bindViews(){
		gridView = (GridView) findViewById(R.id.cardsGrid);
		addBtn = (ImageButton) findViewById(R.id.addBtn);
		minusBtn = (ImageButton) findViewById(R.id.minusBtn);
		multiplyBtn = (ImageButton) findViewById(R.id.mtplBtn);
		divideBtn = (ImageButton) findViewById(R.id.divideBtn);
		opbrktBtn = (ImageButton) findViewById(R.id.opbrktBtn);
		clsbrkBtn = (ImageButton) findViewById(R.id.clsbrktBtn);
		calcBtn = (ImageButton) findViewById(R.id.calcBtn);
		restartBtn = (Button) findViewById(R.id.restartBtn);
		backspaceBtn = (Button) findViewById(R.id.backspaceBtn);
		clearBtn = (Button) findViewById(R.id.clearBtn);
		testBtn = (Button) findViewById(R.id.testBtn);		
		arthTxt = (TextView) findViewById(R.id.arthTxt);
		resultTxt = (TextView) findViewById(R.id.resultTxt);
		opbrkTxt = (TextView) findViewById(R.id.opbrkLeftTxt);
		clsbrkTxt = (TextView) findViewById(R.id.clsbrkLeftTxt);
		winTxt = (TextView) findViewById(R.id.winTxt);
		loseTxt = (TextView) findViewById(R.id.loseTxt);
		totalTxt = (TextView) findViewById(R.id.totalTxt);
		addBtn.setOnClickListener(this);
		minusBtn.setOnClickListener(this);
		multiplyBtn.setOnClickListener(this);
		divideBtn.setOnClickListener(this);
		opbrktBtn.setOnClickListener(this);
		clsbrkBtn.setOnClickListener(this);
		calcBtn.setOnClickListener(this);
		restartBtn.setOnClickListener(this);
		backspaceBtn.setOnClickListener(this);
		clearBtn.setOnClickListener(this);
		testBtn.setOnClickListener(this);
		gridView.setOnItemClickListener(this);
		addBtn.setOnTouchListener(this);
		minusBtn.setOnTouchListener(this);
		multiplyBtn.setOnTouchListener(this);
		divideBtn.setOnTouchListener(this);
		opbrktBtn.setOnTouchListener(this);
		clsbrkBtn.setOnTouchListener(this);
		backspaceBtn.setOnTouchListener(this);
		clearBtn.setOnTouchListener(this);
		restartBtn.setOnTouchListener(this);
		calcBtn.setOnTouchListener(this);
		testBtn.setOnTouchListener(this);
	}
	private void loadImpossibles(){
		String str = "( 1,1,1,1 ) ( 1,1,1,2 ) ( 1,1,1,3 ) ( 1,1,1,4 ) ( 1,1,1,5 ) ( 1,1,1,6 ) ( 1,1,1,7 ) ( 1,1,1,9 ) ( 1,1,1,10 ) ( 1,1,2,2 ) ( 1,1,2,3 ) ( 1,1,2,4 ) ( 1,1,2,5 ) ( 1,1,3,3 ) ( 1,1,4,11 ) ( 1,1,4,13 ) ( 1,1,5,9 ) ( 1,1,5,10 ) ( 1,1,5,11 ) ( 1,1,5,12 ) ( 1,1,5,13 ) ( 1,1,6,7 ) ( 1,1,6,10 ) ( 1,1,6,11 ) ( 1,1,6,13 ) ( 1,1,7,7 ) ( 1,1,7,8 ) ( 1,1,7,9 ) ( 1,1,7,11 ) ( 1,1,7,12 ) ( 1,1,7,13 ) ( 1,1,8,9 ) ( 1,1,8,10 ) ( 1,1,8,11 ) ( 1,1,8,12 ) ( 1,1,8,13 ) ( 1,1,9,9 ) ( 1,1,9,10 ) ( 1,1,9,11 ) ( 1,1,9,12 ) ( 1,1,10,10 ) ( 1,1,10,11 ) ( 1,2,2,2 ) ( 1,2,2,3 ) ( 1,2,5,11 ) ( 1,2,7,13 ) ( 1,2,8,11 ) ( 1,2,8,12 ) ( 1,2,9,9 ) ( 1,2,9,10 ) ( 1,2,10,10 ) ( 1,3,3,13 ) ( 1,3,5,5 ) ( 1,3,7,11 ) ( 1,3,10,13 ) ( 1,3,11,13 ) ( 1,4,4,13 ) ( 1,4,7,10 ) ( 1,4,8,10 ) ( 1,4,9,9 ) ( 1,4,10,13 ) ( 1,4,11,11 ) ( 1,4,11,12 ) ( 1,4,11,13 ) ( 1,4,12,13 ) ( 1,4,13,13 ) ( 1,5,5,7 ) ( 1,5,5,8 ) ( 1,5,7,7 ) ( 1,5,11,13 ) ( 1,5,12,13 ) ( 1,5,13,13 ) ( 1,6,6,7 ) ( 1,6,7,7 ) ( 1,6,7,8 ) ( 1,6,7,13 ) ( 1,6,9,11 ) ( 1,6,10,10 ) ( 1,6,10,11 ) ( 1,6,11,11) ( 1,6,13,13 ) ( 1,7,7,7 ) ( 1,7,7,8 ) ( 1,7,7,13 ) ( 1,7,8,13 ) ( 1,7,10,10 ) ( 1,7,10,11 ) ( 1,7,11,11 ) ( 1,7,11,12 ) ( 1,7,11,13 ) ( 1,8,8,13 ) ( 1,8,9,9 ) ( 1,8,9,10 ) ( 1,8,10,10 ) ( 1,8,11,11 ) ( 1,8,12,13 ) ( 1,8,13,13 ) ( 1,9,9,9 ) ( 1,9,9,10 ) ( 1,9,9,11 ) ( 1,9,9,13 ) ( 1,9,10,10 ) ( 1,9,10,11 ) ( 1,9,12,13 ) ( 1,9,13,13 ) ( 1,10,10,10 ) ( 1,10,10,11 ) ( 1,10,10,13 ) ( 1,10,11,11 ) ( 1,10,11,13 ) ( 1,10,13,13 ) ( 1,11,11,11 ) ( 1,13,13,13 )" +
				"( 2,2,2,2 ) ( 2,2,2,6 ) ( 2,2,5,13 ) ( 2,2,7,9 ) ( 2,2,7,11 ) ( 2,2,8,11 ) ( 2,2,8,13 ) ( 2,2,9,9 ) ( 2,2,9,13 ) ( 2,2,10,12 ) ( 2,3,3,4 ) ( 2,3,9,11 ) ( 2,3,10,11 ) ( 2,4,7,13 ) ( 2,4,9,11 ) ( 2,4,11,13 ) ( 2,4,12,13 ) ( 2,5,5,5 ) ( 2,5,5,6 ) ( 2,5,7,12 ) ( 2,5,9,9 ) ( 2,5,9,13 ) ( 2,5,11,11 ) ( 2,5,11,13 ) ( 2,5,13,13 ) ( 2,6,7,7 ) ( 2,6,9,13 ) ( 2,6,11,11 ) ( 2,6,13,13 ) ( 2,7,7,7 ) ( 2,7,7,9 ) ( 2,7,8,10 ) ( 2,7,9,9 ) ( 2,7,9,12 ) ( 2,7,10,13 ) ( 2,7,11,11 ) ( 2,7,11,13 ) ( 2,7,13,13 ) ( 2,8,11,13 ) ( 2,9,9,9 ) ( 2,9,9,10 ) ( 2,9,11,12 ) ( 2,9,12,12 ) ( 2,10,10,10 ) ( 2,10,12,12 ) ( 2,10,13,13 )" +
				" ( 3,3,3,13 ) ( 3,3,4,10 ) ( 3,3,5,8 ) ( 3,3,5,11 ) ( 3,3,7,10 ) ( 3,3,8,11 ) ( 3,3,10,10 ) ( 3,3,10,11 ) ( 3,3,10,12 ) ( 3,3,11,11 ) ( 3,3,13,13 ) ( 3,4,6,7 ) ( 3,4,7,13 ) ( 3,4,8,8 ) ( 3,4,9,10 ) ( 3,4,10,11 ) ( 3,4,11,11 ) ( 3,4,13,13 ) ( 3,5,5,5 ) ( 3,5,5,10 ) ( 3,5,5,13 ) ( 3,5,7,7 ) ( 3,5,8,10 ) ( 3,5,9,11 ) ( 3,5,11,13 ) ( 3,6,7,11 ) ( 3,6,8,11 ) ( 3,6,10,13 ) ( 3,7,7,11 ) ( 3,7,8,10 ) ( 3,7,10,12 ) ( 3,7,11,13 ) ( 3,8,8,13 ) ( 3,8,10,13 ) ( 3,8,11,13 ) ( 3,10,10,10 ) ( 3,10,10,11 ) ( 3,10,10,13 ) ( 3,10,11,11 ) ( 3,10,12,12 ) ( 3,10,12,13 ) ( 3,10,13,13 ) ( 3,11,11,11 ) ( 3,11,11,13 ) ( 3,11,12,13 ) ( 3,11,13,13 ) ( 3,13,13,13 )" +
				" ( 4,4,4,13 ) ( 4,4,5,9 ) ( 4,4,6,6 ) ( 4,4,6,7 ) ( 4,4,7,11 ) ( 4,4,9,9 ) ( 4,4,9,10 ) ( 4,4,9,13 ) ( 4,4,10,11 ) ( 4,4,11,11 ) ( 4,4,13,13 ) ( 4,5,5,11 ) ( 4,5,5,12 ) ( 4,5,5,13 ) ( 4,5,9,11 ) ( 4,6,6,11 ) ( 4,6,6,13 ) ( 4,6,7,11 ) ( 4,6,7,13 ) ( 4,6,8,11 ) ( 4,6,9,11 ) ( 4,6,10,13 ) ( 4,6,11,13 ) ( 4,7,7,9 ) ( 4,7,7,10 ) ( 4,7,7,12 ) ( 4,7,7,13 ) ( 4,7,10,13 ) ( 4,8,10,13 ) ( 4,9,9,9 ) ( 4,9,9,11 ) ( 4,9,9,13 ) ( 4,9,10,10 ) ( 4,9,11,13 ) ( 4,9,12,13 ) ( 4,9,13,13 ) ( 4,10,10,10 ) ( 4,10,10,13 ) ( 4,10,11,11 ) ( 4,10,13,13 ) ( 4,11,11,11 ) ( 4,11,11,12 ) ( 4,11,11,13 ) ( 4,11,12,12 ) ( 4,11,13,13 ) ( 4,12,12,13 ) ( 4,12,13,13 ) ( 4,13,13,13 )" +
				" ( 5,5,5,7 ) ( 5,5,5,8 ) ( 5,5,5,10 ) ( 5,5,5,11 ) ( 5,5,5,13 ) ( 5,5,6,9 ) ( 5,5,6,10 ) ( 5,5,6,12 ) ( 5,5,6,13 ) ( 5,5,7,9 ) ( 5,5,7,12 ) ( 5,5,7,13 ) ( 5,5,9,12 ) ( 5,5,9,13 ) ( 5,5,10,12 ) ( 5,6,6,11 ) ( 5,6,6,13 ) ( 5,6,7,10 ) ( 5,6,7,11 ) ( 5,6,8,11 ) ( 5,7,7,7 ) ( 5,7,7,8 ) ( 5,7,7,12 ) ( 5,7,7,13 ) ( 5,7,8,11 ) ( 5,7,8,12 ) ( 5,7,8,13 ) ( 5,7,9,9 ) ( 5,7,11,12 ) ( 5,7,12,13 ) ( 5,8,8,11 ) ( 5,8,8,12 ) ( 5,8,9,9 ) ( 5,8,9,10 ) ( 5,8,10,10 ) ( 5,8,10,13 ) ( 5,8,11,11 ) ( 5,8,12,13 ) ( 5,8,13,13 ) ( 5,9,9,9 ) ( 5,9,9,10 ) ( 5,9,9,13 ) ( 5,9,10,12 ) ( 5,9,11,11 ) ( 5,9,11,12 ) ( 5,9,13,13 ) ( 5,10,10,10 ) ( 5,10,11,12 ) ( 5,10,11,13 ) ( 5,10,12,12 ) ( 5,11,11,11 ) ( 5,11,11,12 ) ( 5,11,11,13 ) ( 5,11,12,13 ) ( 5,11,13,13 ) ( 5,12,12,12 ) ( 5,12,12,13 ) ( 5,12,13,13 ) ( 5,13,13,13 )" +
				" ( 6,6,6,7 ) ( 6,6,6,13 ) ( 6,6,7,7 ) ( 6,6,7,8 ) ( 6,6,7,13 ) ( 6,6,9,9 ) ( 6,6,10,10 ) ( 6,6,10,11 ) ( 6,6,11,11 ) ( 6,6,13,13 ) ( 6,7,7,7 ) ( 6,7,7,8 ) ( 6,7,7,9 ) ( 6,7,7,12 ) ( 6,7,7,13 ) ( 6,7,8,8 ) ( 6,7,8,13 ) ( 6,7,9,10 ) ( 6,7,9,11 ) ( 6,7,9,13 ) ( 6,7,10,11 ) ( 6,7,13,13 ) ( 6,8,8,13 ) ( 6,8,10,10 ) ( 6,8,12,13 ) ( 6,9,9,9 ) ( 6,9,9,13 ) ( 6,9,10,10 ) ( 6,9,10,13 ) ( 6,9,11,11 ) ( 6,9,13,13 ) ( 6,10,10,11 ) ( 6,10,10,12 ) ( 6,10,11,11 ) ( 6,10,11,13 ) ( 6,10,13,13 ) ( 6,11,11,11 ) ( 6,11,11,13 ) ( 6,11,13,13 ) ( 6,13,13,13 )" +
				" ( 7,7,7,7 ) ( 7,7,7,8 ) ( 7,7,7,9 ) ( 7,7,7,10 ) ( 7,7,7,11 ) ( 7,7,7,13 ) ( 7,7,8,8 ) ( 7,7,8,9 ) ( 7,7,8,10 ) ( 7,7,8,12 ) ( 7,7,8,13 ) ( 7,7,9,9 ) ( 7,7,9,11 ) ( 7,7,9,12 ) ( 7,7,9,13 ) ( 7,7,10,10 ) ( 7,7,10,11 ) ( 7,7,10,12 ) ( 7,7,11,11 ) ( 7,7,13,13 ) ( 7,8,8,8 ) ( 7,8,9,9 ) ( 7,8,9,11 ) ( 7,8,10,12 ) ( 7,8,11,11 ) ( 7,8,13,13 ) ( 7,9,9,9 ) ( 7,9,9,10 ) ( 7,9,9,11 ) ( 7,9,9,12 ) ( 7,9,10,10 ) ( 7,9,10,13 ) ( 7,9,11,13 ) ( 7,9,12,13 ) ( 7,10,10,10 ) ( 7,10,10,13 ) ( 7,10,11,11 ) ( 7,10,11,12 ) ( 7,10,13,13 ) ( 7,11,11,11 ) ( 7,11,11,12 ) ( 7,11,11,13 ) ( 7,11,12,12 ) ( 7,11,12,13 ) ( 7,11,13,13 ) ( 7,12,12,12 ) ( 7,12,13,13 ) ( 7,13,13,13 )" +
				" (8,8,8,8 ) ( 8,8,8,9 ) ( 8,8,9,9 ) ( 8,8,9,10 ) ( 8,8,10,10 ) ( 8,8,10,11 ) ( 8,8,11,11 ) ( 8,8,13,13 ) ( 8,9,9,9 ) ( 8,9,9,10 ) ( 8,9,9,11 ) ( 8,9,9,13 ) ( 8,9,10,10 ) ( 8,9,10,11 ) ( 8,9,13,13 ) ( 8,10,10,10 ) ( 8,10,10,11 ) ( 8,10,10,13 ) ( 8,10,11,12 ) ( 8,10,11,13 ) ( 8,11,11,11 ) ( 8,11,11,12 ) ( 8,11,11,13 ) ( 8,11,12,13 ) ( 8,11,13,13 ) ( 8,12,12,12 ) ( 8,12,12,13 ) ( 8,12,13,13 ) ( 8,13,13,13 )" +
				" ( 9,9,9,9 ) ( 9,9,9,10 ) ( 9,9,9,11 ) ( 9,9,9,13 ) ( 9,9,10,10 ) ( 9,9,10,11 ) ( 9,9,10,12 ) ( 9,9,11,11 ) ( 9,9,13,13 ) ( 9,10,10,10 ) ( 9,10,10,11 ) ( 9,10,10,12 ) ( 9,10,11,11 ) ( 9,10,13,13 ) ( 9,11,11,12 ) ( 9,11,11,13 ) ( 9,12,12,13 ) ( 9,12,13,13 ) ( 9,13,13,13 )" +
				" ( 10,10,10,10 ) ( 10,10,10,11 ) ( 10,10,11,11 ) ( 10,10,13,13 ) ( 10,11,11,11 ) ( 10,11,13,13 )" +
				" ( 11,11,11,11 ) ( 11,11,13,13 ) ( 13,13,13,13 ) ";
		int count = 0;
		for(int i=0;i<str.length();i++){
			if(str.charAt(i) == '('){
				count++;
			}
		}
		impossibles = new int[count][4];
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile("( \\w{1,2},\\w{1,2},\\w{1,2},\\w{1,2} )");
		matcher = pattern.matcher(str);
		int i=0;
		while(matcher.find()){
			String numStr = matcher.group();
			int[] commas = new int[3];  
			int c = 0;
			for(int j=0;j<numStr.length();j++){
				if(numStr.charAt(j) == ',')
					commas[c++] = j;
			}
			int index = numStr.indexOf(" ",0);
			impossibles[i][0] = Integer.parseInt(numStr.substring(index+1,commas[0]));
			impossibles[i][1] = Integer.parseInt(numStr.substring(commas[0]+1,commas[1]));
			impossibles[i][2] = Integer.parseInt(numStr.substring(commas[1]+1,commas[2]));
			index = numStr.indexOf(" ",commas[2]);
			impossibles[i++][3] = Integer.parseInt(numStr.substring(commas[2]+1,index));
		}
	}
	private void genCards(boolean test){
		Card genCard;
		try{
			if(test){
				genCard = new Card(2, 3);
				cardUtil.addCard(genCard);
				cardImages[0] = 
						R.drawable.class.getField("bordered_"+genCard.getCardColor()+"_"+genCard.getCardNum()).getInt(new R.drawable());			
				genCard = new Card(1, 4);
				cardUtil.addCard(genCard);
				cardImages[1] = 
						R.drawable.class.getField("bordered_"+genCard.getCardColor()+"_"+genCard.getCardNum()).getInt(new R.drawable());			
				genCard = new Card(3, 8);
				cardUtil.addCard(genCard);
				cardImages[2] = 
						R.drawable.class.getField("bordered_"+genCard.getCardColor()+"_"+genCard.getCardNum()).getInt(new R.drawable());			
				genCard = new Card(0, 9);
				cardUtil.addCard(genCard);
				cardImages[3] = 
						R.drawable.class.getField("bordered_"+genCard.getCardColor()+"_"+genCard.getCardNum()).getInt(new R.drawable());			
			} else{
				boolean isImpossible = true;
				//clear cards
				cardUtil.refresh();
				//generate cards until it is solvable
				while(isImpossible){
					isImpossible = false;
					//generate cards
					for(int i=0;i<4;i++){
						do{
							genCard = cardUtil.genRandomCard();
						} while(!cardUtil.addCard(genCard));
						Field field=R.drawable.class.getField("bordered_"+genCard.getCardColor()+"_"+genCard.getCardNum());
						cardImages[i] = field.getInt(new R.drawable());
					}
					//cloned sorted cards
					@SuppressWarnings("unchecked")
					ArrayList<Card> testCards =(ArrayList<Card>) cardUtil.getGenCards().clone();
					ArrayList<Integer> cardNums = new ArrayList<Integer>();
					for(int i=0;i<4;i++){
						cardNums.add(testCards.get(i).getNumber());
					}
					Collections.sort(cardNums);
					//validate
					for(int i=0;i<impossibles.length;i++){
						int[] impossible = impossibles[i];
						if(cardNums.get(0).equals(impossible[0]) && cardNums.get(1).equals(impossibles[1]) && cardNums.get(2).equals(impossibles[2]) && cardNums.get(3).equals(impossibles[3])){
							isImpossible = true;
						}
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	private void refreshGrid(boolean test){
		arthUtil.refresh();
		cardUtil.refresh();
		updateTxt();
		genCards(test);
		updateGridData();
		calcBtn.setImageResource(R.drawable.r_equal);
		resultTxt.setText("");
	}
	private void updateGridData(){
		ArrayList<HashMap<String, Object>> cardList = new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<cardImages.length;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			if(cardUtil.getCardChooses()[i]){
				map.put("cardStatus", "Selected");
			} else{
				map.put("cardStatus", "");
			}
			map.put("cardImages", cardImages[i]);
			cardList.add(map);
		}
		gridAdapter = new SimpleAdapter(this, cardList, R.layout.card, new String[]{"cardImages","cardStatus"},new int[]{R.id.cardImg,R.id.cardStatus});
		gridView.setAdapter(gridAdapter);
	}
	private void showHintMsg(String text){
		if (toast != null){
			toast.setText(text);
			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		} else{
		toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
		}
	}
	private void updateTxt(){
		arthTxt.setText(arthUtil.getArth());
		opbrkTxt.setText("Open brackets:"+arthUtil.getOpbrkLeft());
		clsbrkTxt.setText("Close brackets:"+arthUtil.getClsbrkLeft());
		winTxt.setText("Win: "+wins);
		loseTxt.setText("Lose: "+loses);
		totalTxt.setText("Total: "+(wins+loses));
	}
}


