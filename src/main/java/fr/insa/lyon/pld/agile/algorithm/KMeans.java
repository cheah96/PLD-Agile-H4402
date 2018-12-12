package fr.insa.lyon.pld.agile.algorithm;

import fr.insa.lyon.pld.agile.model.*;
import java.awt.geom.Point2D;
import static java.lang.Double.min;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * An implementation of a same size kmeans++
 * The clustering algorithm used in the app
 */
public class KMeans {
    
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
    private static List<Node> generateNodes(int nodesNb, int spaceHeight, int spaceWidth) {
        List<Node> nodes = new ArrayList<>();
        for(int i=0; i<nodesNb; ++i) {
            double lon = ( (Math.random()-0.5) * spaceWidth ) ;
            double lat = ( (Math.random()-0.5) * spaceHeight ) ;
            nodes.add(new Node (i, lon, lat));
        }
        return nodes;
    }
    
    /**
     * init the kmeans algorithm to choose the best clusters centers 
     * among the points before executing the algorithms (refer to kmeans++)
     * 
     * @param nodes the nodes of the deliveries
     * @param clustersNb the number of clusters
     * @param clustersCenters the centers of the clusters
     */
    private static void initKMeans(List<Node> nodes, int clustersNb, Point2D[] clustersCenters){
        //! Choose one center uniformly at random among the data points.
        int randomIndex = (int)(Math.random() * nodes.size());
        double lon = nodes.get(randomIndex).getLongitude(); // coord x
        double lat = nodes.get(randomIndex).getLatitude(); // coord y
        clustersCenters[0] = new Point2D.Double(lon, lat);
        
        //! Choose one new data point at random as a new center, 
        //!    using a weighted probability distribution where a point x is chosen with probability proportional to D(x)^2
        //!    where D(xi) is the distance between the point i and the closest initialized cluster
        double[] distClosestCluster = new double[nodes.size()];
        for(int i=1; i<clustersNb; ++i) {           // for every non initialized cluster
            double totalDist = 0.0;
            for(int j=0; j<nodes.size(); ++j){              // for every point
                lon = nodes.get(j).getLongitude();
                lat = nodes.get(j).getLatitude();
                distClosestCluster[j] = clustersCenters[0].distance(lon, lat);
                for(int k=1; k<i; ++k){                             // for every initialized cluster
                    distClosestCluster[j] = min(distClosestCluster[j], clustersCenters[k].distance(lon, lat));
                }
                distClosestCluster[j] *= distClosestCluster[j];
                totalDist += distClosestCluster[j];
            }
            
            
            double randomDist = ( Math.random() * totalDist);
            randomIndex = -1;
            while(randomDist>=0){
                ++randomIndex;
                randomDist -= distClosestCluster[randomIndex];
            }
            
            lon = nodes.get(randomIndex).getLongitude(); // coord x
            lat = nodes.get(randomIndex).getLatitude(); // coord y
            clustersCenters[i] = new Point2D.Double(lon, lat);
        }
    }
    
    
    //! 
    /**
     * sort the nodes beginning by the farthest from the warehouse
     * if no warehouse is provided, we take the centroid of all nodes
     * 
     * @param nodes the nodes of the deliveries
     * @param warehouse 
     */
    private static void sortNodes(List<Node> nodes, Node warehouse){
        Point2D centroid = new Point2D.Double();
        if(warehouse!=null){
            centroid.setLocation(warehouse.getLongitude(), warehouse.getLatitude());
        }
        else { //! compute centroid
            Double longSum = 0.0;
            Double latSum = 0.0;
            for(Node node: nodes){
                longSum += node.getLongitude();
                latSum += node.getLatitude();
            }
            centroid.setLocation(longSum/nodes.size(), latSum/nodes.size());
        }
        
        //! sort the points beginning by the farthest to the center
        Collections.sort(nodes, new Comparator<Node>(){
            public int compare(Node p1, Node p2){
                return (int)(1000*(centroid.distance(p2.getLongitude(), p2.getLatitude())
                                   - centroid.distance(p1.getLongitude(), p1.getLatitude())));
            }
        });
    }
    
    
    /**
     * Implementation of the Same Size KMeans++ algorithm
     * 
     * @param nodes the nodes of the deliveries. They are sorted after the call to the function
     * @param clustersNb the number of clusters
     * @param warehouse
     * 
     * @return an array with a length equal to the number of nodes. the value of each case is the index of its cluster [0; clustersNb[
     */
    public static int[] kMeans(List<Node> nodes, int clustersNb, Node warehouse) {
        if (nodes.isEmpty()) {
            return new int[0];
        }
        
        Point2D[] clustersCenters = new Point2D[clustersNb];    // the centers of the clusters
        int[] clusters = new int[nodes.size()];   // the clusters to which the points belong
        int[] clustersNodesNumber = new int[clustersNb];    // the number of points in the clusters
        
        clustersNb = Math.min(clustersNb, nodes.size());
        
        initKMeans(nodes, clustersNb, clustersCenters);
        
        // TODO / WARNING : deliveryNodes is modified by kMeans
        sortNodes(nodes, warehouse);
        
        
        //! the final iteration is the iteration where no point changes its cluster
        boolean aChangeOccured=true;
        int iter=0;
        while(aChangeOccured && iter<nodes.size()){
            aChangeOccured=false;
            ++iter;
            
            //! init the number of deliveries in each cluster
            for(int k=0; k<clustersNb; ++k) {
                clustersNodesNumber[k] = 0;
            }
            
            //! extra deliveries are the deliveries at the end of the assignement, 
            //!     which may make a difference of 1 delivery between some deliverymen
            int extraDeliveriesMax = nodes.size()%clustersNb;
            int extraDeliveriesNb = 0;
            
            //! assign the deliveries to the clusters
            for(int i=0; i<nodes.size(); ++i) {
                //! initialization : assign the delivery to the first non filled cluster
                int cluster = 0;
                while( (clustersNodesNumber[cluster] == nodes.size()/clustersNb && extraDeliveriesNb==extraDeliveriesMax) ||
                       (clustersNodesNumber[cluster] == Math.ceil(1.0*nodes.size()/clustersNb)) ) {
                    ++cluster;
                }
                
                //! look for the closest non filled cluster
                for(int k=cluster+1; k<clustersNb; ++k) {
                    double deliveryLon = nodes.get(i).getLongitude();
                    double deliveryLat = nodes.get(i).getLatitude();
                    if(clustersCenters[k].distance(deliveryLon, deliveryLat) < clustersCenters[cluster].distance(deliveryLon, deliveryLat) ) {
                        if( ( clustersNodesNumber[k] < nodes.size()/clustersNb ) ||
                            ( extraDeliveriesNb<extraDeliveriesMax && clustersNodesNumber[k] < Math.ceil(1.0*nodes.size()/clustersNb) ) ){
                            cluster = k;
                        }
                    }
                }
                
                if(clusters[i] != cluster){
                    clusters[i] = cluster;
                    aChangeOccured = true;
                }
                clustersNodesNumber[cluster]++;
                if(clustersNodesNumber[cluster] > nodes.size()/clustersNb){
                    ++extraDeliveriesNb;
                }
            }
            
            //! reevaluate the clusters centers
            for(int k=0; k<clustersNb; ++k) {
                double LonSum = 0; // Coordinate X
                double LatSum = 0; // Coordinate Y
                for(int i=0; i<nodes.size(); ++i) {
                    if(clusters[i] == k) {
                        LonSum += nodes.get(i).getLongitude();
                        LatSum += nodes.get(i).getLatitude();
                    }
                }
                
                if(clustersNodesNumber[k] != 0) {
                    clustersCenters[k].setLocation(LonSum / clustersNodesNumber[k], 
                                                   LatSum / clustersNodesNumber[k]);
                } else {
                    int randomIndex = (int)(Math.random() * nodes.size());
                    double lon = nodes.get(randomIndex).getLongitude();  // coord x
                    double lat = nodes.get(randomIndex).getLatitude();   // coord y
                    clustersCenters[k] = new Point2D.Double(lon, lat);
                }
            }
        }
        
        System.err.println("KMeans++ finished after " + String.valueOf(iter) + " iterations.");
        
        return clusters;
    }
    
    /**
     * A small function to test the algorithm. The result is used to display the points in a colored graph.
     */
    private static void test() {
        List<Node> nodes = generateNodes(1000, 100, 100);
        int[] clusters = kMeans(nodes, 13, null);
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
