package org.kclhi.hands.hider.singleshot.distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kclhi.hands.graph.GraphController;
import org.kclhi.hands.graph.StringEdge;
import org.kclhi.hands.graph.StringVertex;

/**
* @author Martin
*
*/
public class GreedyRandomFixedDistance extends RandomFixedDistance {
  
  /**
  * @param graph
  */
  public GreedyRandomFixedDistance(GraphController <StringVertex, StringEdge> graphController, int hideLocations) {
    
    super(graphController, hideLocations);
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#getConnectedEdge(HideAndSeek.graph.StringVertex, java.util.List)
  */
  @Override
  public StringEdge getConnectedEdge(StringVertex currentNode, List<StringEdge> connectedEdges) {
    
    for (StringEdge edge : connectedEdges ) {
      
      if ( uniquelyVisitedNodes().contains(edgeToTarget(edge, currentNode)) ) continue;
      
      return edge;
      
    }
    
    return connectedEdges.get((int)(Math.random() * connectedEdges.size()));
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#getConnectedEdges(HideAndSeek.graph.StringVertex)
  */
  public List<StringEdge> getConnectedEdges(StringVertex currentNode) {
    
    ArrayList<StringEdge> edges = new ArrayList<StringEdge>(super.getConnectedEdges(currentNode));
    
    Collections.sort(edges);
    
    return edges;
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#nextNode(HideAndSeek.graph.StringVertex)
  */
  @Override
  public StringVertex nextNode(StringVertex currentNode) {
    
    return connectedNode(currentNode);
    
  }
  
  /* (non-Javadoc)
  * @see HideAndSeek.GraphTraverser#startNode()
  */
  @Override
  public StringVertex startNode() {
    
    return randomNode();
    
  }
  
}
