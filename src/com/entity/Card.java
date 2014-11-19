package com.entity;

public class Card {
	private int color;
	private int number;
	private String cardColor;
	private String cardNum;
	public Card(int color, int number) {
		super();
		this.color = color;
		this.number = number+1;
		switch(this.color){
			case 0:cardColor = "s";break;
			case 1:cardColor = "h";break;
			case 2:cardColor = "c";break;
			case 3:cardColor = "d";break;
		}
		switch(this.number){
			case 1:cardNum = "a";break;
			case 11:cardNum = "j";break;
			case 12:cardNum = "q";break;
			case 13:cardNum = "k";break;
			default:cardNum = ""+this.number;break;
		}
	}
	public void showCard(){
		System.out.println(cardColor+cardNum);
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getCardColor() {
		return cardColor;
	}
	public void setCardColor(String cardColor) {
		this.cardColor = cardColor;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	
}
