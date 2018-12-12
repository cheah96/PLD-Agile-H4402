package fr.insa.lyon.pld.agile.algorithm;

import fr.insa.lyon.pld.agile.model.Node;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class HAC {
    
    private static class Cluster{
        /**
         * the number of points in the cluster
         */
        int weight = 1;
        /**
         * the centroid of the cluster
         */
        Point2D center =  new Point2D.Double(0, 0);
        /**
         * the list of nodes ID
         */
        List<Integer> nodesID;
        
        /**
         * 
         * @param lon the longitude of the center of the cluster
         * @param lat the latitude of the center of the cluster
         * @param weight the number of points in the cluster
         */
        public Cluster(double lon, double lat, int weight){
            this.weight = weight;
            this.center = new Point2D.Double(lon, lat);
            this.nodesID = new ArrayList<>();
        }
        /**
         * @return the centroid of the cluster
         */
        public Point2D getCenterLocation(){
            return this.center;
        }
        /**
         * @return the number of points in the cluster
         */
        public int getWeight(){
            return this.weight;
        }
        /**
         * merge with an other cluster
         * @param other the cluster to merge with
         */
        public void mergeWithCluster(Cluster other){
            double lon1 = this.center.getX() * this.weight;
            double lat1 = this.center.getY() * this.weight;
            double lon2 = other.center.getX() * other.weight;
            double lat2 = other.center.getY() * other.weight;
            this.center.setLocation( (lon1+lon2)/(this.weight+other.weight),
                                     (lat1+lat2)/(this.weight+other.weight) );
            
            for(int id:other.nodesID){
                this.nodesID.add(id);
            }
            this.weight = this.nodesID.size();
        }
        /**
         * add an ID
         * @param id 
         */
        public void addNodeID(int id){
            this.nodesID.add(id);
            this.weight++;
        }
        /**
         * @return the ID of the nodes
         */
        public List<Integer> getNodesID(){
            return this.nodesID;
        }
        
    }
    
    /** 
     * Node generator used mainely for testing the algorithm
     * choose <nodesNb> points in a rectangle with dimensions (<spaceHeight>, <spaceWidth) centered in (0,0)
     * 
     * @param nodesNb the number of nodes to generate
     * @param spaceHeight the height of the space where the nodes are generated
     * @param spaceWidth the width of the space where the nodes are generated
     * 
     * @return the nodes
     */
    public static List<Node> generateNodes(int nodesNb, int spaceHeight, int spaceWidth) {
        List<Node> nodes = new ArrayList<>();
        for(int i=0; i<nodesNb; ++i) {
            double lon = ( (Math.random()-0.5) * spaceWidth ) ;
            double lat = ( (Math.random()-0.5) * spaceHeight ) ;
            nodes.add(new Node (i, lon, lat));
        }
        
        return nodes;
    }
    
    /**
     * Implementation of the Same Size HAC algorithm
     * 
     * @param nodes the nodes of the deliveries. They are sorted after the call to the function
     * @param wantedClustersNb the number of clusters we wish to have
     * 
     * @return an array with a length equal to the number of nodes. the value of each case is the index of its cluster [0; clustersNb[
     */
    public static int[] HAC(List<Node> nodes, int wantedClustersNb) {
        int crtClustersNb = nodes.size(); 
        int clusterMaxWeight = nodes.size()/wantedClustersNb + 1;
        List<Cluster> clusters = new ArrayList<>();
        
        //! create as much clusters as nodes
        for(int k=0; k<crtClustersNb; ++k){
            double lon = nodes.get(k).getLongitude();
            double lat = nodes.get(k).getLatitude();
            clusters.add(new Cluster(lon, lat, 1));
            clusters.get(k).addNodeID(k);
        }
        
        boolean mergeOccured = true;
        while(crtClustersNb > wantedClustersNb && mergeOccured){
            mergeOccured = false;
            
            int cluster1 = 0;
            while(clusters.get(cluster1).getWeight() >= clusterMaxWeight ){
                ++cluster1;
            }
            int cluster2 = cluster1 + 1;
            while(clusters.get(cluster2).getWeight() >= clusterMaxWeight ){
                ++cluster2;
            }
            double minDist = clusters.get(cluster1).getCenterLocation().distance(clusters.get(cluster2).getCenterLocation());
            
            for(int i=cluster1; i<crtClustersNb; ++i){
                if(clusters.get(i).getWeight() >= clusterMaxWeight ){
                    continue;
                }
                
                for(int j=i+1; j<crtClustersNb; ++j){
                    if(clusters.get(i).getCenterLocation().distance(clusters.get(j).getCenterLocation()) < minDist
                            && (clusters.get(i).getWeight() + clusters.get(j).getWeight()) <= clusterMaxWeight ){
                        minDist = clusters.get(i).getCenterLocation().distance(clusters.get(j).getCenterLocation());
                        cluster1 = i;
                        cluster2 = j;
                    }
                }
            }
            
            if( (clusters.get(cluster1).getWeight() + clusters.get(cluster2).getWeight()) <= clusterMaxWeight ){
                clusters.get(cluster1).mergeWithCluster(clusters.get(cluster2));
                clusters.remove(cluster2);
                --crtClustersNb;
                mergeOccured=true;
            }
        }
        
        
        while(crtClustersNb > wantedClustersNb){
            int indexSmallestCluster = 0;
            for(int k=1; k<clusters.size(); ++k){
                if(clusters.get(k).getWeight() < clusters.get(indexSmallestCluster).getWeight() ){
                    indexSmallestCluster = k;
                }
            }
            
            List<Integer> pointsID = clusters.get(indexSmallestCluster).getNodesID();
            for(int id:pointsID){
                int indexNewCluster = indexSmallestCluster==0?1:0;
                while(clusters.get(indexNewCluster).getWeight() >= clusterMaxWeight 
                        || indexNewCluster==indexSmallestCluster){
                    System.out.println(clusters.get(indexNewCluster).getWeight());
                    ++indexNewCluster;
                }
                System.out.println("ok");
                System.out.println(clusterMaxWeight);
                
                for(int k=indexNewCluster; k<clusters.size(); ++k){
                    if(k==indexSmallestCluster){
                        continue;
                    }
                    double lon = nodes.get(id).getLongitude();
                    double lat = nodes.get(id).getLatitude();
                    if( clusters.get(k).getCenterLocation().distance(lon, lat) < clusters.get(indexNewCluster).getCenterLocation().distance(lon, lat) 
                            && clusters.get(k).getWeight() < clusterMaxWeight ){
                        indexNewCluster = k;
                    }
                }
                clusters.get(indexNewCluster).addNodeID(id);
            }
            
            clusters.remove(indexSmallestCluster);
            crtClustersNb--;
        }
        
        int[] nodesClusters = new int[nodes.size()];
        
        for(int k=0; k<clusters.size(); ++k){
            List<Integer> nodesID = clusters.get(k).getNodesID();
            for(int id:nodesID){
                nodesClusters[id] = k;
            }
        }
        
        return nodesClusters;
    }
    
    /**
     * A small function to test the algorithm. The result is used to display the points in a colored graph.
     */
    private static void test() {
        List<Node> nodes = generateNodes(100, 40, 40);
        int[] clusters = HAC(nodes, 5);
        for(int i=0; i<clusters.length; ++i) {
            System.out.print(clusters[i] + " ; " ); 
            System.out.println(nodes.get(i).getLongitude() + " ; " +
                               nodes.get(i).getLatitude());
        }
    }

    public static void main(String[] args) {
        test();
    }
}
