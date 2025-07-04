package BlackJack;

import java.util.ArrayList;
import java.util.List;

public class Player {
	protected List<Card> hand1;
	private List<Card> hand2;
	private boolean usingSecondHand;
	private double balance;
	private double bet;
	
	public Player(double startingBalance) {
		this.hand1 = new ArrayList<>();
		this.hand2 = new ArrayList<>();
		this.balance = startingBalance;
		this.usingSecondHand = false;
	}
	public List<Card> getActiveHand() {
	    return usingSecondHand ? hand2 : hand1;
	}

	public List<Card> getHand1() {
	    return hand1;
	}

	public List<Card> getHand2() {
	    return hand2;
	}
	public void clearHand() {
	    hand1.clear();
	    hand2.clear();
	    usingSecondHand = false;
	}

	public void addCard(Card card) {
	    if (usingSecondHand) {
	        hand2.add(card);
	    } else {
	        hand1.add(card);
	    }
	}

	public int getHandValue() {
	    return calculateHandValue(getActiveHand());
	}

	private int calculateHandValue(List<Card> hand) {
	    int value = 0;
	    int aces = 0;
	    for (Card c : hand) {
	        value += c.getValue(); // assume you have this logic already
	        if (c.getRank().equals("Ace")) aces++;
	    }
	    while (value > 21 && aces > 0) {
	        value -= 10;
	        aces--;
	    }
	    return value;
	}

	public boolean canSplit() {
	    return hand1.size() == 2 &&
	           hand2.isEmpty() &&
	           hand1.get(0).getRank().equals(hand1.get(1).getRank());
	}
	public boolean hasSecondHand() {
	    return !hand2.isEmpty();
	}

	public void switchToSecondHand() {
	    usingSecondHand = true;
	}
	public void switchToFirstHand() {
	    usingSecondHand = false;
	}


	
	public void placeBet(double amount) {
		if (amount <= balance) {
			bet = amount;
			balance -= amount;
		}
		else {
			System.out.println("Balance unavailable!");
		}
	}
	
	public void doubleBet() {
		if (balance >= bet) {
			balance -= bet;
			bet *= 2;
		}
		else {
			System.out.println("Insuffiecient balance to double down.");
		}
	}
	
	public void adjustBalance(double amount) {
		balance += amount;
	}
	
	
	public double getBalance() {
		return balance;
	}
	
	public double getBet() {
		return bet;
	}

}
