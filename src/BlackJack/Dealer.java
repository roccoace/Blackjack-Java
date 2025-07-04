package BlackJack;

import java.util.List;

public class Dealer extends Player{


	public Dealer() {
		super(0);
	}
	public List<Card> getHand() {
	    return hand1;
	}

	
	public String showFirstCard() {
	    if (!getHand().isEmpty()) {
	        Card first = getHand().get(0);
	        return first.getRank() + " of " + first.getSuit();
	    } else {
	        return "No cards";
	    }
	}

	
	public boolean shouldHit() {
		int value = getHandValue();
		
		boolean hasAce = false;
		
		for (Card card : getHand()) {
			if (card.getRank().equals("Ace")) {
				hasAce = true;
				break;
			}
		}
		if (value < 17) return true;
		if (value == 17 && hasAce) return true;
		return false;
	}

}
