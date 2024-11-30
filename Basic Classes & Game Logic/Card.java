package lu.Kontermatt;

import java.util.Map;

public class Card {
    char suit;
    char rank;
    int points;
    int strength = 0;
    boolean trump = false;
    boolean playable = true;
    String imagePath;

    public Card(char suit, char rank, int points) {
        this.suit = suit;
        this.rank = rank;
        this.points = points;
        this.imagePath = "images/cards_front/"+rank+suit+".svg";
    }

    public void updateTrump(char suit) { // sets trumps to true based on given suit, updates strength
        if (suit == this.suit){
            this.trump = true;
            this.strength = Data.ranks.get(this.rank).getTrumpStrength();
        }
    }

    public char getSuit(){ // returns suit if Card is not a trump, returns '*' for all trumps
        if (this.trump){
            return '*';
        }
        else{
            return this.suit;
        }
    }

    public void reset(){ // resets non trumps playable to true and resets strength to 0, trumps remain unaffected
        if (!this.trump){
            this.playable = true;
            this.strength = 0;
        }
    }

    public void resetTrump(){ // resets the trump boolean and strength
        if (this.trump){
            this.trump = false;
            this.strength = 0;
        }
    }

    public String toString(){
        return ""+this.suit+this.rank;
    }
}