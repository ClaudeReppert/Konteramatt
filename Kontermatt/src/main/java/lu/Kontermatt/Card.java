package lu.Kontermatt;

public class Card {
    private String suit;
    private String rank;
    private int points;
    private int strength;
    private String imagePath;

    public Card(String suit, String rank, int points, int strength) {
        this.suit = suit;
        this.rank = rank;
        this.points = points;
        this.strength = strength;
        this.imagePath = "/Kontermatt/images/cards_front/"+rank+suit+".svg";
    }

    // Getters and setters (optional)
    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getPoints() {
        return points;
    }

    public int getStrength() {
        return strength;
    }

    public String getImagePath(){
        return imagePath;
    }
}