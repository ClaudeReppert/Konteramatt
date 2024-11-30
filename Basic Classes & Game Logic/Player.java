package lu.Kontermatt;

import java.util.ArrayList;
import java.util.List;

public class Player {
    List<Card> hand = new ArrayList<>(6);
    List<Card> won = new ArrayList<>(24);
    Player next;

    public Player(Player next){ // when creating a Player point it to the next one
        this.next = next;
    }

    public char setTrump(){ // implement Logic to pick Trump
        return '♠';
    }

    public boolean forced(Card first){ // returns true if he is forced to play certain cards
        for (Card card : this.hand){
            if (card.getSuit() == first.getSuit()){
                return true;
            }
        }
        return false;
    }

    public Card playRandom(){ // returns first playable card
        for (Card card : this.hand){
            if (card.playable){
                return card;
            }
        }
        return null; // should never return null
    }

    public int getScore(){
        int score = 0;
        for (Card card : won){
            score += card.points;
        }
        return score;
    }

    // public char whichTrump() {
        //
    // }
}