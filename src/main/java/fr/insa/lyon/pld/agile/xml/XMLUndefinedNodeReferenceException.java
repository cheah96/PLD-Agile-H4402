package fr.insa.lyon.pld.agile.xml;

public class XMLUndefinedNodeReferenceException extends RuntimeException {
    private long nodeId;
    
    public XMLUndefinedNodeReferenceException(long id) {
        nodeId = id;
    }
    
    public long getNodeId() {
        return nodeId;
    }
}
