package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.algorithm.KMeans;
import fr.insa.lyon.pld.agile.model.Node;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.Test;

public class KmeansTest {

    @Test
    public void testClustering() {
        int clusterNb=9;
        int nodesNb=1000;
        int width=100;
        int height=50;
        List<Node> nodes = generateNodes(nodesNb,width,height);

        int[] clusters = KMeans.kMeans(nodes, clusterNb, null);

        for(int i=0; i<clusters.length; ++i) {
                assertTrue(clusters[i]>=0 && clusters[i]<clusterNb);
        }
    }

    private static List<Node> generateNodes(int nodesNb, int spaceHeight, int spaceWidth) {
        List<Node> nodes = new ArrayList<>();
        for(int i=0; i<nodesNb; ++i) {
            double lon = ( (Math.random()-0.5) * spaceWidth ) ;
            double lat = ( (Math.random()-0.5) * spaceHeight ) ;
            nodes.add(new Node (i, lon, lat));
        }

        return nodes;
    }
}
