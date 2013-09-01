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
    
    public static Double AdditionDouble(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    public static int AdditionInt(int a, int b){
        int t = 0;
        if(a<0){
            t = a-b;
        }else{
            t = a+b;
        }
        return t;
    }
    
    public static Double getDifference(Double a, Double b){
        Double t = null;
        if(a<0){
            t = a-b;
        }else{
            t = a-b;
        }
        return (t < 0 ? -t : t);
    }
}
