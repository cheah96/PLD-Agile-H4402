package fr.insa.lyon.pld.agile.xml;

public class XMLAttributeFormatException extends RuntimeException {
    private String attributeName;
    private String attributeValue;
    
    public XMLAttributeFormatException(String name, String value) {
        attributeName = name;
        attributeValue = value;
    }
    
    public String getAttributeName() {
        return attributeName;
    }
    
    public String getAttributeValue() {
        return attributeValue;
    }
}
