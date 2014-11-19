package com.util;

import java.util.ArrayList;
import java.util.Random;

import com.entity.Card;

public class CardUtil {
	private static class CardUtilHolder{
		public static final CardUtil INSTANCE = new CardUtil();
	}
	public static CardUtil getInstance(){
		return CardUtilHolder.INSTANCE;
	}
	private ArrayList<Card> genCards = new ArrayList<Card>();
	private boolean[] cardChooses = {false,false,false,false};
	public Card genRandomCard(){
		Random r = new Random();
		int number = r.nextInt(13);
		int color = r.nextInt(4);
		return new Card(color, number);
	}
	public ArrayList<Card> getGenCards(){
		return genCards;
	}
	public boolean[] getCardChooses(){
		return cardChooses;
	}
	public boolean getCardStatus(int position){
		return cardChooses[position];
	}
	public boolean addCard(Card card){
		for (Card oldCard : genCards) {
			if(card.getColor() == oldCard.getColor() && card.getNumber() == oldCard.getNumber())
				return false;
		}
		genCards.add(card);
		return true;
	}
	public Card chooseCard(int position){
		if(!cardChooses[position]){
			cardChooses[position] = true;
			return genCards.get(position);
		}
		return null;
	}
	public void releaseCard(int position){
		cardChooses[position] = false;
	}
	public void releaseAllCards(){
		for(int i=0;i<cardChooses.length;i++)
			cardChooses[i] = false;
	}
	public void refresh(){
		genCards.clear();
		releaseAllCards();
	}
	public void setCards(ArrayList<Card> cards){
		refresh();
		genCards = cards;
	}
}
