package fr.insa.lyon.pld.agile.xml;

public class XMLDuplicateNodeException extends RuntimeException {
    private long nodeId;
    
    public XMLDuplicateNodeException(long id) {
        nodeId = id;
    }
    
    public long getNodeId() {
        return nodeId;
    }
}
