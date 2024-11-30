package lu.Kontermatt;

import java.util.Map;
public class Data {
    public static final Map<Character, Values> ranks = Map.of(
            'A', new Values(15,6,4),
            'K', new Values(11,5,3),
            'Q', new Values(10,4,2),
            'J', new Values(9,3,1),
            'T', new Values(8,2,0),
            'N', new Values(7,1,0)
    );
    public final static char[] suits = {'♠','♥','♦','♣'};
}