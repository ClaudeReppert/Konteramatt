package lu.Kontermatt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    public List<Card> deck = new ArrayList<>(24);
    public List<Card> table = new ArrayList<>(4);
    public Player player1;
    public Player player2;
    public Player player3;
    public Player player4;
    public char trump;
    int team1 = 0;
    int team2 = 0;
    int draw = 0;

    public void initialize(){
        createDeck();
        createPlayers();
    }

    public void createDeck(){ // creates deck of 24 cards (21 normal + 3 special cards)
        Data.ranks.forEach((rank, value) -> { // normal Cards
            for (char suit : Data.suits) {
                if (rank != 'Q') { // don't create cards when suit == 'Q'
                    deck.add(new Card(suit, rank, value.getPoints()));
                }
            }
        });
        deck.add(new Card('♣','Q',2));
        deck.add(new Kontermatt('♠','Q',2,14)); // special Cards
        deck.add(new Kontermatt('♥','Q',2,13));
        deck.add(new Kontermatt('♦','Q',2,12));
    }

    public void createPlayers(){ // creates the 4 players each pointing to the next one
        player4 = new Player(null);
        player3 = new Player(player4);
        player2 = new Player(player3);
        player1 = new Player(player2);
        player4.next = player1;
    }

    public void shuffle() { // shuffles deck of 24 cards
        Collections.shuffle(deck);
    }

    public void deal(){ // deals 6 cards to each player
        player1.hand.addAll(deck.subList(0,6));
        player2.hand.addAll(deck.subList(6,12));
        player3.hand.addAll(deck.subList(12,18));
        player4.hand.addAll(deck.subList(18,24));
    }

    public void setTrump(char trump){
        this.trump = trump;
    }

    public void updateTrump(){ // updates trump and strength variables of trump cards
        for (Card card : deck){
            card.updateTrump(trump);
        }
    }

    public void move(Card card, List<Card> source, List<Card> target){ // moves card from one ArrayList to another
        if (source.contains(card)){
            source.remove(card);
            target.add(card);
        }
    }

    public Player getTurnWinner(Player turnStarter) {
        int maxstrength = table.get(0).strength;
        int maxindex = 0;
        for (int i = 1; i < 4; i++) {
            if (table.get(i).strength > maxstrength) {
                maxstrength = table.get(i).strength;
                maxindex = i;
            }
        }
        for (int i = 0; i < maxindex; i++) {
            turnStarter = turnStarter.next;
        }
        return turnStarter;
    }

    public void winRound(){
        if (player1.getScore()+player3.getScore() > player2.getScore()+player4.getScore()){
            team1 += 1;
        }
        else if (player1.getScore()+player3.getScore() < player2.getScore()+player4.getScore()){
            team2 += 1;
        }
        else{
            draw += 1;
        }
    }

    public void clearAll(){
        player1.won.clear();
        player2.won.clear();
        player3.won.clear();
        player4.won.clear();
    }

    public void reset(){
        for (Card card : deck){
            card.reset();
        }
    }

    public void resetTrump(){
        for (Card card : deck){
            card.resetTrump();
        }
    }

    public void updatePlayable(Player player) {
        Card first = table.get(0);
        // If the player has cards they are forced to play, process further
        if (!player.forced(first)) {
            return;
        }
        // Determine whether the first card played is a trump
        boolean isFirstTrump = first.trump;
        for (Card card : player.hand) {
            if (!card.trump) {
                // If the first card is a trump, non-trump cards become unplayable
                if (isFirstTrump) {
                    card.playable = false;
                } else {
                    // If the first card is not a trump
                    if (card.suit == first.suit) {
                        // Update strength if the suit matches the first card
                        card.strength = Data.ranks.get(card.rank).getPlayedStrength();
                    } else {
                        // Non-trump cards with different suits become unplayable
                        card.playable = false;
                    }
                }
            }
        }
    }

    public void updatePlayableAll(Player turnStarter){
        for (int j = 0; j < 3; j++){
            turnStarter = turnStarter.next;
            updatePlayable(turnStarter); // updates playable parameters of 2nd 3rd and 4th player
        }
    }

    public void play(){
        Player roundStarter = player1;
        for (int i = 0; i < 1000; i++){
            roundStarter = playRound(roundStarter);
        }
        System.out.println(team1+"/"+draw+"/"+team2);
    }

    public void play2(){
        shuffle();
        deal();
    }

    public Player playRound(Player roundStarter){
        shuffle();
        deal();
        setTrump(roundStarter.setTrump());
        updateTrump();
        Player turnStarter = roundStarter;
        for (int i = 0; i < 6; i++){ // play 6 Turns
            turnStarter = playTurn(turnStarter);
        }
        winRound(); // add 1 point to winning team
        clearAll(); // clears all Players Cards
        resetTrump(); // resets Trump
        return roundStarter.next;
    }

    public Player playTurn(Player turnStarter){
        for (int i = 0; i < 4; i++) {
            move(turnStarter.playRandom(), turnStarter.hand, table); // moves played card from playerhand to table
            if (i == 0){ // updates Playable cards based on the first played card
                updatePlayableAll(turnStarter);
            }
            turnStarter = turnStarter.next;
        }
        Player turnWinner = getTurnWinner(turnStarter); // get Winner of the played Turn
        turnWinner.won.addAll(table.subList(0, 4)); // add Cards to turnWinner.won ArrayList
        table.clear(); // clear table
        reset(); // resets all Cards to their base state
        return turnWinner;
    }
}