package orgs;

import java.util.Collections;
import java.util.List;

public class Deck{
	List<Card> deck;
	public Deck(){
		deck = Card.getStandardDeck();
		Collections.shuffle(deck);
	}
	Card draw(){
		if(deck.isEmpty()){
			resetDeck();
		}
		Card ret = deck.getFirst();
		deck.removeFirst();
		return ret;
	}
	private void resetDeck(){
		deck = Card.getStandardDeck();
		Collections.shuffle(deck);
	}
}
