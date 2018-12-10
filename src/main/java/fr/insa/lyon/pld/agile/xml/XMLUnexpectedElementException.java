package fr.insa.lyon.pld.agile.xml;

public class XMLUnexpectedElementException extends RuntimeException {
    private String elementName;
    
    public XMLUnexpectedElementException(String name) {
        elementName = name;
    }
    
    public String getElementName() {
        return elementName;
    }
}
