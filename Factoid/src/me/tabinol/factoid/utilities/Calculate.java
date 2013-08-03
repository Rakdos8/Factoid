package me.tabinol.factoid.utilities;

public class Calculate {

    public static int greaterInt(int nb1, int nb2) {

        if (nb1 > nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }

    public static int lowerInt(int nb1, int nb2) {

        if (nb1 < nb2) {
            return nb1;
        } else {
            return nb2;
        }
    }
    
    public static boolean isInInterval(int nbSource, int nb1, int nb2) {
        
        return nbSource >= nb1 && nbSource <= nb2;
    }
}
