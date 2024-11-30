package lu.Kontermatt;

public class Values {
    int trumpStrength;
    int playedStrength;
    int points;
    public Values(int trumpStrength, int playedStrength, int points){
        this.trumpStrength = trumpStrength;
        this.playedStrength = playedStrength;
        this.points = points;
    }
    public int getTrumpStrength(){
        return trumpStrength;
    }
    public int getPlayedStrength(){
        return playedStrength;
    }
    public int getPoints(){
        return points;
    }
}