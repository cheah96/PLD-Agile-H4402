package fr.insa.lyon.pld.agile.xml;

public class XMLMissingAttributeException extends RuntimeException {
    private final String attributeName;
    
    public XMLMissingAttributeException(String attribute) {
        attributeName = attribute;
    }
    
    public String getMissingAttributeName() {
        return attributeName;
    }
}
