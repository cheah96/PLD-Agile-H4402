package fr.insa.lyon.pld.agile.xml;

public class XMLMissingAttributeException extends RuntimeException {
    private String attributeName;
    
    public XMLMissingAttributeException(String attribute) {
        attributeName = attribute;
    }
    
    public String getMissingAttributeName() {
        return attributeName;
    }
}
