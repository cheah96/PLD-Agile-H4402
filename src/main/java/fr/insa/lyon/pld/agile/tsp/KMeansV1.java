package fr.insa.lyon.pld.agile.tsp;


import fr.insa.lyon.pld.agile.model.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// ideas : begin assigning randomly
//          compute all the distances, and begin by the closest points
// https://elki-project.github.io/tutorial/same-size_k_means


/**
 *
 * @author challal
 */
// todo: solve the problem of 4,4,2
public class KMeansV1 {
    
    public static List<Node> generateNodes(int nodesNb, int spaceHeight, int spaceWidth) {
        List<Node> nodes = new ArrayList();
        for(int i=0; i<nodesNb; ++i) {
            double lon = ( (Math.random()-0.5) * spaceWidth ) ;
            double lat = ( (Math.random()-0.5) * spaceHeight ) ;
            nodes.add(new Node (i, lon, lat));
        }
        
        return nodes;
    }
        
    // todo : corriger le problème 10pts, 3clusters
    // todo : initialiser correctement la 1ère assignation de cluster
    public static List<Integer> kMeans(List<Node> nodes, int clustersNb) {
        Point2D[] clustersCenters = new Point2D[clustersNb];    // the centers of the clusters
        int[] clusters = new int[nodes.size()];   // the clusters to which the points belong
        int[] clustersDeliveriesNumber = new int[clustersNb];    // the number of points in the clusters
        
        for(int i=0; i<clustersNb; ++i) {
            int randomIndex = (int)(Math.random() * nodes.size());
            double lon = nodes.get(randomIndex).getLongitude(); // coord x
            double lat = nodes.get(randomIndex).getLatitude(); // coord y
            clustersCenters[i] = new Point2D.Double(lon, lat);
        }
        
        // todo: replace the for by a while loop
        for(int t=0; t<5000; ++t) {
            
            //! init the number of deliveries in each cluster
            for(int k=0; k<clustersNb; ++k) {
                clustersDeliveriesNumber[k] = 0;
            }
                        
            //! assign the deliveries to the clusters
            for(int i=0; i<nodes.size(); ++i) {
                int cluster = 0;
                while( clustersDeliveriesNumber[cluster] >= Math.ceil(nodes.size()/clustersNb) ) {
                    cluster++;
                }
                
                for(int k=1; k<clustersNb; ++k) {
                    double deliveryLon = nodes.get(i).getLongitude();
                    double deliveryLat = nodes.get(i).getLatitude();
                    if(clustersCenters[k].distance(deliveryLon, deliveryLat) < clustersCenters[cluster].distance(deliveryLon, deliveryLat)
                            && clustersDeliveriesNumber[k] < Math.ceil(nodes.size()/clustersNb)) {
                        cluster = k;
                    }
                }
                clusters[i] = cluster;
                clustersDeliveriesNumber[cluster]++;
            }
            
            //! reevaluate the clusters centers
            for(int k=0; k<clustersNb; ++k) {
                int LonSum = 0; // Coordinate X
                int LatSum = 0; // Coordinate Y
                for(int i=0; i<nodes.size(); ++i) {
                    if(clusters[i] == k) {
                        LonSum += nodes.get(i).getLongitude();
                        LatSum += nodes.get(i).getLatitude();
                    }
                }
                
                if(clustersDeliveriesNumber[k] != 0) {
                    clustersCenters[k].setLocation(LonSum / clustersDeliveriesNumber[k], 
                                                   LatSum / clustersDeliveriesNumber[k]);
                } else {
                    int randomIndex = (int)(Math.random() * nodes.size());
                    double lon = nodes.get(randomIndex).getLongitude();  // coord x
                    double lat = nodes.get(randomIndex).getLatitude();   // coord y
                    clustersCenters[k] = new Point2D.Double(lon, lat);
                }
            }
        }
        
        return Arrays.stream(clusters).boxed().collect(Collectors.toList());
    }
    
    public static void test() {
        List<Node> nodes = generateNodes(100, 40, 40);
        List<Integer> clusters = kMeans(nodes, 5);
        for(int i=0; i<clusters.size(); ++i) {
            System.out.print(clusters.get(i) + " ; " ); 
            System.out.println(nodes.get(i).getLongitude() + " ; " +
                               nodes.get(i).getLatitude());
        }
    }

    public static void main(String[] args) {
        test();
    }

}
