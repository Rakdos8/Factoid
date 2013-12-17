package me.tabinol.factoid.commands;

public class FactoidCommandException extends Exception {
    
    final String langMsg;
    
    public FactoidCommandException(String langMsg) {
        
        super("Error on Factoid command: " + langMsg);
        this.langMsg = langMsg;
    }
    
    public String getLangMsg() {
        
        return langMsg;
    }
    
}
