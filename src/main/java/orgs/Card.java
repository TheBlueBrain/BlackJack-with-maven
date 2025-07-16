package orgs;

import java.util.ArrayList;
import java.util.List;

public class Card{
	Rank rank;
	Suit suit;
	static enum Rank{
		ACE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING
	}
	static enum Suit{
		CLUBS,
		SPADES,
		HEARTS,
		DIAMONDS
	}
	public Card(Rank r, Suit s){
		rank = r;
		suit = s;
	}
	public String toString(){
		return rank.name() + "-" + suit.name();
	}
	public static int calculateScore(List<Card> hand){
		int sum = 0;
		for(Card c : hand){
			switch(c.rank){
				case TWO -> sum +=2;
				case THREE -> sum += 3;
				case FOUR -> sum += 4;
				case FIVE -> sum += 5;
				case SIX -> sum += 6;
				case SEVEN -> sum += 7;
				case EIGHT -> sum += 8;
				case NINE -> sum += 9;
				case TEN, JACK, QUEEN, KING -> sum += 10;
				default -> {}
			}
		}
		int aceCount = 0;
		for(Card c : hand){
			if(c.rank == Rank.ACE){
				aceCount++;
			}
		}
		int aceScore = 11*aceCount;
		while(sum+aceScore>21 && aceScore != aceCount){
			aceScore -= 10;
		}
		return aceScore+sum;
	}
	static List<Card> getStandardDeck(){
		List<Card> deck = new ArrayList<>();
		for(Suit s : Suit.values()){
			for(Rank r : Rank.values()){
				deck.add(new Card(r,s));
			}
		}
		return deck;
	}
}
