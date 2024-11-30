package lu.Kontermatt;

public class Kontermatt extends Card{
    public Kontermatt(char suit, char rank, int points, int strength) {
        super(suit, rank, points);
        this.strength = strength;
        this.trump = true;
    }
    @Override
    public void updateTrump(char suit) {} // doesn't apply for Kontermatt
    public void reset(){} // doesn't apply for Kontermatt
    public void resetTrump(){} // doesn't apply for Kontermatt
}