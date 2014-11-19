package com.util;

import java.util.ArrayList;

import com.entity.Card;
import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

public class ArthUtil{
	private static class ArthUtilHolder{
		public static final ArthUtil INSTANCE = new ArthUtil();
	}
	public static ArthUtil getInstance(){
		return ArthUtilHolder.INSTANCE;
	}
	private CardUtil cardUtil = CardUtil.getInstance();
	public static final int INPUT_TYPE_NUM = 1;
	public static final int INPUT_TYPE_ARH = 2;
	private int opbrkLeft = 3;
	private int clsbrkLeft = 3;
	private String arth = "";
	private ArrayList<Integer> positions = new ArrayList<Integer>();
	private ArrayList<String> symbols = new ArrayList<String>();
	private ArrayList<Integer> types = new ArrayList<Integer>();
	private Jep myParser = new Jep();
	public boolean put(Integer position, String symbol){
		String input;
		if(checkLegal(position, symbol)){
			if(position != null){
				input = cardUtil.getGenCards().get(position).getNumber()+"";
				positions.add(position);
				types.add(INPUT_TYPE_NUM);
				cardUtil.chooseCard(position);
			} else{
				input = symbol;
				if(symbol.equals("(")){
					if(opbrkLeft == 0)
						return false;
					opbrkLeft--;
				}
				else if(symbol.equals(")")){
					if(clsbrkLeft == 0)
						return false;
					clsbrkLeft--;
				}
				symbols.add(symbol);
				types.add(INPUT_TYPE_ARH);
			}
			arth += input;
			return true;
		}
		else return false;
	}
	public String getArth(){
		return arth;
	}
	public int getOpbrkLeft(){
		return opbrkLeft;
	}
	public int getClsbrkLeft(){
		return clsbrkLeft;
	}
	public ArrayList<Integer> getTypes(){
		return types;
	}
	public ArrayList<String> getSymbols(){
		return symbols;
	}
	public ArrayList<Integer> getPositions(){
		return positions;
	}
	public void remove(){
		String newArth = "";
		if(arth.length() == 0)
			return;
		if(types.get(types.size()-1) == INPUT_TYPE_NUM){
			int lastPos = positions.get(positions.size()-1);
			System.out.println(lastPos);
			cardUtil.releaseCard(lastPos);
			ArrayList<Card> cards = cardUtil.getGenCards();
			Card card = cards.get(lastPos);
			int num = card.getNumber();
			int len = (num+"").length();
			newArth = arth.substring(0, arth.length()-len);
			positions.remove(positions.size()-1);
		} else if(types.get(types.size()-1) == INPUT_TYPE_ARH){
			if(symbols.get(symbols.size()-1).equals("(")){
				opbrkLeft++;
			} else if(symbols.get(symbols.size()-1).equals(")")){
				clsbrkLeft++;
			}
			newArth = arth.substring(0, arth.length()-1);
			symbols.remove(symbols.size()-1);
		}
		types.remove(types.size()-1);
		arth = newArth;
	}
	public Double calculate(){
		Object result = null;
		double d = 0.0;
		try {
			myParser.parse(arth);
			result = myParser.evaluate();
			d = (Double)result;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} catch (EvaluationException e) {
			e.printStackTrace();
			return null;
		}
		return d;
	}
	public void refresh(){
		arth = "";
		opbrkLeft = 3;
		clsbrkLeft = 3;
		positions = new ArrayList<Integer>();
		symbols = new ArrayList<String>();
		types = new ArrayList<Integer>();
		cardUtil.releaseAllCards();
	}
	private boolean checkLegal(Integer position, String symbol){
		//number or "(" - only in 1st or follow symbol except ")"
		if(position != null || symbol.equals("(")){
			if(types.size() == 0)
				return true;
			else if(types.get(types.size()-1).equals(INPUT_TYPE_NUM))
				return false;
			else if(symbols.get(symbols.size()-1).equals(")"))
				return false;
			else return true;
		}
		//")" - only follow number or ")" and cannot be used more than "("
		else if(symbol.equals(")")){
			if(types.size() == 0)
				return false;
			else if(opbrkLeft >= clsbrkLeft)
				return false;
			else if(types.get(types.size()-1).equals(INPUT_TYPE_NUM))
				return true;
			else if(symbols.get(symbols.size()-1).equals(")"))
				return true;
			else return false;
		} 
		//"+" "-" "*" "/" - only follow number or ")"
		else {
			if(types.size() == 0)
				return false;
			else if(types.get(types.size()-1).equals(ArthUtil.INPUT_TYPE_NUM))
				return true;
			else if(symbols.get(symbols.size()-1).equals(")"))
				return true;
			else return false;
		}
	}
}
