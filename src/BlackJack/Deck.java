package BlackJack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	private List<Card> cards;
	private int numDecks;
	
	public Deck(int numDecks) {
		this.numDecks = numDecks;
		this.cards = new ArrayList<>();
		initializeDeck();
		shuffle();
	}

	

	private void initializeDeck() {
		// TODO Auto-generated method stub
		String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
		String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", 
				"Jack", "Queen", "King", "Ace"};
		int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};
		
		for (int d = 0; d < numDecks; d++) {
			for (String suit : suits) {
				for (int i = 0; i <ranks.length; i++) {
					cards.add(new Card(suit, ranks[i], values[i]));
				}
			}
		}
	}
	public void shuffle() {
		Collections.shuffle(cards);
	}
	public Card dealCard() {
		if (!cards.isEmpty()) {
			return cards.remove(0);
		} 
		else {
			return null;
		}
	}
	public int remainingCards() {
		return cards.size();
	}
	public void resetDeck() {
		cards.clear();
		initializeDeck();
		shuffle();
	}
	public boolean needsreshuffle() {
		return cards.size() < 15;
	}
}
	

