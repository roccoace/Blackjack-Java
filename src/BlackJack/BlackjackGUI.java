package BlackJack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BlackjackGUI extends JFrame {
    private Deck deck;
    private Player player;
    private Dealer dealer;

    private JPanel dealerCardsPanel, playerCardsPanel;
    private JLabel balanceLabel, betLabel, messageLabel;
    private JButton hitButton, standButton, doubleButton, splitButton, newGameButton;

    private boolean playerTurnActive = false;
    private boolean playingFirstHand = true;  // For split hands

    public BlackjackGUI() {
        setTitle("Blackjack");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Dealer cards panel at top
        dealerCardsPanel = new JPanel();
        dealerCardsPanel.setBorder(BorderFactory.createTitledBorder("Dealer's Hand"));
        dealerCardsPanel.setPreferredSize(new Dimension(800, 150));
        add(dealerCardsPanel, BorderLayout.NORTH);

        // Player cards panel in center
        playerCardsPanel = new JPanel();
        playerCardsPanel.setBorder(BorderFactory.createTitledBorder("Player's Hand"));
        playerCardsPanel.setPreferredSize(new Dimension(800, 200));
        add(playerCardsPanel, BorderLayout.CENTER);

        // Right side: Balance, bet, messages
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        balanceLabel = new JLabel("Balance: $100");
        betLabel = new JLabel("Current Bet: $0");
        messageLabel = new JLabel("Welcome to Blackjack!");
        messageLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        infoPanel.add(balanceLabel);
        infoPanel.add(betLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(messageLabel);
        add(infoPanel, BorderLayout.EAST);

        // Bottom: Buttons
        JPanel buttonPanel = new JPanel();
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        doubleButton = new JButton("Double Down");
        splitButton = new JButton("Split");
        newGameButton = new JButton("New Game");

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleButton);
        buttonPanel.add(splitButton);
        buttonPanel.add(newGameButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Disable play buttons initially
        enablePlayButtons(false);
        splitButton.setEnabled(false);

        // Button listeners
        hitButton.addActionListener(e -> playerHit());
        standButton.addActionListener(e -> playerStand());
        doubleButton.addActionListener(e -> playerDoubleDown());
        splitButton.addActionListener(e -> playerSplit());
        newGameButton.addActionListener(e -> startNewGame());

        // Initialize game objects
        player = new Player(100);
        dealer = new Dealer();

        setVisible(true);
    }

    private void startNewGame() {
        deck = new Deck(2);
        player.clearHand();
        dealer.clearHand();
        playerTurnActive = true;
        playingFirstHand = true;

        // Prompt bet
        String betStr = JOptionPane.showInputDialog(this, "Enter your bet:", "Bet", JOptionPane.PLAIN_MESSAGE);
        if (betStr == null) return;
        double bet;
        try {
            bet = Double.parseDouble(betStr);
        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid bet amount!");
            return;
        }

        if (bet <= 0 || bet > player.getBalance()) {
            messageLabel.setText("Bet must be positive and no more than your balance!");
            return;
        }
        player.placeBet(bet);

        // Deal initial cards
        for (int i = 0; i < 2; i++) {
            player.addCard(deck.dealCard());
            dealer.addCard(deck.dealCard());
        }

        updateLabels();
        updateCardPanels();

        splitButton.setEnabled(player.canSplit());
        enablePlayButtons(true);
        messageLabel.setText("Your turn: Hit, Stand, Double Down, or Split.");
    }

    private void playerHit() {
        if (!playerTurnActive) return;

        player.addCard(deck.dealCard());
        updateLabels();
        updateCardPanels();

        if (player.getHandValue() > 21) {
            messageLabel.setText("BUST! Dealer wins this hand.");
            endCurrentHand();
        }
    }

    private void playerStand() {
        if (!playerTurnActive) return;
        endCurrentHand();
    }

    private void playerDoubleDown() {
        if (!playerTurnActive) return;

        if (player.getBalance() < player.getBet()) {
            messageLabel.setText("Not enough balance to double down.");
            return;
        }
        player.doubleBet();
        player.addCard(deck.dealCard());
        updateLabels();
        updateCardPanels();

        if (player.getHandValue() > 21) {
            messageLabel.setText("BUST after double down!");
            endCurrentHand();
        } else {
            endCurrentHand();
        }
    }

    private void playerSplit() {
        if (!playerTurnActive || !player.canSplit()) return;

        player.getHand2().add(player.getHand1().remove(1));
        player.placeBet(player.getBet());
        player.getHand1().add(deck.dealCard());
        player.getHand2().add(deck.dealCard());
        splitButton.setEnabled(false);

        updateLabels();
        updateCardPanels();
        messageLabel.setText("Split! Play Hand 1.");
    }

    private void endCurrentHand() {
        if (player.hasSecondHand() && playingFirstHand) {
            playingFirstHand = false;
            player.switchToSecondHand();
            messageLabel.setText("Play Hand 2.");
            updateLabels();
            updateCardPanels();
        } else {
            playerTurnActive = false;
            enablePlayButtons(false);
            dealerTurn();
        }
    }

    private void dealerTurn() {
        while (dealer.shouldHit()) {
            dealer.addCard(deck.dealCard());
        }
        updateLabels();
        updateCardPanels();
        checkWinners();
    }

    private void checkWinners() {
        StringBuilder results = new StringBuilder();

        player.switchToFirstHand();
        results.append(evaluateHand("Hand 1", player.getHand1()));

        if (player.hasSecondHand()) {
            player.switchToSecondHand();
            results.append(evaluateHand("Hand 2", player.getHand2()));
        }

        messageLabel.setText("<html>" + results.toString().replaceAll("\n", "<br>") + "</html>");
        updateLabels();
    }

    private String evaluateHand(String label, List<Card> hand) {
        int playerTotal = calculateHandValue(hand);
        int dealerTotal = dealer.getHandValue();
        StringBuilder sb = new StringBuilder();

        sb.append(label).append(" Result: ");

        if (playerTotal > 21) {
            sb.append("BUST! Dealer Wins.\n");
        } else if (dealerTotal > 21 || playerTotal > dealerTotal) {
            sb.append("You Win!\n");
            player.adjustBalance(player.getBet() * 2);
        } else if (playerTotal == dealerTotal) {
            sb.append("Push.\n");
            player.adjustBalance(player.getBet());
        } else {
            sb.append("Dealer Wins.\n");
        }
        return sb.toString();
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

    private void updateLabels() {
        balanceLabel.setText("Balance: $" + String.format("%.2f", player.getBalance()));
        betLabel.setText("Current Bet: $" + String.format("%.2f", player.getBet()));
    }

    private void updateCardPanels() {
        // Clear current cards
        dealerCardsPanel.removeAll();
        playerCardsPanel.removeAll();

        // Show dealer cards (only first card visible if player's turn)
        List<Card> dealerCards = dealer.getHand();
        if (playerTurnActive) {
            // Show first card face up
            dealerCardsPanel.add(createCardLabel(dealerCards.get(0)));
            // Show card back for second card (hidden)
            dealerCardsPanel.add(createCardBackLabel());
        } else {
            // Show all dealer cards
            for (Card card : dealerCards) {
                dealerCardsPanel.add(createCardLabel(card));
            }
        }

        // Show player cards
        List<Card> playerCards = player.getActiveHand();
        for (Card card : playerCards) {
            playerCardsPanel.add(createCardLabel(card));
        }

        // Refresh UI
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    private JLabel createCardLabel(Card card) {
        String fileName = "cards/" + card.getRank() + "_of_" + card.getSuit() + ".png";
        ImageIcon icon = new ImageIcon(fileName);
        // Resize image icon for consistent size
        Image img = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        return new JLabel(icon);
    }

    private JLabel createCardBackLabel() {
        ImageIcon icon = new ImageIcon("cards/card_back.png"); // You need a card back image
        Image img = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        return new JLabel(icon);
    }

    private void enablePlayButtons(boolean enable) {
        hitButton.setEnabled(enable);
        standButton.setEnabled(enable);
        doubleButton.setEnabled(enable);
        splitButton.setEnabled(enable && player.canSplit());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlackjackGUI::new);
    }
}

