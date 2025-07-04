package BlackJack;

import java.util.List;
import java.util.Scanner;

public class Game {
	private Deck deck;
	private Player player;
	private Dealer dealer;
	private Scanner scan;
	
	public Game( ) {
		deck = new Deck(2);
		player = new Player(100);
		dealer = new Dealer();
		scan = new Scanner(System.in);
	}
	
	public void start() {
		System.out.println("Welcome to Blackjack!");
		
		while (player.getBalance() > 0) {
			player.clearHand();
			dealer.clearHand();
			
			System.out.print("\nYour Balance: $" + player.getBalance());
			System.out.print("\nEnter Your Bet: $");
			double bet = scan.nextDouble();
			player.placeBet(bet);
			
			dealInitialCards();
			
			if (player.canSplit()) {
			    System.out.print("You have two " + player.getHand1().get(0).getRank() + "s. Do you want to split? (yes/no): ");
			    String splitChoice = scan.next().toLowerCase();
			    if (splitChoice.equals("yes")) {
			        // Move one card to hand2
			        player.getHand2().add(player.getHand1().remove(1));

			        // Deduct another bet
			        player.placeBet(player.getBet());

			        // Deal one card to each new hand
			        player.getHand1().add(deck.dealCard());
			        player.getHand2().add(deck.dealCard());
			    }
			}

			
			System.out.println("\nYour Hand:");
			displayPlayerHands(player);
			
			System.out.println("\nDealer Shows: " + dealer.showFirstCard());
			
			playerTurn();
			if (player.getHandValue() > 21 ) {
				System.out.println("BUST!");
				continue;
			}
			dealerTurn();
			
			System.out.println("\nFinal Hands: ");
			System.out.println("Your Hand:");
			displayHand(player.getHand1());
			System.out.println("Total: " + calculateHandValue(player.getHand1()));

			if (player.hasSecondHand()) {
			    System.out.println("Second Hand:");
			    displayHand(player.getHand2());
			    System.out.println("Total: " + calculateHandValue(player.getHand2()));
			}

			System.out.println("Dealer Hand:");
			displayHand(dealer.getHand());
			System.out.println("Total: " + calculateHandValue(dealer.getHand()));

			checkWinner();
			System.out.println("\n------------------------------\n");

		}
		System.out.println("You're Out of Money!");
	}

	private void playerTurn() {
	    // Play first hand
	    player.switchToFirstHand();
	    System.out.println("\nPlaying Hand 1:");
	    playSingleHand();

	    // If split, play second hand
	    if (player.hasSecondHand()) {
	        player.switchToSecondHand();
	        System.out.println("\nPlaying Hand 2:");
	        playSingleHand();
	    }
	}

	private void playSingleHand() {
	    while (true) {
	        System.out.println("Hit, Stand, or Double Down? ");
	        String choice = scan.next().toLowerCase();

	        switch (choice) {
	            case "hit":
	                player.addCard(deck.dealCard());
	                System.out.println("Your Hand:");
	                displayHand(player.getActiveHand());
	                System.out.println("Total: " + player.getHandValue());
	                if (player.getHandValue() > 21) return;
	                break;

	            case "stand":
	                return;

	            case "double":
	            case "double down":
	                if (player.getBalance() >= player.getBet()) {
	                    player.doubleBet();
	                    player.addCard(deck.dealCard());
	                    System.out.println("Your Hand (after double down):");
	                    displayHand(player.getActiveHand());
	                    System.out.println("Total: " + player.getHandValue());
	                } else {
	                    System.out.println("Insufficient balance to double down.");
	                }
	                return;

	            default:
	                System.out.println("Invalid input.");
	        }
	    }
	}

	private void checkWinner() {
	    player.switchToFirstHand();
	    evaluateHand("Hand 1", player.getHand1());

	    if (player.hasSecondHand()) {
	        player.switchToSecondHand();
	        evaluateHand("Hand 2", player.getHand2());
	    }
	}

	private void evaluateHand(String label, List<Card> hand) {
	    int playerTotal = calculateHandValue(hand);
	    int dealerTotal = dealer.getHandValue();

	    System.out.println("\n" + label + " Result:");

	    if (playerTotal > 21) {
	        System.out.println("BUST! Dealer Wins.");
	    } else if (dealerTotal > 21 || playerTotal > dealerTotal) {
	        System.out.println("You Win!");
	        // For simplicity, pay 2x the original bet per hand
	        player.adjustBalance(player.getBet() * 2);
	    } else if (playerTotal == dealerTotal) {
	        System.out.println("Push.");
	        player.adjustBalance(player.getBet());
	    } else {
	        System.out.println("Dealer Wins.");
	    }
	}
	
	
	private void dealerTurn() {
		while (dealer.shouldHit()) {
			dealer.addCard(deck.dealCard());
		}
	}

	// Prints the cards in one hand
	private void displayHand(List<Card> hand) {
	    for (Card card : hand) {
	        System.out.println(card.getRank() + " of " + card.getSuit());
	    }
	    System.out.println();
	}

	// Prints all hands of the player, including split hands if any
	private void displayPlayerHands(Player p) {
	    if (p.hasSecondHand()) {
	        p.switchToFirstHand();
	        System.out.println("Hand 1:");
	        displayHand(p.getHand1());
	        System.out.println("Total: " + p.getHandValue());

	        p.switchToSecondHand();
	        System.out.println("Hand 2:");
	        displayHand(p.getHand2());
	        System.out.println("Total: " + p.getHandValue());
	    } else {
	        displayHand(p.getHand1());
	        System.out.println("Total: " + p.getHandValue());
	    }
	}



	private void dealInitialCards() {
		for (int i = 0; i < 2; i++) {
			player.addCard(deck.dealCard());
			dealer.addCard(deck.dealCard());
		}
	}
	private int calculateHandValue(List<Card> hand) {
	    int value = 0;
	    int aces = 0;
	    for (Card c : hand) {
	        value += c.getValue();
	        if (c.getRank().equals("Ace")) aces++;
	    }
	    while (value > 21 && aces > 0) {
	        value -= 10;
	        aces--;
	    }
	    return value;
	}


	public static void main(String[] args) {
		Game blackjack = new Game();
		blackjack.start();
	}

}
