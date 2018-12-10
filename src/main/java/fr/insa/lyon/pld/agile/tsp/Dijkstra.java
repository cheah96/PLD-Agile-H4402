package fr.insa.lyon.pld.agile.tsp;

import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author challal
 */
public class Dijkstra {
    private static class NodeInfo {
        public Node node;
        public int state;
        public double dist;
        public long parent;
        public int parentSection;

        public NodeInfo(Node node, int state, double dist, long parent, int parentSection) {
            this.node = node;
            this.state = state;
            this.dist = dist;
            this.parent = parent;
            this.parentSection = parentSection;            
        }
        
        public double getDistance(){
            return (this.dist);
        }
        
    }
    
    //! the core of the algorithm
    private static Map<Long, NodeInfo> dijkstra(Map<Long, Node> nodes, Node origin){
        int WHITE = 0;
        int GRAY = 1;
        int BLACK = 2;
        
        Map<Long, NodeInfo> nodeInfos = new HashMap<>();
        
        for(Node node : nodes.values()) {
            nodeInfos.put(node.getId(), new NodeInfo(node, WHITE, 100000000., -1, -1));
        }
        
        
        Comparator<NodeInfo> nodeInfoComparator = Comparator.comparingDouble(NodeInfo::getDistance);
        PriorityQueue<NodeInfo> priorityQueue = new PriorityQueue<>(nodeInfoComparator);
        priorityQueue.add(nodeInfos.get(origin.getId()));
        nodeInfos.get(origin.getId()).dist = 0;
        nodeInfos.get(origin.getId()).state = GRAY;
        
        while(!priorityQueue.isEmpty()){
            NodeInfo head = priorityQueue.remove();
                        
            for(int i=0; i<head.node.getOutgoingSections().size(); ++i){
                Section section = head.node.getOutgoingSections().get(i);
                NodeInfo sectionDest = nodeInfos.get(section.getDestination().getId());
                if(sectionDest.state != BLACK){
                    if(sectionDest.state == WHITE){
                        priorityQueue.add(sectionDest);
                        sectionDest.state = GRAY;
                    }
                    if(sectionDest.dist > head.dist + section.getLength()){
                        sectionDest.dist = head.dist + section.getLength();
                        sectionDest.parent = head.node.getId();
                        sectionDest.parentSection = i;
                    }
                }
            }
            
            head.state = BLACK;
        }
        
        return nodeInfos;
    }
    
    
    public static List<Section> getPath(Map<Long, Node> nodes, Node origin, Node destination){
        Map<Long, NodeInfo> nodeInfos = dijkstra(nodes, origin);
        
        List<Section> path = new ArrayList<>();
        NodeInfo node = nodeInfos.get(destination.getId());
        while(node.node != origin) {
            if (node.parentSection == -1) {
                return null;
            }
            NodeInfo parentNode = nodeInfos.get( node.parent );
            path.add( parentNode.node.getOutgoingSections().get( node.parentSection ) );
            node = parentNode;
        }
        Collections.reverse(path);
        
        return path;
    }
    
    
    public static Map<Long, Double> getDistances(Map<Long, Node> nodes, Node origin){
        Map<Long, NodeInfo> nodeInfos = dijkstra(nodes, origin);
        Map<Long, Double> distances = new HashMap<>();
        
        for(Map.Entry<Long, NodeInfo> pair : nodeInfos.entrySet()){
            distances.put( pair.getKey(), ((NodeInfo)pair.getValue()).dist );
        }
        
        return distances;
    }
    
    public static void test(){
        Map<Long, Node> nodes = new HashMap<>();
        Node n0 = new Node(0,1,1);
        Node n1 = new Node(1,1,1);
        Node n2 = new Node(2,1,1);
        Node n3 = new Node(3,1,1);
        Node n4 = new Node(4,1,1);
        Node n5 = new Node(5,1,1);
        
        n0.addOutgoingSection(new Section(" 0 -> 1 ", 7, n1));
        n0.addOutgoingSection(new Section(" 0 -> 2 ", 14, n2));
        n0.addOutgoingSection(new Section(" 0 -> 3 ", 9, n3));
        
        n1.addOutgoingSection(new Section(" 1 -> 0 ", 7, n0));
        n1.addOutgoingSection(new Section(" 1 -> 3 ", 10, n3));
        n1.addOutgoingSection(new Section(" 1 -> 4 ", 15, n4));
        
        n2.addOutgoingSection(new Section(" 2 -> 0 ", 14, n0));
        n2.addOutgoingSection(new Section(" 2 -> 3 ", 2, n3));
        n2.addOutgoingSection(new Section(" 2 -> 5 ", 9, n5));
        
        n3.addOutgoingSection(new Section(" 3 -> 0 ", 9, n0));
        n3.addOutgoingSection(new Section(" 3 -> 1 ", 10, n1));
        n3.addOutgoingSection(new Section(" 3 -> 2 ", 2, n2));
        n3.addOutgoingSection(new Section(" 3 -> 4 ", 11, n4));
        
        n4.addOutgoingSection(new Section(" 4 -> 1 ", 15, n1));
        n4.addOutgoingSection(new Section(" 4 -> 3 ", 11, n3));
        n4.addOutgoingSection(new Section(" 4 -> 5 ", 6, n5));
        
        n5.addOutgoingSection(new Section(" 5 -> 2 ", 9, n2));
        n5.addOutgoingSection(new Section(" 5 -> 4 ", 6, n4));
        
        
        nodes.put(0L, n0);
        nodes.put(1L, n1);
        nodes.put(2L, n2);
        nodes.put(3L, n3);
        nodes.put(4L, n4);
        nodes.put(5L, n5);
        
        List path = getPath(nodes, n5, n0);
        for(int i=0; i<path.size(); ++i){
            System.out.println(((Section)path.get(i)).getName());
        }
        
        Map<Long, Double> dists = getDistances(nodes, n0);
        for (Map.Entry pair : dists.entrySet()) {
            System.out.println(pair.getValue());
        }
        
    }
    
    public static void main(String[] args){
        test();
    }
    
}
